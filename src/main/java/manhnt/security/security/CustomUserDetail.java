package manhnt.security.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomUserDetail implements UserDetails {
    private manhnt.security.entity.User user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
        * Nếu dùng : hasAnyRole thì phải thêm ROLE_
        * Spring Security tự động thêm tiền tố "ROLE_" vào các role khi so sánh.
        * Nghĩa là nếu bạn viết hasRole("ADMIN"), thì Spring sẽ kiểm tra "ROLE_ADMIN"*/
//        return List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().toString()));
        /*
        * Nếu dùng : hasAnyAuthority
        * */
        return List.of(new SimpleGrantedAuthority(user.getRole().toString()));

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // default fun này chưa dùng tới

    @Override
    public boolean isAccountNonExpired() {
        return true; // có thể xử lý logic riêng nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
