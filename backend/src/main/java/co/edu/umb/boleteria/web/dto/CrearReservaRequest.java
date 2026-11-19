package co.edu.umb.boleteria.web.dto;
import jakarta.validation.constraints.*;import java.util.UUID;
public record CrearReservaRequest(@NotNull UUID localidadId,@NotBlank @Size(max=100) String usuarioId,@Min(1) int cantidad){}
