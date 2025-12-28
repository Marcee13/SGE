package sistemaestudiantil.sge.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.PagoDTO;
import sistemaestudiantil.sge.enums.EstadoPago;
import sistemaestudiantil.sge.enums.TipoArancel;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Arancel;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Pago;
import sistemaestudiantil.sge.repository.ArancelRepository;
import sistemaestudiantil.sge.repository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ArancelRepository arancelRepository;

    public PagoService(PagoRepository pagoRepository,ArancelRepository arancelRepository){
        this.arancelRepository=arancelRepository;
        this.pagoRepository=pagoRepository;
    }

    @Transactional
    public Pago generarPagoExamen(Estudiante estudiante, TipoArancel tipo){
        String codigoBuscar="";
        switch (tipo) {
            case EXAMEN_ADMISION:
                codigoBuscar="EX-ADM";
                break;
            case EXAMEN_CONOCIMIENTOS:
                codigoBuscar="EX-CON";
                break;
            default:
                throw new OperacionNoPermitidaException("Tipo de examen no válido");
        }

        String codigoFinal=codigoBuscar;
        Arancel arancel=arancelRepository.findByCodigo(codigoFinal).orElseThrow(()->new RecursoNoencontradoException("Arancel no definido o configurado para " +codigoFinal));

        Pago pago = new Pago();
        pago.setEstudiante(estudiante);
        pago.setArancel(arancel);
        pago.setMonto(arancel.getCosto());
        pago.setFechaVencimiento(LocalDate.now().plusDays(15));
        pago.setEstado(EstadoPago.PENDIENTE);

        return pagoRepository.save(pago);
    }

    @Transactional
    public void generarTalonarioAnual(Estudiante estudiante, int anio) {
        List<Pago> talonario = new ArrayList<>();

        Arancel arancelMatricula = arancelRepository.findByCodigo("MATRICULA").orElseThrow(() -> new RuntimeException("Falta configurar precio matrícula"));

        Pago matricula = new Pago();
        matricula.setEstudiante(estudiante);
        matricula.setArancel(arancelMatricula);
        matricula.setMonto(arancelMatricula.getCosto());
        matricula.setFechaVencimiento(LocalDate.of(anio, 1, 31)); 
        matricula.setEstado(EstadoPago.PENDIENTE);
        talonario.add(matricula);
        Arancel arancelMensualidad = arancelRepository.findByCodigo("MENSUALIDAD").orElseThrow(() -> new RuntimeException("Falta configurar precio mensualidad"));

        for (int mes = 1; mes <= 12; mes++) {
            Pago mensualidad = new Pago();
            mensualidad.setEstudiante(estudiante);
            mensualidad.setArancel(arancelMensualidad);
            mensualidad.setMonto(arancelMensualidad.getCosto());

            int diaFinMes = (mes == 2) ? 28 : 30; // Simplificado
            mensualidad.setFechaVencimiento(LocalDate.of(anio, mes, diaFinMes));
            
            mensualidad.setEstado(EstadoPago.PENDIENTE);
            talonario.add(mensualidad);
        }

        pagoRepository.saveAll(talonario);
    }

    public List<PagoDTO> listarPagosPorEstudiante(Long idEstudiante) {
    return pagoRepository.findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(idEstudiante)
            .stream()
            .map(p -> new PagoDTO(
                    p.getIdPago(),
                    p.getEstudiante().getIdEstudiante(),
                    p.getArancel().getNombre(),
                    p.getMonto(),
                    p.getFechaVencimiento(),
                    p.getFechaPago(),
                    p.getEstado()
            ))
            .toList();
    }
}
