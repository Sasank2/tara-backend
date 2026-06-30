package com.tara.profile;

import com.tara.auth.AuthService;
import com.tara.common.Exceptions.ResourceNotFoundException;
import com.tara.user.User;
import com.tara.western.WesternChartService;
import com.tara.vedic.VedicChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthProfileService {

    private final BirthProfileRepository birthProfileRepository;
    private final GeoNamesClient geoNamesClient;
    private final WesternChartService westernChartService;
    private final VedicChartService vedicChartService;
    private final AuthService authService;

    @Transactional
    public BirthProfileDto.Response createBirthProfile(BirthProfileDto.CreateRequest request) {
        User user = authService.getCurrentUser();

        // Resolve location via GeoNames if lat/lng not provided
        BirthProfileDto.GeoNamesResult geo;
        if (request.getLatitude() != null && request.getLongitude() != null && request.getTimezone() != null) {
            geo = new BirthProfileDto.GeoNamesResult();
            geo.setLatitude(request.getLatitude());
            geo.setLongitude(request.getLongitude());
            geo.setTimezone(request.getTimezone());
            geo.setFormattedLocation(request.getPlaceOfBirth());
        } else {
            geo = geoNamesClient.searchLocation(request.getPlaceOfBirth());
        }

        // Unset existing primary
        birthProfileRepository.findPrimaryByUserId(user.getId()).ifPresent(existing -> {
            existing.setPrimary(false);
            birthProfileRepository.save(existing);
        });

        BirthProfile profile = BirthProfile.builder()
                .user(user)
                .profileName(request.getProfileName() != null ? request.getProfileName() : "My Profile")
                .fullName(request.getFullName())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .timeOfBirth(LocalTime.parse(request.getTimeOfBirth(), DateTimeFormatter.ofPattern("HH:mm")))
                .placeOfBirth(request.getPlaceOfBirth())
                .latitude(geo.getLatitude())
                .longitude(geo.getLongitude())
                .timezone(geo.getTimezone())
                .isPrimary(true)
                .build();

        BirthProfile saved = birthProfileRepository.save(profile);
        log.info("Birth profile created for user {}", user.getId());

        // Generate charts (non-blocking — chart failure won't fail profile creation)
        try {
            westernChartService.generateChart(saved);
            vedicChartService.generateChart(saved);
        } catch (Exception e) {
            log.error("Chart generation failed for profile {}: {}", saved.getId(), e.getMessage());
        }

        return toResponse(saved);
    }


    @Transactional
    public BirthProfileDto.Response updateBirthProfile(BirthProfileDto.UpdateRequest request) {
        User user = authService.getCurrentUser();
        BirthProfile profile = birthProfileRepository.findPrimaryByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Birth profile not found. Please complete onboarding."));

        // Resolve location via GeoNames if lat/lng not provided, or if place changed
        BirthProfileDto.GeoNamesResult geo;
        boolean placeChanged = !profile.getPlaceOfBirth().equalsIgnoreCase(request.getPlaceOfBirth());
        if (!placeChanged && request.getLatitude() == null) {
            geo = new BirthProfileDto.GeoNamesResult();
            geo.setLatitude(profile.getLatitude());
            geo.setLongitude(profile.getLongitude());
            geo.setTimezone(profile.getTimezone());
        } else if (request.getLatitude() != null && request.getLongitude() != null && request.getTimezone() != null) {
            geo = new BirthProfileDto.GeoNamesResult();
            geo.setLatitude(request.getLatitude());
            geo.setLongitude(request.getLongitude());
            geo.setTimezone(request.getTimezone());
        } else {
            geo = geoNamesClient.searchLocation(request.getPlaceOfBirth());
        }

        profile.setProfileName(request.getProfileName() != null ? request.getProfileName() : profile.getProfileName());
        profile.setFullName(request.getFullName());
        profile.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        profile.setTimeOfBirth(LocalTime.parse(request.getTimeOfBirth(), DateTimeFormatter.ofPattern("HH:mm")));
        profile.setPlaceOfBirth(request.getPlaceOfBirth());
        profile.setLatitude(geo.getLatitude());
        profile.setLongitude(geo.getLongitude());
        profile.setTimezone(geo.getTimezone());

        BirthProfile saved = birthProfileRepository.save(profile);
        log.info("Birth profile updated for user {}", user.getId());

        // Regenerate charts since birth details changed
        try {
            westernChartService.generateChart(saved);
            vedicChartService.generateChart(saved);
        } catch (Exception e) {
            log.error("Chart regeneration failed for profile {}: {}", saved.getId(), e.getMessage());
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BirthProfileDto.Response getBirthProfile() {
        User user = authService.getCurrentUser();
        BirthProfile profile = birthProfileRepository.findPrimaryByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Birth profile not found. Please complete onboarding."));
        return toResponse(profile);
    }

    private BirthProfileDto.Response toResponse(BirthProfile p) {
        BirthProfileDto.Response r = new BirthProfileDto.Response();
        r.setId(p.getId().toString());
        r.setProfileName(p.getProfileName());
        r.setFullName(p.getFullName());
        r.setDateOfBirth(p.getDateOfBirth().toString());
        r.setTimeOfBirth(p.getTimeOfBirth().toString());
        r.setPlaceOfBirth(p.getPlaceOfBirth());
        r.setLatitude(p.getLatitude());
        r.setLongitude(p.getLongitude());
        r.setTimezone(p.getTimezone());
        r.setPrimary(p.isPrimary());
        return r;
    }
}
