package me.study.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.study.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);

    }

    /**
     * 토큰 생성
     * @param expiry 만료시간
     * @param user 사용자 정보
     * @return
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 type : JWT
                .setIssuer(jwtProperties.getIssuer())   // iss : 토큰발급자, 프로퍼티값
                .setIssuedAt(now)                       // iat : 토큰발급시간, 현재시간
                .setExpiration(expiry)                  // exp : 토큰만료시간, expiry 변수값
                .setSubject(user.getEmail())            // sub : 토큰제목, 유저 이메일
                .claim("id", user.getId())        // 클레임 ID
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명, HS256 암호화
                .compact();
    }

    /**
     * JWT 토큰 유효성 체크
     * @param token
     * @return
     */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())    // 비밀키로 복호화
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) { // 복호화 오류시 유효하지 않은 토큰으로 간주함
            return false;
        }
    }

    /**
     * 토큰기반으로 인증정보 갖고오기
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities)
                , token, authorities);
    }

    /**
     * 토큰 기반의 사용자 ID 갖고오는 메서드
     * @param token
     * @return
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()    // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
