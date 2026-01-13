package sistemaestudiantil.sge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;

@Configuration
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(EstudianteRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByEmail("admin@sge.com").isEmpty()) {
                
                Estudiante admin = new Estudiante();
                admin.setNombres("Super");
                admin.setApellidos("Administrador");
                admin.setCarnet("ADMIN001");
                admin.setEmail("admin@sge.com");
                admin.setContrasenia(passwordEncoder.encode("MiContr@123$"));
                admin.setRol(Roles.ROLE_ADMIN);
                admin.setEstado(EstadoEstudiante.ESTUDIANTE);
                
                repository.save(admin);
                
                logger.info("Usuario ADMINISTRADOR creado exitosamente: admin@sge.com");
            }
        };
    }
}
