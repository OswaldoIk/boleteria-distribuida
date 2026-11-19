package co.edu.umb.boleteria.domain;
import jakarta.persistence.*;
@Entity @DiscriminatorValue("ASIENTO") public class Asiento extends Localidad { protected Asiento(){} public boolean validarDisponibilidad(int n){return n==1&&estado==EstadoLocalidad.DISPONIBLE;} public void reservar(int n){if(!validarDisponibilidad(n))throw new IllegalStateException("Asiento no disponible");estado=EstadoLocalidad.RESERVADO;} public void liberar(int n){if(n!=1)throw new IllegalArgumentException("Cantidad inválida");estado=EstadoLocalidad.DISPONIBLE;} }
