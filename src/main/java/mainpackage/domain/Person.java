package mainpackage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mainpackage.secutiry.domain.Profile;

import java.util.List;

@Table(name = "profile")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String encodedPassword;
//    @OneToOne(cascade = CascadeType.REMOVE)
//    @JoinColumn(name = "profile_id", referencedColumnName = "id")
//    private Profile profile;

    @JsonIgnore
    @OneToMany(mappedBy = "person")
    private List<Task> tasks;

}
