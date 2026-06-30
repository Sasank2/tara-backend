package com.tara.auth;

import com.tara.common.Exceptions.UnauthorizedException;
import com.tara.user.User;
import com.tara.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        TaraPrincipal principal = getCurrentPrincipal();
        return userRepository.findByFirebaseUid(principal.getFirebaseUid())
                .orElseThrow(() -> new UnauthorizedException("User not registered. Please complete onboarding."));
    }

    public TaraPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof TaraPrincipal))
            throw new UnauthorizedException("Not authenticated");
        return (TaraPrincipal) auth.getPrincipal();
    }
}
