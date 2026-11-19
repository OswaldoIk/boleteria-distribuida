package co.edu.umb.boleteria;

import co.edu.umb.boleteria.domain.Localidad;
import co.edu.umb.boleteria.config.SecurityConfig;
import co.edu.umb.boleteria.repository.LocalidadRepository;
import co.edu.umb.boleteria.service.ClusterService;
import co.edu.umb.boleteria.service.ReservaFacade;
import co.edu.umb.boleteria.service.ReservaService;
import co.edu.umb.boleteria.web.*;
import co.edu.umb.boleteria.web.dto.ReservaDto;
import co.edu.umb.boleteria.web.excepciones.SinDisponibilidadException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({LocalidadController.class, ReservaController.class, ClusterController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties="APP_DEVOPS_PASSWORD_HASH=$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
class ApiWebTest {
    @Autowired MockMvc mvc;
    @MockBean LocalidadRepository localidades;
    @MockBean ReservaFacade facade;
    @MockBean ReservaService reservaService;
    @MockBean ClusterService clusterService;

    @Test void cuerpoInvalidoResponde400() throws Exception {
        mvc.perform(post("/api/v1/reservas").contentType("application/json").content("{\"cantidad\":0}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDACION"));
    }
    @Test void localidadAusenteResponde404() throws Exception {
        when(localidades.findById(any(UUID.class))).thenReturn(Optional.empty());
        mvc.perform(get("/api/v1/localidades/00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("NO_ENCONTRADA"));
    }
    @Test void faltaRolDevopsResponde401() throws Exception {
        mvc.perform(get("/api/v1/cluster/estado")).andExpect(status().isUnauthorized());
    }
    @Test @WithMockUser(roles="DEVOPS") void devopsPuedeConsultarCluster() throws Exception {
        when(clusterService.estado()).thenReturn(Map.of("nodos", java.util.List.of(), "replicacion", java.util.List.of()));
        mvc.perform(get("/api/v1/cluster/estado")).andExpect(status().isOk());
    }
    @Test void indisponibilidadResponde409() throws Exception {
        when(facade.crear(any(UUID.class),anyString(),anyInt())).thenThrow(new SinDisponibilidadException());
        mvc.perform(post("/api/v1/reservas").contentType("application/json").content("{\"localidadId\":\"00000000-0000-0000-0000-000000000001\",\"usuarioId\":\"u\",\"cantidad\":1}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.error").value("SIN_DISPONIBILIDAD"));
    }
    @Test void bloqueoAgotadoResponde503ConRetryAfter() throws Exception {
        when(facade.crear(any(UUID.class),anyString(),anyInt())).thenThrow(new CannotAcquireLockException("lock"));
        mvc.perform(post("/api/v1/reservas").contentType("application/json").content("{\"localidadId\":\"00000000-0000-0000-0000-000000000001\",\"usuarioId\":\"u\",\"cantidad\":1}"))
                .andExpect(status().isServiceUnavailable()).andExpect(header().string("Retry-After","1"));
    }
}
