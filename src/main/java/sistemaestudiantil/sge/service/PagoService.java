package sistemaestudiantil.sge.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.CorteDiarioDTO;
import sistemaestudiantil.sge.dto.DetalleCorteDTO;
import sistemaestudiantil.sge.dto.PagoDTO;
import sistemaestudiantil.sge.enums.EstadoPago;
import sistemaestudiantil.sge.enums.TipoArancel;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.PagoMapper;
import sistemaestudiantil.sge.model.Arancel;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Pago;
import sistemaestudiantil.sge.repository.ArancelRepository;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ArancelRepository arancelRepository;
    private final PagoMapper pagoMapper;
    private final EstudianteRepository estudianteRepository;

    public PagoService(PagoRepository pagoRepository, PagoMapper pagoMapper, ArancelRepository arancelRepository, EstudianteRepository estudianteRepository){
        this.arancelRepository=arancelRepository;
        this.pagoMapper=pagoMapper;
        this.pagoRepository=pagoRepository;
        this.estudianteRepository=estudianteRepository;
    }

    @Transactional
    public Pago generarPagoExamen(Estudiante estudiante, TipoArancel tipo){
        String codigoBuscar="";
        int codigoTipoParaNPE=0;
        switch (tipo) {
            case EXAMEN_ADMISION:
                codigoBuscar="EX-ADM";
                codigoTipoParaNPE=21;
                break;
            case EXAMEN_CONOCIMIENTOS:
                codigoBuscar="EX-CON";
                codigoTipoParaNPE=22;
                break;
            default:
                throw new OperacionNoPermitidaException("Tipo de examen no válido");
        }

        String codigoFinal=codigoBuscar;
        Arancel arancel=arancelRepository.findByCodigo(codigoFinal).orElseThrow(()->new RecursoNoencontradoException("Arancel no definido o configurado para " +codigoFinal));

        Pago pago = new Pago();
        pago.setEstudiante(estudiante);
        pago.setArancel(arancel);
        int anioActual = LocalDate.now().getYear();
        pago.setCodigoPago(generarNPE(anioActual, codigoTipoParaNPE, estudiante.getIdEstudiante()));
        pago.setMonto(arancel.getCosto());
        pago.setFechaVencimiento(LocalDate.now().plusDays(15));
        pago.setEstado(EstadoPago.PENDIENTE);

        return pagoRepository.save(pago);
    }

    @Transactional
    public void generarTalonarioAnual(Estudiante estudiante, int anio) {

        if (pagoRepository.existeTalonario(estudiante.getIdEstudiante(), anio)) {
            throw new OperacionNoPermitidaException(
                "El estudiante ya tiene generado el talonario para el año " + anio
            );
        }

        List<Pago> talonario = new ArrayList<>();

        Arancel arancelMatricula = arancelRepository.findByCodigo("MATRICULA").orElseThrow(() -> new RecursoNoencontradoException("Falta configurar el arancel MATRICULA"));

        Arancel arancelMensualidad = arancelRepository.findByCodigo("MENSUALIDAD").orElseThrow(() -> new RecursoNoencontradoException("Falta configurar el arancel MENSUALIDAD"));

        Pago matricula = new Pago();
        matricula.setEstudiante(estudiante);
        matricula.setArancel(arancelMatricula);
        matricula.setCodigoPago(generarNPE(anio, 0, estudiante.getIdEstudiante()));
        matricula.setMonto(arancelMatricula.getCosto());
        matricula.setFechaVencimiento(LocalDate.of(anio, 1, 31)); 
        matricula.setEstado(EstadoPago.PENDIENTE);
        talonario.add(matricula);

        for (int mes = 1; mes <= 12; mes++) {
            Pago mensualidad = new Pago();
            mensualidad.setEstudiante(estudiante);
            mensualidad.setArancel(arancelMensualidad);
            mensualidad.setMonto(arancelMensualidad.getCosto());
            mensualidad.setCodigoPago(generarNPE(anio, mes, estudiante.getIdEstudiante()));

            int diaFinMes = LocalDate.of(anio,mes,1).lengthOfMonth();
            mensualidad.setFechaVencimiento(LocalDate.of(anio, mes, diaFinMes));
            
            mensualidad.setEstado(EstadoPago.PENDIENTE);
            talonario.add(mensualidad);
        }

        pagoRepository.saveAll(talonario);
    }

    @Transactional
    public PagoDTO registrarPago(Long codigoPago) {
        Pago pago = pagoRepository.findByCodigoPago(codigoPago).orElseThrow(() -> new RecursoNoencontradoException("No se encontró el recibo con código: " + codigoPago));

        if (pago.getEstado() == EstadoPago.ANULADO) {
            throw new OperacionNoPermitidaException("No se puede cobrar un recibo que ha sido ANULADO.");
        }

        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new OperacionNoPermitidaException("Este recibo ya fue pagado el día: " + pago.getFechaPago());
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(pago.getFechaVencimiento())) {
            Arancel arancelMora = arancelRepository.findByCodigo("MORA").orElseThrow(() -> new RecursoNoencontradoException("No hay arancel de MORA configurado"));

            BigDecimal recargo;

            if(Boolean.TRUE.equals(arancelMora.getEsPorcentaje())){
                BigDecimal porcentaje = arancelMora.getCosto().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
                recargo = pago.getMonto().multiply(porcentaje);
            } else {
                recargo = arancelMora.getCosto();
            }

            BigDecimal nuevoTotal=pago.getMonto().add(recargo).setScale(2,RoundingMode.HALF_UP);

            pago.setMonto(nuevoTotal);

            pago.setObservaciones("Recargo aplicado: $" + recargo.setScale(2, RoundingMode.HALF_UP));
        }

        pago.setFechaPago(hoy);
        pago.setEstado(EstadoPago.PAGADO);

        Pago pagoRealizado = pagoRepository.save(pago);

        return pagoMapper.toDTO(pagoRealizado);
    }

    //listar TODOS los pagos por estudiante ordenados por fecha de vencimiento y año opcional
    public List<PagoDTO> listarPagosPorEstudiante(String identificador, Integer anio) {
        Estudiante estudiante = estudianteRepository.findByCarnetOrEmail(identificador, identificador).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado con el identificador: " + identificador));
        List<Pago> pagos;
        if (anio != null) {
            pagos = pagoRepository.findByEstudianteAndAnio(estudiante.getIdEstudiante(), anio);
        } else {
            pagos = pagoRepository.findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(estudiante.getIdEstudiante());
        }
        return pagos.stream()
                .map(pagoMapper::toDTO)
                .toList();
    }

    //listar todos los pagos PENDIENTES por estudiante ordenados por fecha de vencimiento
    public List<PagoDTO> listarPendientes(String identificador) {
        Estudiante estudiante = estudianteRepository.findByCarnetOrEmail(identificador, identificador).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado con el identificador: " + identificador));
        return pagoRepository.findByEstudiante_IdEstudianteAndEstadoOrderByFechaVencimientoAsc(estudiante.getIdEstudiante(), EstadoPago.PENDIENTE)
                .stream()
                .map(pagoMapper::toDTO)
                .toList();
    }

    private Long generarNPE(int anio, int codigoTipo, Long estudianteId) {
        String codigoStr = String.format("%d%02d%06d", anio, codigoTipo, estudianteId);
        return Long.parseLong(codigoStr);
    }

    @Transactional
    public void anularPago(Long codigoPago, String motivo, boolean regenerarDeuda) {
        Pago pagoOriginal = pagoRepository.findByCodigoPago(codigoPago).orElseThrow(() -> new RecursoNoencontradoException("Pago no encontrado: " + codigoPago));

        if (pagoOriginal.getEstado() == EstadoPago.ANULADO) {
            throw new OperacionNoPermitidaException("El pago ya está anulado.");
        }

        pagoOriginal.setEstado(EstadoPago.ANULADO);
        pagoOriginal.setObservaciones("ANULADO: " + motivo + " | Fecha: " + LocalDate.now());

        pagoRepository.save(pagoOriginal);

        if (regenerarDeuda) {
            Pago nuevoPago = new Pago();

            nuevoPago.setEstudiante(pagoOriginal.getEstudiante());
            nuevoPago.setArancel(pagoOriginal.getArancel());

            nuevoPago.setMonto(pagoOriginal.getArancel().getCosto()); 
            
            nuevoPago.setFechaVencimiento(pagoOriginal.getFechaVencimiento());
            nuevoPago.setEstado(EstadoPago.PENDIENTE);
            
            int anio = pagoOriginal.getFechaVencimiento().getYear();
            int mesOriginal = pagoOriginal.getFechaVencimiento().getMonthValue();

            int codigoTipoCorreccion=mesOriginal+50;
            Long estudianteId=pagoOriginal.getEstudiante().getIdEstudiante();

            nuevoPago.setCodigoPago(generarNPE(anio, codigoTipoCorreccion, estudianteId));
            nuevoPago.setObservaciones("Reposición del pago anulado: " + codigoPago);
            
            pagoRepository.save(nuevoPago);
        }
    }

    public CorteDiarioDTO generarCorteDiario(LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();

        List<Object[]> resumenDatos = pagoRepository.obtenerResumenPorFecha(fecha);
        List<DetalleCorteDTO> detalles = new ArrayList<>();
        BigDecimal totalRecaudado = BigDecimal.ZERO;
        Long totalPagos = 0L;

        for (Object[] fila : resumenDatos) {
            String concepto = (String) fila[0];
            BigDecimal total = (BigDecimal) fila[1];
            Long cantidad = (Long) fila[2];

            detalles.add(new DetalleCorteDTO(concepto, total, cantidad));

            totalRecaudado = totalRecaudado.add(total);
            totalPagos += cantidad;
        }

        CorteDiarioDTO corteDiario = new CorteDiarioDTO();
        corteDiario.setFecha(fecha);
        corteDiario.setTotalIngresos(totalRecaudado);
        corteDiario.setTotalTransacciones(totalPagos);
        corteDiario.setDesglose(detalles);

        return corteDiario;
    }

    public boolean estaSolvente(Long idEstudiante, String codigoArancel) {
    return pagoRepository.existsByEstudiante_IdEstudianteAndArancel_CodigoAndEstado(
            idEstudiante, 
            codigoArancel, 
            EstadoPago.PAGADO
    );
}
}
