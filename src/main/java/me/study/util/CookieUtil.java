package me.study.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    /**
     * 이름, 값, 만료기간으로 Http 응답에 쿠키 추가
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 이름을 받아 쿠키 삭제
     * 실제 삭제할수 없으니 파라미터로 넘어온 쿠키를 빈값으로 바꾸고 만료시간을 0으로 설정해서 만료처리
     * @param request
     * @param response
     * @param name
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * 객체를 직렬화 해서 쿠키 값으로 들어갈 값으로 변환
     * @param obj
     * @return
     */
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * 쿠키를 역직렬화 객체로 변환
     * @param cookie
     * @param cls
     * @return
     * @param <T>
     */
    public static<T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue()))
        );
    }
}
