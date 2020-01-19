package com.springboot.social.server.security.provider;

import com.springboot.social.server.KakaoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class UserDetailsServiceProvider implements AuthenticationProvider {

    @Autowired
    private KakaoClient kakaoClient;

    @Autowired
    @Qualifier("userDetailsServiceBean")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        HashMap<String, Object> details = (HashMap<String, Object>) authentication.getDetails();

        // provider_token 이 있으면 소셜에서 회원정보를 조회한다.
        if (details.get("provider_token") != null) {
            String sso_user_id = ssoExtractor(details.get("provider_token"));

            if (sso_user_id != null) {
                // ..TODO DB 처리

                return new UsernamePasswordAuthenticationToken(
                        sso_user_id, null, AuthorityUtils.createAuthorityList("BASIC_UESR")
                );
            }
        }

        // 기본인증.
        String user_password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        if (!passwordEncoder.matches(user_password, userDetails.getPassword())) {
            throw new BadCredentialsException("Password Match Failed");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()
        );
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private String ssoExtractor(final Object extractorToken) {
        String convert_str = extractorToken.toString();
        String[] xHeader = convert_str.split(" ");
        if (xHeader.length == 2) {
            String access_token = xHeader[1];
            if (xHeader[0].equalsIgnoreCase("kakao")) {
                // 카카오 헤더일 경우, kakao 유저를 검증한다.
                ResponseEntity<HashMap<String, Object>> response = kakaoClient.me(access_token);
                if (response.hasBody()) {
                    assert response.getBody() != null;
                    return response.getBody().get("id").toString();
                }
            }
        }

        return null;
    }
}
