package sistemaestudiantil.sge.service;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;

import jakarta.servlet.http.HttpServletResponse;
import sistemaestudiantil.sge.dto.CicloKardexDTO;
import sistemaestudiantil.sge.dto.ComprobanteDTO;
import sistemaestudiantil.sge.dto.DetalleComprobanteDTO;
import sistemaestudiantil.sge.dto.MateriaKardexDTO;
import sistemaestudiantil.sge.dto.RecordAcademicoDTO;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Inscripcion;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.InscripcionRepository;

@Service
public class ReporteService {
    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;

    public ReporteService(InscripcionRepository inscripcionRepository, EstudianteRepository estudianteRepository){
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public ComprobanteDTO obtenerDatosComprobante(Long idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado."));

        List<Inscripcion> listaInscripciones = inscripcionRepository.findHistorialCompleto(idEstudiante).stream().filter(i -> i.getEstadoInscripcion() == EstadoInscripcion.INSCRITO).toList();

        ComprobanteDTO dto = new ComprobanteDTO();
        dto.setCarnet(estudiante.getCarnet());
        dto.setEstudiante(estudiante.getNombres() + " " + estudiante.getApellidos());
        dto.setCarrera(estudiante.getCarrera() != null ? estudiante.getCarrera().getNombreCarrera() : "N/A");
        dto.setFechaGeneracion(LocalDate.now());

        if (!listaInscripciones.isEmpty()) {
            dto.setCiclo(listaInscripciones.get(0).getGrupo().getCiclo().getNombre());
        } else {
            dto.setCiclo("N/A");
        }

        List<DetalleComprobanteDTO> detalles = new ArrayList<>();
        for (Inscripcion i : listaInscripciones) {
            DetalleComprobanteDTO det = new DetalleComprobanteDTO();
            det.setCodigo(i.getGrupo().getAsignatura().getCodigo());
            det.setAsignatura(i.getGrupo().getAsignatura().getName());
            det.setGrupo(i.getGrupo().getCodigoGrupo());
            det.setDias(i.getGrupo().getDias());
            det.setHorario(i.getGrupo().getHoraInicio() + " - " + i.getGrupo().getHoraFin());
            detalles.add(det);
        }
        dto.setMaterias(detalles);

        return dto;
    }

    public void generarBoletaPdf(HttpServletResponse response, Long idEstudiante) throws DocumentException, IOException {

        ComprobanteDTO datos = obtenerDatosComprobante(idEstudiante);

        Document document = new Document(PageSize.LETTER);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph titulo = new Paragraph("Comprobante de Inscripción", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));

            Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Carnet: " + datos.getCarnet(), fontDatos));
            document.add(new Paragraph("Estudiante: " + datos.getEstudiante(), fontDatos));
            document.add(new Paragraph("Carrera: " + datos.getCarrera(), fontDatos));
            document.add(new Paragraph("Ciclo: " + datos.getCiclo(), fontDatos));
            document.add(new Paragraph("Fecha: " + datos.getFechaGeneracion(), fontDatos));
            document.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[] {1.5f, 4f, 1.5f, 2f, 2f});

            agregarCabecera(tabla, "CÓDIGO");
            agregarCabecera(tabla, "ASIGNATURA");
            agregarCabecera(tabla, "GRUPO");
            agregarCabecera(tabla, "DÍAS");
            agregarCabecera(tabla, "HORARIO");

            for (DetalleComprobanteDTO materia : datos.getMaterias()) {
                tabla.addCell(materia.getCodigo());
                tabla.addCell(materia.getAsignatura());
                tabla.addCell(materia.getGrupo());
                tabla.addCell(materia.getDias());
                tabla.addCell(materia.getHorario());
            }

