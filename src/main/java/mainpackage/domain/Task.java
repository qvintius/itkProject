package mainpackage.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "person_id")
    private Person person;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatusTask status;

    @PrePersist
    public void onCreate() {
        if (status == null)
            status = StatusTask.NEW;
    }

}
