package co.edu.umb.boleteria.web.dto;
import co.edu.umb.boleteria.domain.Reserva;import java.time.Instant;import java.util.UUID;
public record ReservaDto(UUID reservaId,UUID localidadId,int cantidad,String estado,Instant fechaExpiracion,String nodoAtiende){public static ReservaDto of(Reserva r,String nodo){return new ReservaDto(r.getId(),r.getLocalidad().getId(),r.getCantidad(),r.getEstado().name(),r.getFechaExpiracion(),nodo);}}
