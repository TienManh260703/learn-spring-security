| Thành phần                | Chức năng                                                             |
| ------------------------- | --------------------------------------------------------------------- |
| **Authentication**        | Đại diện cho người dùng đã đăng nhập (username, password, quyền hạn). |
| **SecurityContextHolder** | Lưu thông tin người dùng hiện tại (Authentication object).            |
| **UserDetailsService**    | Lấy thông tin người dùng từ database.                                 |
| **UserDetails**           | Đối tượng chứa username, password, roles.                             |
| **AuthenticationManager** | Quản lý xác thực – gọi các provider để kiểm tra thông tin.            |
| **PasswordEncoder**       | Mã hóa mật khẩu, so sánh password an toàn.                            |
| **AccessDecisionManager** | Kiểm tra quyền truy cập tài nguyên (ROLE_USER, ROLE_ADMIN...).        |


1️⃣ Client gửi request → ví dụ: POST /login
     ↓
2️⃣ Filter Chain của Spring Security bắt request.
     ↓
3️⃣ Filter tương ứng (VD: UsernamePasswordAuthenticationFilter)
     ↓
4️⃣ Gọi AuthenticationManager.authenticate()
     ↓
5️⃣ Gọi UserDetailsService.loadUserByUsername() → lấy user từ DB
     ↓
6️⃣ So sánh password bằng PasswordEncoder
     ↓
7️⃣ Nếu hợp lệ → tạo Authentication object
     ↓
8️⃣ Lưu Authentication vào SecurityContextHolder
     ↓
9️⃣ Khi truy cập tài nguyên khác (VD: /admin)
     ↓
🔟 Kiểm tra quyền (AccessDecisionManager)
     ↓
✅ Nếu hợp lệ → truy cập Controller
❌ Nếu không → trả 403 Forbidden

+--------------------------------------
🧩 1️⃣ Spring Security – Form Login Flow (session-based)
   [CLIENT: Browser]
          |
          | 1️⃣ Gửi request login (POST /login)
          v
   [Security Filter Chain]
          |
          |--> [UsernamePasswordAuthenticationFilter]
          |        |
          |        | 2️⃣ Lấy username/password từ request
          |        v
          |   [AuthenticationManager.authenticate()]
          |        |
          |        |--> [DaoAuthenticationProvider]
          |                 |
          |                 |--> [UserDetailsService.loadUserByUsername()]
          |                 |       |
          |                 |       |--> Truy vấn DB lấy UserDetails (username, password, roles)
          |                 |
          |                 |--> [PasswordEncoder.matches()]
          |                 |       |
          |                 |       |--> So sánh password nhập với password trong DB
          |                 |
          |                 |--> Nếu đúng → tạo Authentication object
          |                 
          |--> 3️⃣ Lưu Authentication vào SecurityContextHolder
          |--> 4️⃣ Lưu SecurityContext vào HttpSession
          |
          |  (Login thành công)
          |
          |--> 5️⃣ Trả về Response (Set-Cookie: JSESSIONID)
          |
          |====================== Request sau =======================
          |
          | 6️⃣ Client gửi request GET /admin
          |
          |--> [Security Filter Chain]
          |        |
          |        |--> [SessionManagementFilter] (Đọc lại SecurityContext từ session)
          |        |
          |        |--> [FilterSecurityInterceptor]
          |        |       |
          |        |       |--> [AccessDecisionManager]
          |        |       |       |
          |        |       |       |--> Kiểm tra quyền ROLE_ADMIN
          |        |       |
          |        |       |--> Nếu hợp lệ → chuyển đến Controller
          |        |       |--> Nếu không → 403 Forbidden
          |
          v
   [Controller xử lý logic business]
          |
          v
   [Response trả về client]


✅ Đặc điểm của Form Login Flow:

Dựa trên Session (stateful).

SecurityContext lưu trong HttpSession.

Thường dùng cho ứng dụng web truyền thống (Thymeleaf, JSP, ...).

+-----------------------------------------
🔐 2️⃣ Spring Security – JWT Token Flow (stateless)
   [CLIENT: React / Mobile App]
          |
          | 1️⃣ Gửi request login (POST /api/auth/login)
          v
   [Security Filter Chain]
          |
          |--> [UsernamePasswordAuthenticationFilter] (hoặc custom Auth filter)
          |        |
          |        | 2️⃣ Lấy username/password
          |        |--> [AuthenticationManager.authenticate()]
          |        |--> [UserDetailsService.loadUserByUsername()]
          |        |--> [PasswordEncoder.matches()]
          |        |
          |        |--> Nếu thành công → tạo Authentication
          |        |
          |        |--> 3️⃣ Sinh JWT token (chứa username, role, expiration)
          |        |
          |        |--> Trả về token cho client
          |
          v
   [Client lưu JWT token (localStorage / header)]
          |
          |==================== Request tiếp theo ====================
          |
          | 4️⃣ Gửi request GET /api/admin
          |     kèm header: Authorization: Bearer <JWT_TOKEN>
          v
   [Security Filter Chain]
          |
          |--> [JwtAuthenticationFilter]
          |        |
          |        | 5️⃣ Đọc header Authorization
          |        |--> Tách token
          |        |--> Giải mã token (validate signature, expiration)
          |        |--> Nếu hợp lệ → tạo Authentication
          |        |--> Gắn vào SecurityContextHolder
          |
          |--> [FilterSecurityInterceptor]
          |        |
          |        |--> [AccessDecisionManager]
          |        |       |
          |        |       |--> Kiểm tra quyền ROLE_ADMIN
          |        |       |
          |        |       |--> Nếu hợp lệ → qua Controller
          |        |       |--> Nếu không → 403 Forbidden
          |
          v
   [Controller xử lý logic business]
          |
          v
   [Response JSON trả về client]


✅ Đặc điểm của JWT Flow:

Stateless – không dùng session.

SecurityContext chỉ tồn tại trong request hiện tại.

Token JWT lưu ở client (tránh session fix).

Phù hợp cho REST API, SPA, Mobile app.
