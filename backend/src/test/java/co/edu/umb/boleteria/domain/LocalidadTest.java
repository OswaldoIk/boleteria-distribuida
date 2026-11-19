package co.edu.umb.boleteria.domain;
import org.junit.jupiter.api.Test;import java.lang.reflect.*;import static org.junit.jupiter.api.Assertions.*;
class LocalidadTest {
 private <T>T nueva(Class<T> c)throws Exception{var ctor=c.getDeclaredConstructor();ctor.setAccessible(true);T x=ctor.newInstance();for(Class<?> k=c;k!=null;k=k.getSuperclass())for(Field f:k.getDeclaredFields()){f.setAccessible(true);switch(f.getName()){case "estado"->f.set(x,EstadoLocalidad.DISPONIBLE);case "cuposDisponibles"->f.set(x,10);case "capacidadTotal"->f.set(x,10);}}return x;}
 @Test void asientoRespetaCantidadYEstados()throws Exception{Asiento a=nueva(Asiento.class);assertTrue(a.validarDisponibilidad(1));a.reservar(1);assertEquals(EstadoLocalidad.RESERVADO,a.getEstado());assertFalse(a.validarDisponibilidad(1));a.liberar(1);assertEquals(EstadoLocalidad.DISPONIBLE,a.getEstado());assertThrows(IllegalStateException.class,()->a.reservar(2));}
 @Test void aforoDescuentaAgotaYLibera()throws Exception{LocalidadGeneral g=nueva(LocalidadGeneral.class);g.cuposDisponibles=2;g.capacidadTotal=2;g.reservar(2);assertEquals(0,g.getCuposDisponibles());assertEquals(EstadoLocalidad.AGOTADO,g.getEstado());g.liberar(1);assertEquals(1,g.getCuposDisponibles());assertEquals(EstadoLocalidad.DISPONIBLE,g.getEstado());assertThrows(IllegalArgumentException.class,()->g.liberar(2));}
}
