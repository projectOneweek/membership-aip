package com.example.demo.config;

import com.example.demo.dto.AuthInfo;
import com.example.demo.service.JwtService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

public class AuthInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    public AuthInfoArgumentResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AuthInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String authorization = webRequest.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("UnauthorizedException");
        }

        String token = authorization.substring(7);
        Map<String, Long> decodedToken = jwtService.decode(token);
        Long memberId = ((Number) decodedToken.get("id")).longValue();

        if (memberId == null) {
            throw new RuntimeException("UnauthorizedException");
        }

        return new AuthInfo(memberId);
    }
}