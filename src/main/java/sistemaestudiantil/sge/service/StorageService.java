package sistemaestudiantil.sge.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import sistemaestudiantil.sge.exceptions.AlmacenamientoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;

@Service
public class StorageService {
    @Value("${storage.location}")
    private String storageLocation;

    private Path rootLocation;

    private final List<String> extensionesPermitidas=Arrays.asList("jpg", "jpeg", "png", "pdf", "docx");

    @PostConstruct
    public void init() {
        try {
            if(storageLocation==null||storageLocation.trim().isEmpty()){
                throw new AlmacenamientoException("La ubicación de almacenamiento no está configurada correctamente.");
            }
            this.rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new AlmacenamientoException("No se pudo inicializar la carpeta de almacenamiento.", e);
        }
    }

    public String almacenarArchivo(MultipartFile file) {

        if (file.isEmpty()) {
            throw new OperacionNoPermitidaException("Error: El archivo enviado está vacío.");
        }

        String rawFilename = file.getOriginalFilename();
        if (rawFilename == null) {
            throw new OperacionNoPermitidaException("Error: El archivo no tiene nombre válido.");
        }

        String originalFilename = StringUtils.cleanPath(rawFilename);

        if (originalFilename.contains("..")) {
            throw new OperacionNoPermitidaException("Error de seguridad: El nombre del archivo contiene secuencias inválidas.");
        }

        String extension = obtenerExtension(originalFilename);
        
        if (extension.isEmpty() || !extensionesPermitidas.contains(extension.toLowerCase().replace(".", ""))) {
            throw new OperacionNoPermitidaException("Tipo de archivo no permitido. Solo se aceptan: " + extensionesPermitidas);
        }

        try {
            String nombreFinal = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(nombreFinal).normalize();
            
            if (!destinationFile.getParent().equals(this.rootLocation)) {
                throw new AlmacenamientoException("No se puede almacenar el archivo fuera del directorio actual.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return nombreFinal;

        } catch (IOException e) {
            throw new AlmacenamientoException("Fallo al guardar el archivo.", e);
        }
    }

    public Resource cargarComoRecurso(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RecursoNoencontradoException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RecursoNoencontradoException("Error al resolver la ruta del archivo: " + filename);
        }
    }
    
    private String obtenerExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
