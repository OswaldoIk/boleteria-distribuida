package co.edu.umb.boleteria.config;
import org.springframework.beans.factory.annotation.Value;import org.springframework.context.annotation.*;import org.springframework.security.config.Customizer;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.core.userdetails.*;import org.springframework.security.provisioning.InMemoryUserDetailsManager;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(@Value("${app.devops-user}") String name,@Value("${app.devops-password-hash}") String hash){return new InMemoryUserDetailsManager(User.withUsername(name).password(hash).roles("DEVOPS").build());}
 @Bean SecurityFilterChain filter(HttpSecurity h)throws Exception{return h.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/v1/cluster/**").hasRole("DEVOPS").requestMatchers("/ws/**","/api/**","/","/**").permitAll()).httpBasic(Customizer.withDefaults()).build();}
}
