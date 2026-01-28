package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.CicloDTO;
import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.TipoExamenAdmision;
import sistemaestudiantil.sge.model.Ciclo;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.AdministrativoService;
import sistemaestudiantil.sge.service.EvaluacionService;
import sistemaestudiantil.sge.service.InscripcionService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdministrativoController {
    private final InscripcionService inscripcionService;
    private final AdministrativoService administrativoService;
    private final EvaluacionService evaluacionService;

    public AdministrativoController(InscripcionService inscripcionService, AdministrativoService administrativoService, EvaluacionService evaluacionService){
        this.inscripcionService=inscripcionService;
        this.administrativoService=administrativoService;
        this.evaluacionService=evaluacionService;
    }

    @PostMapping("/cierre-ciclo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> ejecutarCierre(@RequestParam Long idCiclo){
        String resultado=inscripcionService.cerrarCiclo(idCiclo);
        ApiResponse<String> respuesta=new ApiResponse<>(
            "Proceso finalizado.", 
            resultado, 
            true);
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }

    @PostMapping("/cierre-admision")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<List<EstudianteDTO>>> cerrarAdmision(@RequestParam Integer anioIngreso) {
        
        List<EstudianteDTO> nuevosEstudiantes = inscripcionService.cerrarCicloAdmision(anioIngreso);
        
        ApiResponse<List<EstudianteDTO>> respuesta = new ApiResponse<>(
            "Cierre de admisión ejecutado correctamente. Se han generado carnets y usuarios.",
            nuevosEstudiantes,
            true
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PostMapping("/generar-oferta/{idCiclo}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> generarOferta(@PathVariable Long idCiclo) {
        
        String resultado = administrativoService.generarOfertaAcademica(idCiclo);
        
        ApiResponse<String> respuesta = new ApiResponse<>(
            "Proceso de generación de oferta completado.",
            resultado,
            true
        );

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PostMapping("/crear-ciclo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<Ciclo>> crearCiclo(@RequestBody CicloDTO dto) {
        
        Ciclo nuevo = administrativoService.crearCiclo(dto);
        
        ApiResponse<Ciclo> respuesta = new ApiResponse<>(
            "Ciclo creado correctamente.", 
            nuevo, 
            true
        );

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<Ciclo>> activarCiclo(@PathVariable Long id) {
        
        Ciclo ciclo = administrativoService.activarCiclo(id);
        
        ApiResponse<Ciclo> respuesta = new ApiResponse<>(
            "El ciclo " + ciclo.getNombre() + " es ahora el ACTIVO.", 
            ciclo, 
            true
        );

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<List<Ciclo>>> listarCiclos() {
        
        List<Ciclo> listaCiclos = administrativoService.listarTodos();
        
        ApiResponse<List<Ciclo>> respuesta = new ApiResponse<>(
            "Lista de ciclos.", 
            listaCiclos, 
            true
        );

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PatchMapping("/{id}/calificar-admision")
    public ResponseEntity<ApiResponse<EstudianteDTO>> calificarAspirante(@PathVariable Long id, @RequestParam Double nota, @RequestParam TipoExamenAdmision tipo){
        EstudianteDTO resultado = evaluacionService.calificarAspirante(id, nota, tipo);

        String mensaje="";
        
        if(resultado.getEstado()==EstadoEstudiante.SELECCIONADO){
            mensaje="El aspirante ha aprobado la prueba de conocimientos generales.";
        }else if(resultado.getEstado()==EstadoEstudiante.REPROBADO){
            mensaje="El aspirante ha reprobado el examen de conocimiento específicos. No puede continuar el proceso de ingreso.";
        }else if(resultado.getEstado()==EstadoEstudiante.CONDICIONADO){
            mensaje="El aspirante ha alcanzado el puntaje de aprobacion de la prueba de conocimiento generales. Puedo continuar con el proceso correspondiente a carreras de profesorado.";
        }else{
            mensaje="El aspirante no ha alcanzado el puntaje de aprobacion de la prueba de conocimientos generales. Debe realizar la prueba de conocimiento específicos.";
        }

        return new ResponseEntity<>(new ApiResponse<>(mensaje, resultado, true),HttpStatus.OK);
    }

    @PostMapping("/{id}/formalizar")
    public ResponseEntity<EstudianteDTO> formalizarIngreso(@PathVariable Long id) {
        EstudianteDTO resultado = inscripcionService.formalizarInscripcion(id);
        return ResponseEntity.ok(resultado);
    }
}
