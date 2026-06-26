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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> naverResponse =
                (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String socialId = String.valueOf(naverResponse.get("id"));
        String email    = naverResponse.get("email") != null
                ? String.valueOf(naverResponse.get("email"))
                : socialId + "@naver.placeholder";
        String nickname = naverNickname(naverResponse, socialId);

        User user = resolveUser(socialId, email, nickname);

        Map<String, Object> attributes = new HashMap<>(naverResponse);
        attributes.put("dbUserId", user.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
    }

    private User resolveUser(String socialId, String email, String nickname) {
        // 1) 같은 이메일 계정 우선 조회 → 연동
        if (!email.endsWith("@naver.placeholder")) {
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                User existing = byEmail.get();
                if (existing.getSocialId() == null) {
                    existing.setSocialId(socialId);
                    userRepository.save(existing);
                }
                return existing;
            }
        }

        // 2) socialId로 기존 네이버 계정 조회
        Optional<User> bySocial = userRepository.findBySocialId(socialId);
        if (bySocial.isPresent()) {
            return bySocial.get();
        }

        // 3) 신규 네이버 계정 생성
        User nu = new User();
        nu.setEmail(email);
        nu.setNickname(nickname);
        nu.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        nu.setProvider("naver");
        nu.setSocialId(socialId);
        return userRepository.save(nu);
    }

    private String naverNickname(Map<String, Object> response, String socialId) {
        if (response.get("nickname") != null) {
            String nick = String.valueOf(response.get("nickname")).trim();
            if (!nick.isEmpty()) return nick;
        }
        String suffix = socialId.length() > 6
                ? socialId.substring(socialId.length() - 6) : socialId;
        return "naver_" + suffix;
    }
}
