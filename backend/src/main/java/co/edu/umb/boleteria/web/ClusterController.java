package co.edu.umb.boleteria.web;
import co.edu.umb.boleteria.service.ClusterService;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/v1/cluster") public class ClusterController {final ClusterService s;public ClusterController(ClusterService s){this.s=s;}@GetMapping("/estado") public Map<String,Object> estado(){return s.estado();}}
