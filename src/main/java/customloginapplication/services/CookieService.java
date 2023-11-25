package customloginapplication.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CookieService {

    public String extractUsernameFromToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    log.info("{}", token);
                    return token;
                }
            }
        }
        throw new NullPointerException("Куки 'jwtToken' не знайдено");
    }
    public Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60 * 30);
        return cookie;
    }
}
