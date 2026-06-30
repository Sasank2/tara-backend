package com.tara.user;

import com.tara.auth.AuthService;
import com.tara.auth.TaraPrincipal;
import com.tara.profile.BirthProfileRepository;
import com.tara.western.WesternChartRepository;
import com.tara.vedic.VedicChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BirthProfileRepository birthProfileRepository;
    private final WesternChartRepository westernChartRepository;
    private final VedicChartRepository vedicChartRepository;
    private final AuthService authService;

    @Transactional
    public UserDto.Response registerOrGet() {
        TaraPrincipal principal = authService.getCurrentPrincipal();
        User user = userRepository.findByFirebaseUid(principal.getFirebaseUid())
                .orElseGet(() -> {
                    log.info("Registering new user: {}", principal.getEmail());
                    return userRepository.save(User.builder()
                            .firebaseUid(principal.getFirebaseUid())
                            .email(principal.getEmail())
                            .name(principal.getName() != null ? principal.getName() : "Tara User")
                            .build());
                });
        return toResponse(user);
    }

    @Transactional
    public UserDto.Response updateUser(UserDto.UpdateRequest request) {
        User user = authService.getCurrentUser();
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        return toResponse(userRepository.save(user));
    }

    private UserDto.Response toResponse(User user) {
        UserDto.Response r = new UserDto.Response();
        r.setId(user.getId().toString());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setPhone(user.getPhone());
        r.setProfileImage(user.getProfileImage());
        // null-safe createdAt fix
        r.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        birthProfileRepository.findPrimaryByUserId(user.getId()).ifPresent(bp -> {
            r.setHasBirthProfile(true);
            westernChartRepository.findByBirthProfileId(bp.getId())
                    .ifPresent(w -> r.setHasWesternChart(true));
            vedicChartRepository.findByBirthProfileId(bp.getId())
                    .ifPresent(v -> r.setHasVedicChart(true));
        });
        return r;
    }
}
