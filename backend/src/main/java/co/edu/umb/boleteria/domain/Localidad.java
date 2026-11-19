package co.edu.umb.boleteria.domain;
import jakarta.persistence.*;import java.math.BigDecimal;import java.util.UUID;
@Entity @Table(name="localidad") @Inheritance(strategy=InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="tipo_localidad",discriminatorType=DiscriminatorType.STRING,length=20)
public abstract class Localidad {
 @Id @GeneratedValue(strategy=GenerationType.UUID) @Column(name="id_localidad") protected UUID id;
 @Column(nullable=false,length=100) protected String nombre;
 @Column(name="tipo_localidad",insertable=false,updatable=false) protected String tipo;
 @Column(name="codigo_silla",length=20) protected String codigoSilla;
 @Column(name="capacidad_total") protected Integer capacidadTotal;
 @Column(name="cupos_disponibles") protected Integer cuposDisponibles;
 @Column(nullable=false,precision=10,scale=2) protected BigDecimal precio;
 @Enumerated(EnumType.STRING) @Column(nullable=false) protected EstadoLocalidad estado=EstadoLocalidad.DISPONIBLE;
 @Version @Column(nullable=false) protected int version;
 protected Localidad(){}
 public abstract boolean validarDisponibilidad(int cantidad); public abstract void reservar(int cantidad); public abstract void liberar(int cantidad);
 public UUID getId(){return id;} public String getNombre(){return nombre;} public String getTipo(){return tipo;} public String getCodigoSilla(){return codigoSilla;} public Integer getCapacidadTotal(){return capacidadTotal;} public Integer getCuposDisponibles(){return cuposDisponibles;} public BigDecimal getPrecio(){return precio;} public EstadoLocalidad getEstado(){return estado;}
}
