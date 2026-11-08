package mainpackage.secutiry.controller;

import jakarta.validation.Valid;
import mainpackage.secutiry.dto.AuthDto;
import mainpackage.secutiry.dto.ProfileCredentialsDto;
import mainpackage.secutiry.dto.ProfileKeycloakDto;
import mainpackage.secutiry.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String resourceUrl;
    @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}")
    private String grantType;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String secretKey;

    private final ProfileService profileService;

    @Autowired
    public AuthController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> registration(@RequestBody @Valid ProfileCredentialsDto profile) {
        profileService.save(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
    @PostMapping("/registrationKeycloak")
    public ResponseEntity<Void> registration(@RequestBody ProfileKeycloakDto profile) {
        profileService.save(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/sign-in")
    public String signIn(@RequestBody AuthDto authDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "clientId=" + clientId +
                "&username=" + authDto.getLogin() +
                "&password=" + authDto.getPassword() +
                "&grant_type=" + grantType +
                "&secret_key=" + secretKey;
        HttpEntity entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.exchange(resourceUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().value()==200)
            return response.getBody().toString();
        return null;
//        Profile profile = profileService.signIn(profileCredentialsDto);
////        return ResponseEntity.ok().body(HttpStatus.OK);
//        return new ResponseEntity<>(HttpStatus.OK);
    }




}
