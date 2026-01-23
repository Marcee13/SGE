# SGE - Sistema de Gestión Estudiantil

## 📋 Descripción del Proyecto

**SGE** (Sistema de Gestión Estudiantil) es una aplicación backend desarrollada con **Spring Boot 4.0.1** que proporciona una plataforma integral para la gestión académica y administrativa de una institución educativa.

### Propósito Principal
Facilitar la administración de:
- Estudiantes y profesores
- Carreras y asignaturas
- Inscripciones y evaluaciones
- Pagos y aranceles
- Reportes y estadísticas académicas
- Autenticación y seguridad

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework**: Spring Boot 4.0.1
- **Lenguaje**: Java 21
- **Build Tool**: Maven
- **Base de Datos**: PostgreSQL
- **ORM**: JPA/Hibernate

### Seguridad
- **Spring Security**: Autenticación y autorización
- **JWT (JSON Web Tokens)**: Tokens para sesiones
  - Librería: jjwt (v0.11.5)
  - Algoritmo: HS256
  - Duración: 10 horas (36,000,000 ms)

### Librerías Adicionales
- **Lombok**: Reducción de código boilerplate
- **OpenPDF**: Generación de reportes en PDF
- **SpringDoc OpenAPI**: Documentación automática de API (Swagger/OpenAPI)
- **Spring Data JPA**: Acceso a datos
- **Spring Boot Actuator**: Monitoreo y métricas

---

## 📁 Estructura del Proyecto

```
SGE/
├── src/
│   ├── main/
│   │   ├── java/sistemaestudiantil/sge/
│   │   │   ├── SgeApplication.java          # Clase principal
│   │   │   ├── config/                      # Configuración
│   │   │   │   ├── DataInitializer.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/                  # Controladores REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── EstudianteController.java
│   │   │   │   ├── ProfesorController.java
│   │   │   │   ├── CarreraController.java
│   │   │   │   ├── AsignaturaController.java
│   │   │   │   ├── GrupoController.java
│   │   │   │   ├── InscripcionController.java
│   │   │   │   ├── EvaluacionController.java
│   │   │   │   ├── PagoController.java
│   │   │   │   ├── ArancelController.java
│   │   │   │   ├── FacultadController.java
│   │   │   │   ├── AdministrativoController.java
│   │   │   │   ├── EstadisticaController.java
│   │   │   │   └── ReporteController.java
│   │   │   ├── service/                     # Lógica de negocio
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── EstudianteService.java
│   │   │   │   ├── ProfesorService.java
│   │   │   │   ├── CarreraService.java
│   │   │   │   ├── AsignaturaService.java
│   │   │   │   ├── GrupoService.java
│   │   │   │   ├── InscripcionService.java
│   │   │   │   ├── EvaluacionService.java
│   │   │   │   ├── PagoService.java
│   │   │   │   ├── ArancelService.java
│   │   │   │   ├── FacultadService.java
│   │   │   │   ├── AdministrativoService.java
│   │   │   │   ├── EstadisticaService.java
│   │   │   │   ├── ReporteService.java
│   │   │   │   └── StorageService.java      # Gestión de archivos
│   │   │   ├── model/                       # Entidades JPA
│   │   │   │   ├── Estudiante.java
│   │   │   │   ├── Profesor.java
│   │   │   │   ├── Carrera.java
│   │   │   │   ├── Asignatura.java
│   │   │   │   ├── Grupo.java
│   │   │   │   ├── Inscripcion.java
│   │   │   │   ├── Evaluacion.java
│   │   │   │   ├── Pago.java
│   │   │   │   ├── Arancel.java
│   │   │   │   ├── Facultad.java
│   │   │   │   └── Ciclo.java
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   │   ├── EstudianteDTO.java
│   │   │   │   ├── LoginDTO.java
│   │   │   │   ├── CambioContraseniaDTO.java
│   │   │   │   └── ... (otros DTOs)
│   │   │   ├── repository/                 # Repositorios (acceso a datos)
│   │   │   ├── mapper/                     # Mapeo de entidades a DTOs
│   │   │   ├── response/                   # Respuestas API
│   │   │   │   ├── ApiResponse.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── security/                   # Configuración de seguridad
│   │   │   ├── enums/                      # Enumeraciones
│   │   │   │   ├── EstadoEstudiante.java
│   │   │   │   ├── EstadoInscripcion.java
│   │   │   │   └── EstadoPago.java
│   │   │   └── exceptions/                 # Excepciones personalizadas
│   │   └── resources/
│   │       └── application.properties      # Configuración de la aplicación
│   └── test/                               # Tests
├── pom.xml                                 # Dependencias Maven
├── mvnw, mvnw.cmd                          # Maven Wrapper
└── uploads/                                # Directorio para almacenamiento de archivos
```

