package co.edu.umb.boleteria.service;
import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;
@Component public class ExpiracionReservasJob {final ReservaService s;public ExpiracionReservasJob(ReservaService s){this.s=s;}@Scheduled(fixedDelayString="${app.reservas.expiracion-scan-ms:30000}") public void ejecutar(){s.expirarVencidas();}}
