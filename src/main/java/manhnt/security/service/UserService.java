package manhnt.security.service;

import lombok.RequiredArgsConstructor;
import manhnt.security.entity.User;
import manhnt.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return
                userRepository.findByUsername(username).orElseThrow(
                        () -> new RuntimeException("User not found!")
                );
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        userRepository.delete(user);

    }

    @Override
    public User save(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    @Override
    public User update(User req) {
        User user = userRepository.findById(req.getId()).orElseThrow(() -> new RuntimeException("User not found!"));
        user.setUsername(req.getUsername());
//        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        return userRepository.save(user);
    }
}
