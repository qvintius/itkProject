package mainpackage.secutiry;

import mainpackage.secutiry.domain.Profile;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//соотношение пароля, username и ролей сущности Profile
public record CustomUserDetails(Profile profile) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return profile.getEncodedPassword();
    }

    @Override
    public String getUsername() {
        return profile.getEmail();
    }


}
