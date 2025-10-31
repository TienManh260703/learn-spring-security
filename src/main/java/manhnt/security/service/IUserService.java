package manhnt.security.service;

import manhnt.security.entity.User;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    User findByUsername(String username);
    void deleteById(Long  id);
    User save(User user);
    User update(User user);
}
