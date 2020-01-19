package com.springboot.social.server;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class KakaoClient {

    private static final String KAKAO_HOST = "https://kapi.kakao.com";

    public ResponseEntity<HashMap<String, Object>> me(String access_token) {
        RestTemplate template = new RestTemplate();

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<HashMap<String, Object>> responseType =
                new ParameterizedTypeReference<HashMap<String, Object>>() {};

        return template.exchange(KAKAO_HOST + "/v2/user/me", HttpMethod.GET, entity, responseType);
    }
}
