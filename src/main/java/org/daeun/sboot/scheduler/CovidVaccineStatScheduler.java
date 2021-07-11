package org.daeun.sboot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.daeun.sboot.controller.CovidVaccineStatController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CovidVaccineStatScheduler {

    @Autowired
    CovidVaccineStatController controller;

    @Scheduled(cron = "*/30 * * * * *")
    public void covidVaccineStatPushSchedule() {
//        controller.readCovidVaccineStatTodayData();
        log.info("push success!");
    }
}
