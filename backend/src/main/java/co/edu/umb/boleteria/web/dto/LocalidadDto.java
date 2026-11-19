package co.edu.umb.boleteria.web.dto;
import co.edu.umb.boleteria.domain.Localidad;import java.math.BigDecimal;import java.util.UUID;
public record LocalidadDto(UUID id,String nombre,String tipo,String codigoSilla,Integer capacidadTotal,Integer cuposDisponibles,BigDecimal precio,String estado){public static LocalidadDto of(Localidad l){return new LocalidadDto(l.getId(),l.getNombre(),l.getTipo(),l.getCodigoSilla(),l.getCapacidadTotal(),l.getCuposDisponibles(),l.getPrecio(),l.getEstado().name());}}
