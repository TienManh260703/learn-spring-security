package manhnt.security.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import manhnt.security.entity.User;
import manhnt.security.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("users")
//@CrossOrigin
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    IUserService userService;

    @GetMapping
    private ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /*
     * #userName → tham số method (tên biến trong Java).
     * authentication.principal.username → tên người dùng đang đăng nhập.
     * Nếu userName != principal.username → ném AccessDeniedException → trả về 403.
     */
    @GetMapping("{user_name}")
//    @PreAuthorize("#userName == authentication.principal.username")
    private ResponseEntity<User> getUser(@PathVariable(name = "user_name") String userName) {
        return ResponseEntity.ok(userService.findByUsername(userName));
    }

    @PostMapping
    private ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @PutMapping
    private ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }
}
