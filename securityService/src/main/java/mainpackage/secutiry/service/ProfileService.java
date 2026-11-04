package mainpackage.secutiry.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import mainpackage.secutiry.repo.ProfileRepo;
import mainpackage.secutiry.domain.Profile;
import mainpackage.secutiry.dto.ProfileCredentialsDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
public class ProfileService {

    public final ProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;
    public final ModelMapper mapper;

    @Autowired
    public ProfileService(ProfileRepo profileRepo, PasswordEncoder passwordEncoder, ModelMapper mapper) {
        this.profileRepo = profileRepo;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public void save(ProfileCredentialsDto profileDto) {
        if (profileRepo.findByEmail(profileDto.getEmail()).isPresent())//проверка уникальности нового email
            throw new EntityExistsException("Пользователь с таким email уже существует");
        Profile profile = mapper.map(profileDto, Profile.class);//сохранение в бд email, password + зашифрованного пароля
        profile.setEncodedPassword(passwordEncoder.encode(profile.getPassword()));
        profile = profileRepo.save(profile);
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
