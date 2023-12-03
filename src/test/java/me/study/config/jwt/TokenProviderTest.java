package me.study.config.jwt;

import io.jsonwebtoken.Jwts;
import me.study.domain.User;
import me.study.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달홰 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given : 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("csm0222@gmail.com")
                .password("seng")
                .build());

        // when 토큰생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then jwt 라이브러리로 토큰 복호화
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given : jjwt 라이브러리를 사용해 토큰을 생성합니다.
        // 만료시간은 1970년 1월 1일 부터 현재시간을 밀리초단위로 치환간 값(new Date().getTime()) 에서 7일*밀로초를 빼 이미 만료된 토큰으로 생성합니다.
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when : valid 토큰 여부 체크
        boolean result = tokenProvider.validToken(token);

        // then : 반환값이 유효한 토큰이 아님(false)를 확인
        assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given : jjwt 라이브러리를 사용해 토큰 생성(기본값, 만료일 14일뒤로 생성)
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when : valid 토큰 여부 체크
        boolean result = tokenProvider.validToken(token);

        // then : 반환값이 유효한 토큰(true)인지를 확인
        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // csm0222@gmail.com user로 인증 토큰 생성
        String userEmail = "csm0222@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when : 인증객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then : 가져온 인증객체와 subject 값인 "csm0222@gmail.com"값 비교
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserid() {
        // given : 키는 ID, 값은 1이라는 유저아이디 토큰 생성
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when : getUserId로 유저ID를 반환
        Long userIdByToken = tokenProvider.getUserId(token);

        // then : given의 userid와 반환값 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}