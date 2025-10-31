package manhnt.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import manhnt.security.dto.LoginRequest;
import manhnt.security.security.CustomUserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            log.info("Authentication Success: {}", authentication.toString());
            log.info("Authentication Details: {}", authentication.getDetails());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // ⚠️ Ghi vào session hiện tại
            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
            log.info("Session Attribute: {}", session.getAttribute("SPRING_SECURITY_CONTEXT"));
            CustomUserDetail userDetail = authentication.getPrincipal() == null ? null : (CustomUserDetail) authentication.getPrincipal();
            return ResponseEntity.ok(userDetail);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    //
    @PostMapping("/loginv2")
    public ResponseEntity<?> loginv2(@RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 1️⃣ Xác thực thông tin username/password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 2️⃣ Tạo SecurityContext và lưu Authentication vào đó
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 3️⃣ Lưu context này vào HttpSession (rất quan trọng)
            new HttpSessionSecurityContextRepository()
                    .saveContext(context, request, response);

            // 4️⃣ Trả kết quả về client
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            return ResponseEntity.ok(userDetail);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
}
