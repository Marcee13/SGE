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
import sistemaestudiantil.sge.mapper.PagoMapper;
import sistemaestudiantil.sge.model.Arancel;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Pago;
import sistemaestudiantil.sge.repository.ArancelRepository;
import sistemaestudiantil.sge.repository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ArancelRepository arancelRepository;
    private final PagoMapper pagoMapper;

    public PagoService(PagoRepository pagoRepository, PagoMapper pagoMapper, ArancelRepository arancelRepository){
        this.arancelRepository=arancelRepository;
        this.pagoMapper=pagoMapper;
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

        Arancel arancelMatricula = arancelRepository.findByCodigo("MATRICULA").orElseThrow(() -> new RecursoNoencontradoException("Falta configurar precio MATRICULA"));

        Pago matricula = new Pago();
        matricula.setEstudiante(estudiante);
        matricula.setArancel(arancelMatricula);
        matricula.setMonto(arancelMatricula.getCosto());
        matricula.setFechaVencimiento(LocalDate.of(anio, 1, 31)); 
        matricula.setEstado(EstadoPago.PENDIENTE);
        talonario.add(matricula);

        Arancel arancelMensualidad = arancelRepository.findByCodigo("MENSUALIDAD").orElseThrow(() -> new RecursoNoencontradoException("Falta configurar precio MENSUALIDAD"));

        for (int mes = 1; mes <= 12; mes++) {
            Pago mensualidad = new Pago();
            mensualidad.setEstudiante(estudiante);
            mensualidad.setArancel(arancelMensualidad);
            mensualidad.setMonto(arancelMensualidad.getCosto());

            int diaFinMes = LocalDate.of(anio,mes,1).lengthOfMonth();
            mensualidad.setFechaVencimiento(LocalDate.of(anio, mes, diaFinMes));
            
            mensualidad.setEstado(EstadoPago.PENDIENTE);
            talonario.add(mensualidad);
        }

        pagoRepository.saveAll(talonario);
    }

    public List<PagoDTO> listarPagosPorEstudiante(Long idEstudiante) {
    return pagoRepository.findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(idEstudiante)
            .stream()
            .map(pagoMapper::toDTO)
            .toList();
    }

    @Transactional
    public PagoDTO registrarPago(Long idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RecursoNoencontradoException("No se encontró el recibo con ID: " + idPago));

        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new OperacionNoPermitidaException("Este recibo ya fue pagado el día: " + pago.getFechaPago());
        }

        pago.setFechaPago(LocalDate.now());
        pago.setEstado(EstadoPago.PAGADO);

        Pago pagoRealizado = pagoRepository.save(pago);

        return pagoMapper.toDTO(pagoRealizado);
    }

    public List<PagoDTO> listarPendientes(Long idEstudiante) {
        return pagoRepository.findByEstudiante_IdEstudianteAndEstadoOrderByFechaVencimientoAsc(idEstudiante, EstadoPago.PENDIENTE)
                .stream()
                .map(pagoMapper::toDTO)
                .toList();
    }
}
