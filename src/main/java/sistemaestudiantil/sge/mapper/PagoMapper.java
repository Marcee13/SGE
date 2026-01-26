package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.PagoDTO;
import sistemaestudiantil.sge.model.Pago;

@Component
public class PagoMapper {

    public PagoDTO toDTO(Pago pago) {
        if (pago == null) {
            return null;
        }
        PagoDTO dto = new PagoDTO();
        dto.setIdPago(pago.getIdPago());
        dto.setMonto(pago.getMonto());
        dto.setCodigoPago(pago.getCodigoPago());
        dto.setFechaVencimiento(pago.getFechaVencimiento());
        dto.setFechaPago(pago.getFechaPago());
        dto.setEstado(pago.getEstado());
        dto.setObservaciones(pago.getObservaciones());
        if (pago.getArancel() != null) {
            dto.setNombreArancel(pago.getArancel().getNombre());
        }
        if (pago.getEstudiante() != null) {
            dto.setIdEstudiante(pago.getEstudiante().getIdEstudiante());
        }
        return dto;
    }

    public Pago toEntity(PagoDTO dto) {
        if (dto == null) {
            return null;
        }
        Pago pago = new Pago();
        pago.setIdPago(dto.getIdPago());
        pago.setCodigoPago(dto.getCodigoPago());
        pago.setMonto(dto.getMonto());
        pago.setFechaVencimiento(dto.getFechaVencimiento());
        pago.setFechaPago(dto.getFechaPago());
        pago.setEstado(dto.getEstado());
        pago.setObservaciones(dto.getObservaciones());
        return pago;
    }
}