---

## 🔑 Características Principales

### 1. **Autenticación y Autorización**
- Sistema de login para estudiantes y profesores
- Generación de tokens JWT
- Cambio de contraseña
- Seguridad basada en roles

**Endpoints:**
- `POST /api/auth/login` - Login de estudiantes
- `POST /api/auth/login-profesor` - Login de profesores
- `POST /api/auth/cambiar-password` - Cambiar contraseña

### 2. **Gestión de Estudiantes**
- CRUD completo de estudiantes
- Carga de documentos (foto, cedula, etc.)
- Estados de estudiante (activo, inactivo, egresado)
- Historial académico y kardex

### 3. **Gestión Académica**
- Carreras y facultades
- Asignaturas y grupos
- Inscripciones a materias
- Evaluaciones y calificaciones
- Ciclos académicos

### 4. **Gestión de Pagos**
- Aranceles y matrículas
- Registro de pagos
- Comprobantes de pago
- Estados de pago

### 5. **Reportes y Estadísticas**
- Reportes académicos (kardex, récord académico)
- Estadísticas generales
- Generación de PDF
- Dashboards

### 6. **Gestión Administrativa**
- Gestión de administradores
- Gestión de profesores
- Almacenamiento de archivos

---

## ⚙️ Configuración

### Base de Datos
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql:uri_database
spring.datasource.username=postgres
spring.datasource.password=your_pass
spring.jpa.hibernate.ddl-auto=update
```

### JWT
```properties
jwt.secret=EXAMPLE_KEY_TOKEN
jwt.expiration=36000000  # 10 horas
```

### Almacenamiento de Archivos
```properties
storage.location=uploads
```

---

## 📡 Endpoints Principales

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login de estudiante |
| POST | `/api/auth/login-profesor` | Login de profesor |
| POST | `/api/auth/cambiar-password` | Cambiar contraseña |

### Estudiantes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/estudiantes` | Listar todos los estudiantes |
| GET | `/api/estudiantes/{id}` | Obtener estudiante por ID |
| POST | `/api/estudiantes` | Crear nuevo estudiante |
| PUT | `/api/estudiantes/{id}` | Actualizar estudiante |
| DELETE | `/api/estudiantes/{id}` | Eliminar estudiante |
| POST | `/api/estudiantes/{id}/upload` | Cargar documentos |

### Profesores
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/profesores` | Listar profesores |
| POST | `/api/profesores` | Crear profesor |
| PUT | `/api/profesores/{id}` | Actualizar profesor |

### Carreras
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/carreras` | Listar carreras |
| POST | `/api/carreras` | Crear carrera |
| PUT | `/api/carreras/{id}` | Actualizar carrera |

### Asignaturas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/asignaturas` | Listar asignaturas |
| POST | `/api/asignaturas` | Crear asignatura |
| PUT | `/api/asignaturas/{id}` | Actualizar asignatura |

### Inscripciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/inscripciones` | Listar inscripciones |
| POST | `/api/inscripciones` | Crear inscripción |
| PUT | `/api/inscripciones/{id}` | Actualizar inscripción |

### Evaluaciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/evaluaciones` | Listar evaluaciones |
| POST | `/api/evaluaciones` | Registrar evaluación |
| PUT | `/api/evaluaciones/{id}` | Actualizar evaluación |

### Pagos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/pagos` | Listar pagos |
| POST | `/api/pagos` | Registrar pago |
| GET | `/api/pagos/estudiante/{id}` | Pagos por estudiante |

### Reportes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/kardex/{id}` | Kardex de estudiante |
| GET | `/api/reportes/record/{id}` | Récord académico |
| GET | `/api/reportes/constancia/{id}` | Constancia de matrícula |

### Estadísticas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/estadisticas/dashboard` | Dashboard con métricas |
| GET | `/api/estadisticas/estudiantes` | Estadísticas de estudiantes |

---

## 🔐 Enumeraciones (Estados)

### EstadoEstudiante
- `ACTIVO` - Estudiante activo
- `INACTIVO` - Estudiante inactivo
- `EGRESADO` - Estudiante egresado
- `SUSPENDIDO` - Estudiante suspendido

### EstadoInscripcion
- `ACTIVA` - Inscripción activa
- `COMPLETADA` - Inscripción completada
- `CANCELADA` - Inscripción cancelada

