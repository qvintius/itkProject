package mainpackage.secutiry;

import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@ComponentScan("mainpackage")
@PropertySources({@PropertySource("classpath:application.properties")})
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**", "/test", "/login").permitAll()
                        .requestMatchers("/**").authenticated())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.permitAll())
                .formLogin(form -> form.loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)))
                        .failureHandler((request, response, exception) -> {
                            exception.printStackTrace();
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        .permitAll())
                .oauth2Login(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.authenticationProvider(daoAuthProvider());
        ;
        return http.build();
    }

    @Bean
    public AuthenticationProvider daoAuthProvider(CustomUserService customUserService) {//собственный провайдер
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

}
