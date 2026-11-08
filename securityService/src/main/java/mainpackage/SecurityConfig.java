package mainpackage;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mainpackage.secutiry.AuthoritiesConverter;
import mainpackage.secutiry.CustomUserService;
import mainpackage.secutiry.KeycloakJwtAuthConverter;
import mainpackage.secutiry.repo.ProfileRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Stream;

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
                //.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll()
                        .requestMatchers("/secured").hasRole("USER")
                        .requestMatchers("/**").authenticated())

                .logout(LogoutConfigurer::permitAll)

//                .oauth2Login(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
                        userInfo -> userInfo.userAuthoritiesMapper(userAuthoritiesMapper())))
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))

        ;
        return http.build();
    }

//    @Bean
//    public AuthenticationProvider daoAuthProvider(CustomUserService customUserService) {//собственный провайдер
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

//    private JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtAuthConverter());
//        return converter;
//    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mapped = new HashSet<>(authorities);
            for (GrantedAuthority authority : authorities) {
                if (authority instanceof OidcUserAuthority oidc) {
                    Object rolesClaim = oidc.getIdToken().getClaim("roles");

                    if (rolesClaim instanceof Collection<?> roles) {
                        roles.stream()
                                .map(Object::toString)
                                .map(SimpleGrantedAuthority::new)
                                .forEach(mapped::add);
                    }
                }
            }

            return mapped;
        };
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new AuthoritiesConverter());
        return converter;
    }

//    @Autowired
//    private JwtConverter jwtAuthenticationConverter;

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
////        converter.setPrincipalClaimName("preferred_username");
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
//            List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
//            return Stream.concat(authorities.stream(),
//                    roles.stream()
//                            .filter(role -> role.startsWith("ROLE_"))
//                            .map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast)).toList();
//        });
//        return converter;
//    }


//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
////        converter.setPrincipalClaimName("preffered_username");
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
//            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//            if (realmAccess != null) {
//                List<String> realmRoles = (List<String>) realmAccess.get("roles");
//                if (realmRoles != null) {
//                    realmRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                            .forEach(authorities::add);
//                }
//            }
//            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//            if (resourceAccess != null) {
//                resourceAccess.forEach((clientId, clientAccess) -> {
//                    Map<String, Object> client = (Map<String, Object>) clientAccess;
//                    List<String> clientRoles = (List<String>) client.get("roles");
//                    if (clientRoles != null) {
//                        clientRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                                .forEach(authorities::add);
//                    }
//                });
//            }
//            return authorities;
//        });
//        return converter;
//    }

}
