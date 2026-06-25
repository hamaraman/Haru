package org.example.asq.service;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.User;
import org.example.asq.dto.MemberStatsDto;
import org.example.asq.dto.UserDto;
import org.example.asq.dto.UserUpdateDto;
import org.example.asq.repository.CommentRepository;
import org.example.asq.repository.PostRepository;
import org.example.asq.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setProvider("local");
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if (!"local".equals(user.getProvider())) {
            throw new IllegalArgumentException("소셜 로그인 계정입니다. 네이버로 로그인해주세요.");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public User updateProfile(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 닉네임 중복 체크 (자신 제외)
        if (!user.getNickname().equals(dto.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        // 비밀번호 변경 요청 시 현재 비밀번호 검증
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
            }
            if (!"local".equals(user.getProvider())) {
                throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
                throw new IllegalArgumentException("새 비밀번호와 확인이 일치하지 않습니다.");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public MemberStatsDto getStats(Long userId) {
        long postCount    = postRepository.countByUserId(userId);
        long commentCount = commentRepository.countByUserId(userId);
        long totalLikes   = userRepository.sumLikeCountByUserId(userId);
        return new MemberStatsDto(postCount, commentCount, totalLikes);
    }
}