### EstadoPago
- `PENDIENTE` - Pago pendiente
- `PAGADO` - Pago realizado
- `VENCIDO` - Pago vencido
- `CANCELADO` - Pago cancelado

---

## 📦 Dependencias Principales

| Dependencia | Versión | Propósito |
|-------------|---------|----------|
| Spring Boot | 4.0.1 | Framework principal |
| Spring Security | Latest | Seguridad |
| Spring Data JPA | Latest | Acceso a datos |
| PostgreSQL Driver | Runtime | Base de datos |
| jjwt-api | 0.11.5 | JWT tokens |
| OpenPDF | 1.3.30 | Generación de PDF |
| Lombok | Latest | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.5 | Documentación API |

---

## 🚀 Ejecutar la Aplicación

### Requisitos Previos
- Java 21 instalado
- PostgreSQL instalado y ejecutándose
- Base de datos `sge_db` creada

### Pasos
1. **Clonar el repositorio**
   ```bash
   git clone <url-repositorio>
   cd SGE
   ```

2. **Configurar la base de datos**
   - Crear base de datos: `CREATE DATABASE sge_db;`
   - Verificar credenciales en `application.properties`

3. **Compilar y ejecutar**
   ```bash
   # Con Maven Wrapper (Windows)
   mvnw.cmd clean package
   mvnw.cmd spring-boot:run
   
   # O con Maven instalado
   mvn clean package
   mvn spring-boot:run
   ```

4. **Acceder a la API**
   - URL base: `http://localhost:8080`
   - Documentación Swagger: `http://localhost:8080/swagger-ui.html`


## 📊 Estructura de Datos

### Entidades Principales

#### Estudiante
- ID, Cédula, Nombres, Apellidos
- Email, Teléfono, Dirección
- Fecha de nacimiento, Sexo
- Estado (ACTIVO, INACTIVO, EGRESADO, SUSPENDIDO)
- Carrera, Facultad
- Documentos adjuntos

#### Profesor
- ID, Cédula, Nombres, Apellidos
- Email, Teléfono
- Especialidad
- Departamento/Facultad

#### Carrera
- ID, Nombre, Código
- Descripción
- Facultad
- Duración (ciclos)
- Aranceles

#### Asignatura
- ID, Nombre, Código
- Descripción
- Créditos, Horas
- Carrera, Ciclo
- Prerequisitos

#### Grupo
- ID, Código
- Asignatura
- Profesor
- Aula, Horario
- Capacidad
- Ciclo académico

#### Inscripción
- ID, Estudiante, Grupo
- Fecha de inscripción
- Estado (ACTIVA, COMPLETADA, CANCELADA)
- Calificación final

#### Evaluación
- ID, Inscripción
- Tipo (Parcial, Examen Final)
- Calificación
- Fecha

#### Pago
- ID, Estudiante
- Monto, Concepto
- Fecha de pago
- Estado (PENDIENTE, PAGADO, VENCIDO)

---

## 🔧 Configuración de Desarrollo

### IDE Recomendado
- IntelliJ IDEA (Community o Ultimate)
- Visual Studio Code con extensiones Java

### Extensiones Recomendadas (VS Code)
- Extension Pack for Java
- Spring Boot Extension Pack
- Lombok Annotations Support for VS Code
- REST Client

### Configuración de Lombok
Asegúrese de habilitar la anotación de procesamiento en su IDE para que Lombok funcione correctamente.

---

## 🐛 Manejo de Excepciones

La aplicación implementa excepciones personalizadas para casos específicos:

- `RecursoNoencontradoException` - Cuando un recurso no existe
- `CredencialesInvalidasException` - Login fallido
- `ContraseniaDebilException` - Contraseña no cumple requisitos
- `EntidadYaExisteException` - Intento de crear un recurso duplicado

---

## 📝 Notas Importantes

1. **Seguridad JWT**: Cambie la clave secreta (`jwt.secret`) en producción
2. **Base de Datos**: Asegúrese de que PostgreSQL esté en ejecución antes de iniciar
3. **Almacenamiento**: El directorio `uploads/` se crea automáticamente
4. **Logs**: Revise los logs para debugging y monitoreo
5. **CORS**: Verifique la configuración de CORS si accede desde un frontend diferente

---

## 📞 Contacto y Soporte

Para reportar problemas o solicitar mejoras, contacte al equipo de desarrollo.

---

**Versión**: 0.0.1-SNAPSHOT  
**Fecha de Actualización**: 23 de enero de 2026  
**Estado**: En Desarrollo

