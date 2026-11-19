package co.edu.umb.boleteria.repository;
import co.edu.umb.boleteria.domain.Localidad;import jakarta.persistence.LockModeType;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import java.util.*;
public interface LocalidadRepository extends JpaRepository<Localidad,UUID>{@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select l from Localidad l where l.id=:id") Optional<Localidad> findByIdWithPessimisticLock(@Param("id") UUID id);}
