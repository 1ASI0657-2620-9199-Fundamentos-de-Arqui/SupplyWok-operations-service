package aurora.supply_wok.platform.shared.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public final class TestSecurityHelper {

    public static final String TEST_SECRET = "test-secret-key-with-enough-length-for-context-loading-12345678901234567890";

    private TestSecurityHelper() {
    }

    public static String createToken(Long userId, String email, List<String> roles) {
        var key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
