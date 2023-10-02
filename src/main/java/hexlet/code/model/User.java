package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Email
    @EqualsAndHashCode.Include
    private String email;

    @NotBlank
    @Size(min = 1, message = "Имя должно быть не менее 1 символа")
    @EqualsAndHashCode.Include
    private String firstName;

    @NotBlank
    @Size(min = 1, message = "Фамилия должна быть не менее 1 символа")
    @EqualsAndHashCode.Include
    private String lastName;

    @JsonIgnore
    @NotBlank
    @Size(min = 3, max = 255, message = "Пароль должен быть от 3 до 255 символов")
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @EqualsAndHashCode.Include
    private Date createdAt;

    public User(final Long id) {
        this.id = id;
    }
}
