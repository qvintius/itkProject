package mainpackage.secutiry.controller;

import jakarta.validation.Valid;
import mainpackage.secutiry.dto.ProfileCredentialsDto;
import mainpackage.secutiry.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

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

//    @PostMapping("/sign-in")
//    public ResponseEntity<Void> signIn(@RequestBody ProfileCredentialsDto profileCredentialsDto) {
//        Profile profile = profileService.signIn(profileCredentialsDto);
////        return ResponseEntity.ok().body(HttpStatus.OK);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }




}
