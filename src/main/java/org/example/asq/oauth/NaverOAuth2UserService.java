package org.example.asq.oauth;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.User;
import org.example.asq.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> naverResponse =
                (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String socialId = String.valueOf(naverResponse.get("id"));
        String email = naverResponse.get("email") != null
                ? String.valueOf(naverResponse.get("email"))
                : socialId + "@naver.placeholder";
        String nickname = naverResponse.get("nickname") != null
                ? String.valueOf(naverResponse.get("nickname"))
                : String.valueOf(naverResponse.getOrDefault("name", "네이버사용자"));

        User user = userRepository.findBySocialId(socialId).orElseGet(() -> {
            User nu = new User();
            nu.setEmail(userRepository.existsByEmail(email)
                    ? socialId + "_" + email : email);
            nu.setNickname(nickname);
            nu.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            nu.setProvider("naver");
            nu.setSocialId(socialId);
            return userRepository.save(nu);
        });

        Map<String, Object> attributes = new HashMap<>(naverResponse);
        attributes.put("dbUserId", user.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
    }
}
