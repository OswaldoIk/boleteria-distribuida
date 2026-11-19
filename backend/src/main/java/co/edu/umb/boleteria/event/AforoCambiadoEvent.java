package co.edu.umb.boleteria.event;
import co.edu.umb.boleteria.domain.*;import java.util.UUID;
public record AforoCambiadoEvent(UUID localidadId,EstadoLocalidad estado,Integer cuposDisponibles){}
