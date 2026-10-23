package mainpackage.repo;

import mainpackage.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepo extends JpaRepository<Person, Long> {

    @Query("""
            select p from Person p left join fetch p.tasks where p.id = :id
            """)
    public Optional<Person> findById(@Param("id") Long id);

    public Optional<Person> findByEmail(@Param("email") String email);

}
