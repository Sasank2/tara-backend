package com.tara.planets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Slf4j @Component @RequiredArgsConstructor
public class PlanetsScheduler {
    private final PlanetsService planetsService;

    @Scheduled(cron = "${planets.scheduler.cron}", zone = "${planets.scheduler.timezone}")
    public void scheduleDailyPlanets() {
        log.info("Daily planets scheduler running for: {}", LocalDate.now());
        try { planetsService.calculateAndSave(LocalDate.now()); }
        catch (Exception e) { log.error("Daily planets scheduler failed", e); }
    }
}
