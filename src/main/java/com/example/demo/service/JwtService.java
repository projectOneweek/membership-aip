package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")  // 비밀 키를 외부 설정에서 가져옴 ㅇㅇㅇ
    private String secretKey;

    public static final String CLAIM_NAME_MEMBER_ID = "MemberId";  // JWT 클레임 키

    private Algorithm algorithm;  // HMAC 알고리즘
    private JWTVerifier jwtVerifier;  // JWT 검증기

    @PostConstruct
    private void init() {
        algorithm = Algorithm.HMAC256(secretKey);  // HMAC256 알고리즘 초기화
        jwtVerifier = JWT.require(algorithm).build();  // JWT 검증기 설정
    }

    // JWT 토큰 생성 메서드
    public String encode(Long memberId) {
        LocalDateTime expiredAt = LocalDateTime.now().plusWeeks(4);  // 4주 후 만료
        Date expirationDate = Timestamp.valueOf(expiredAt);

        return JWT.create()
                .withClaim(CLAIM_NAME_MEMBER_ID, memberId)  // MemberId 클레임 추가
                .withExpiresAt(expirationDate)  // 만료 시간 설정
                .sign(algorithm);  // 서명
    }

    // JWT 토큰 해독 및 검증 메서드
    public Map<String, Long> decode(String token) {
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);  // 토큰 검증
            Long memberId = jwt.getClaim(CLAIM_NAME_MEMBER_ID).asLong();  // MemberId 추출
            return Map.of(CLAIM_NAME_MEMBER_ID, memberId);
        } catch (JWTVerificationException e) {
            log.warn("Failed to decode jwt token: {}", token, e);  // 로그 경고
            return null;
        }
    }
}