            document.add(tabla);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Este documento es un comprobante oficial de inscripción.", 
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private void agregarCabecera(PdfPTable tabla, String titulo) {
        PdfPCell cell = new PdfPCell(new Phrase(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        tabla.addCell(cell);
    }

    public RecordAcademicoDTO obtenerDatosKardex(Long idEstudiante) {
        Estudiante estudiante = estudianteRepository.findById(idEstudiante).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));
        List<Inscripcion> historial = inscripcionRepository.findHistorialConNotas(idEstudiante);

        double sumaPuntos = 0.0;
        int totalUVParaCum = 0;
        int totalUVAprobadas = 0;

        Map<String, List<MateriaKardexDTO>> mapaCiclos = new LinkedHashMap<>();

        for (Inscripcion ins : historial) {
            Asignatura asig = ins.getGrupo().getAsignatura();
            Double nota = ins.getNotaFinal();
            String estado = ins.getEstadoInscripcion().toString();

            if (ins.getEstadoInscripcion() == EstadoInscripcion.APROBADO) {
                sumaPuntos += (nota * asig.getUv());
                totalUVParaCum += asig.getUv();
                totalUVAprobadas += asig.getUv();
            }

            MateriaKardexDTO materiaDTO = new MateriaKardexDTO(
                asig.getCodigo(),
                asig.getName(),
                asig.getUv(),
                nota,
                estado,
                1 
            );

            String nombreCiclo = ins.getGrupo().getCiclo().getNombre();
            mapaCiclos.computeIfAbsent(nombreCiclo, k -> new ArrayList<>()).add(materiaDTO);
        }

        Double cum = (totalUVParaCum > 0) ? (sumaPuntos / totalUVParaCum) : 0.0;
        cum = Math.round(cum * 100.0) / 100.0;

        RecordAcademicoDTO recordAcademico = new RecordAcademicoDTO();
        recordAcademico.setCarnet(estudiante.getCarnet());
        recordAcademico.setNombreCompleto(estudiante.getNombres() + " " + estudiante.getApellidos());
        recordAcademico.setCarrera(estudiante.getCarrera() != null ? estudiante.getCarrera().getNombreCarrera() : "N/A");
        recordAcademico.setCumActual(cum);
        recordAcademico.setUvAprobadas(totalUVAprobadas);

        List<CicloKardexDTO> listaCiclos = new ArrayList<>();
        for (Map.Entry<String, List<MateriaKardexDTO>> entry : mapaCiclos.entrySet()) {
            CicloKardexDTO cicloDTO = new CicloKardexDTO();
            cicloDTO.setNombreCiclo(entry.getKey());
            cicloDTO.setMaterias(entry.getValue());
            listaCiclos.add(cicloDTO);
        }
        recordAcademico.setCiclos(listaCiclos);

        return recordAcademico;
    }

    public void generarKardexPdf(HttpServletResponse response, Long idEstudiante) throws DocumentException, IOException {
        RecordAcademicoDTO recordAcademico = obtenerDatosKardex(idEstudiante);
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fontRoja = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.RED);

        Paragraph titulo = new Paragraph("RÉCORD ACADÉMICO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Estudiante: " + recordAcademico.getNombreCompleto(), fontNormal));
        document.add(new Paragraph("Carnet: " + recordAcademico.getCarnet(), fontNormal));
        document.add(new Paragraph("Carrera: " + recordAcademico.getCarrera(), fontNormal));
        document.add(new Paragraph("CUM GLOBAL: " + recordAcademico.getCumActual(), fontBold));
        document.add(new Paragraph("UVs Aprobadas: " + recordAcademico.getUvAprobadas(), fontNormal));
        document.add(new Paragraph(" "));

        for (CicloKardexDTO ciclo : recordAcademico.getCiclos()) {

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);
            tabla.setWidths(new float[] {1.5f, 5f, 1f, 1.5f, 2f});

            PdfPCell cellHeader = new PdfPCell(new Phrase(ciclo.getNombreCiclo(), fontBold));
            cellHeader.setColspan(5);
            cellHeader.setBackgroundColor(Color.LIGHT_GRAY);
            tabla.addCell(cellHeader);

            agregarCelda(tabla, "CÓDIGO", fontBold);
            agregarCelda(tabla, "ASIGNATURA", fontBold);
            agregarCelda(tabla, "UV", fontBold);
            agregarCelda(tabla, "NOTA", fontBold);
            agregarCelda(tabla, "ESTADO", fontBold);

            for (MateriaKardexDTO mat : ciclo.getMaterias()) {

                boolean esReprobada = "REPROBADO".equalsIgnoreCase(mat.getEstado());
                Font fuenteActual = esReprobada ? fontRoja : fontNormal;

                agregarCelda(tabla, mat.getCodigo(), fuenteActual);
                agregarCelda(tabla, mat.getNombreAsignatura(), fuenteActual);
                agregarCelda(tabla, String.valueOf(mat.getUv()), fuenteActual);
                agregarCelda(tabla, String.valueOf(mat.getNotaFinal()), fuenteActual);
                agregarCelda(tabla, mat.getEstado(), fuenteActual);
            }
            document.add(tabla);
        }

        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Fin del reporte.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void agregarCelda(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        tabla.addCell(cell);
    }

    public void generarKardexPdfPorUsuario(HttpServletResponse response, String username) throws DocumentException, IOException {
        Estudiante estudiante = estudianteRepository.findByCarnet(username).orElseThrow(() -> new RecursoNoencontradoException("No se encontró un estudiante asociado al usuario: " + username));
        generarKardexPdf(response, estudiante.getIdEstudiante());
    }

    public ComprobanteDTO obtenerDatosComprobantePorUsuario(String username) {
        Estudiante estudiante = estudianteRepository.findByCarnet(username).orElseThrow(() -> new RecursoNoencontradoException("Usuario no encontrado: " + username));
        
        return obtenerDatosComprobante(estudiante.getIdEstudiante());
    }

    public void generarBoletaPdfPorUsuario(HttpServletResponse response, String username) throws DocumentException, IOException {
        Estudiante estudiante = estudianteRepository.findByCarnet(username).orElseThrow(() -> new RecursoNoencontradoException("Usuario no encontrado: " + username));
        
        generarBoletaPdf(response, estudiante.getIdEstudiante());
    }
}
