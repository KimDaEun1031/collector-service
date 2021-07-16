package org.daeun.collector.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.daeun.collector.controller.CovidVaccineStatController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CovidVaccineStatScheduler {

    @Autowired
    CovidVaccineStatController controller;

    @Scheduled(cron = "0 0 12 * * ?")
    public void covidVaccineStatPushSchedule() {

        controller.readCovidVaccineStatTodayData();
//        controller.readCovidVaccineStatFullData();

        log.info("push success!");
    }
}
