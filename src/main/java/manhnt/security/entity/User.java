package manhnt.security.entity;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import manhnt.security.constant.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false,name = "user_name")
    private String username;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;


}
