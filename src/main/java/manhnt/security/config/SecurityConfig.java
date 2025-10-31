package manhnt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers("/auth/logout").permitAll()
                                .requestMatchers("/auth/**").permitAll()

//                .requestMatchers("/users/**").permitAll() // cho tất các method quả đi qua
                                // chặn theo method và url
//                        .requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasAnyAuthority("USER", "ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/users").hasAuthority("ADMIN")

                                .anyRequest().authenticated()// các API khác cần login
                )
                //
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // +-----------------------

    /*
     * Là một AuthenticationProvider mặc định của Spring Security, dùng để xác thực người dùng qua username/password bằng cách:
     * Gọi UserDetailsService để tải thông tin người dùng (từ DB, memory, v.v.)
     * Dùng PasswordEncoder để so sánh mật khẩu đã mã hóa
     * */

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        //
        return daoAuthenticationProvider;
    }


    /*
     * Spring Boot dùng AuthenticationConfiguration để tự động cấu hình AuthenticationManager — bao gồm:
     * Đăng ký các AuthenticationProvider bạn đã định nghĩa (như DaoAuthenticationProvider ở trên)
     * Cấu hình xử lý exception, parent manager, v.v.
     * → Khi bạn inject AuthenticationManager vào service và gọi .authenticate(), chính bean này sẽ được dùng.
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
    /*
     * 🔗 Mối quan hệ
     * AuthenticationManager sử dụng DaoAuthenticationProvider (và các provider khác) để xác thực.
     * DaoAuthenticationProvider cần UserDetailsService + PasswordEncoder để hoạt động.
     * ✅ Tóm tắt 1 dòng
     * DaoAuthenticationProvider: Xác thực user bằng username/password từ DB.
     * AuthenticationManager: Điểm truy cập chính để kích hoạt quá trình xác thực.
     */

    //+-----------------
}
