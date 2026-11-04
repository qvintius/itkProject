package mainpackage.secutiry;

import mainpackage.secutiry.repo.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//получение CustomUserDetails пользователя из бд
@Service
public class CustomUserService implements UserDetailsService {

    private final ProfileRepo profileRepo;

    @Autowired
    public CustomUserService(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails customUserDetails = profileRepo.findByEmail(username).map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return customUserDetails;
    }


}
