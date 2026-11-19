package co.edu.umb.boleteria.repository;
import co.edu.umb.boleteria.domain.AuditoriaTransaccion;import org.springframework.data.jpa.repository.JpaRepository;import java.util.UUID;
public interface AuditoriaRepository extends JpaRepository<AuditoriaTransaccion,UUID>{}
