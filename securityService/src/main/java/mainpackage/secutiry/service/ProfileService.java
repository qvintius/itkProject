package mainpackage.secutiry.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mainpackage.secutiry.dto.PersonSaveEventFlow;
import mainpackage.secutiry.dto.PersonSaveEventStream;
import mainpackage.secutiry.dto.ProfileKeycloakDto;
import mainpackage.secutiry.repo.ProfileRepo;
import mainpackage.secutiry.domain.Profile;
import mainpackage.secutiry.dto.ProfileCredentialsDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    public final ProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;
    public final ModelMapper mapper;
    public final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void save(ProfileCredentialsDto profileDto) {
        if (profileRepo.findByEmail(profileDto.getEmail()).isPresent())//проверка уникальности нового email
            throw new EntityExistsException("Пользователь с таким email уже существует");
        Profile profile = mapper.map(profileDto, Profile.class);//сохранение в бд email, password + зашифрованного пароля
        profile.setEncodedPassword(passwordEncoder.encode(profile.getPassword()));
        profile = profileRepo.save(profile);
        PersonSaveEventStream eventStream = mapper.map(profile, PersonSaveEventStream.class);
        PersonSaveEventFlow eventFlow = new PersonSaveEventFlow(profile.getId(), LocalDate.now());
        //eventPublisher.publishEvent(profile);
//        eventPublisher.publishEvent(eventStream);
        eventPublisher.publishEvent(eventFlow);
    }

    @Transactional
    public void save(ProfileKeycloakDto profileDto) {
        if (profileRepo.findByEmail(profileDto.getEmail()).isPresent())//проверка уникальности нового email
            throw new EntityExistsException("Пользователь с таким email уже существует");
        Profile profile = new Profile().builder().name(profileDto.getName())
                .keycloakId(UUID.fromString(profileDto.getKeycloakId()))
                .email(profileDto.getEmail()).build();
        profile.setEncodedPassword(passwordEncoder.encode(profile.getPassword()));
        profile = profileRepo.save(profile);
        PersonSaveEventStream eventStream = mapper.map(profile, PersonSaveEventStream.class);
        PersonSaveEventFlow eventFlow = new PersonSaveEventFlow(profile.getId(), LocalDate.now());
        //eventPublisher.publishEvent(profile);
//        eventPublisher.publishEvent(eventStream);
        eventPublisher.publishEvent(eventFlow);
    }

    @SneakyThrows
    private Profile findByCredentials(ProfileCredentialsDto profileCredentialsDto) {
        Optional<Profile> userOpt = profileRepo.findByEmail(profileCredentialsDto.getEmail());
        if (userOpt.isPresent()) {
            Profile profile = userOpt.get();
            if (passwordEncoder.matches(profileCredentialsDto.getPassword(), profile.getEncodedPassword()))
                return profile;
        }//если пользователь не найден по email или пароли не совпадают, то ошибка
        throw new AuthenticationException("Некорректный эмейл или пароль");
    }

    private Profile findByEmail(String email) {
        return profileRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Пользователь с таким эмайлом не найден"));
    }


}
