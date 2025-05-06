package com.fitted.security;

import com.fitted.user.Role;
import com.fitted.user.User;
import com.fitted.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public User checkAdmin(String supabaseId) {
        User user = userRepository.findBySupabaseId(supabaseId).orElseThrow(
                () -> new NoSuchElementException("해당하는 사용자가 없습니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자가 아닙니다.");
        }
        return user;
    }

    public User checkCustomer(String supabaseId) {
        User user = userRepository.findBySupabaseId(supabaseId).orElseThrow(
                () -> new NoSuchElementException("해당하는 사죵자가 없습니다."));
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalStateException("소비자가 아닙니다.");
        }
        return user;
    }
}
