package sistemaestudiantil.sge.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.ArancelDTO;
import sistemaestudiantil.sge.mapper.ArancelMapper;
import sistemaestudiantil.sge.model.Arancel;
import sistemaestudiantil.sge.repository.ArancelRepository;

@Service
public class ArancelService {
    private final ArancelRepository arancelRepository;
    private final ArancelMapper mapper;

    public ArancelService(ArancelRepository arancelRepository, ArancelMapper mapper) {
        this.arancelRepository = arancelRepository;
        this.mapper=mapper;
    }

    @Transactional
    public ArancelDTO guardarArancel(ArancelDTO dto) {
        Arancel arancel = arancelRepository.findByCodigo(dto.getCodigo()).orElse(new Arancel());

        arancel.setCodigo(dto.getCodigo());
        arancel.setNombre(dto.getNombre());
        arancel.setCosto(dto.getCosto());

        Arancel guardado = arancelRepository.save(arancel);
        
        return mapper.toDTO(guardado);
    }
}
