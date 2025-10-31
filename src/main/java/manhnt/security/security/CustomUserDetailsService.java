package manhnt.security.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manhnt.security.entity.User;
import manhnt.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            User user = optional.get();
            log.info("user {} found", user);
            CustomUserDetail customUserDetail = new CustomUserDetail();
            customUserDetail.setUser(user);
            log.info("user detail {} found", customUserDetail);
            return customUserDetail;

        }
        throw new UsernameNotFoundException("User not found");
    }
}
