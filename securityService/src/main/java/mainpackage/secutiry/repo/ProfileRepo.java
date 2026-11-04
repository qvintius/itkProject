package mainpackage.secutiry.repo;

import mainpackage.secutiry.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<Profile, Long> {
    public Optional<Profile> findByEmail(String email);

}
