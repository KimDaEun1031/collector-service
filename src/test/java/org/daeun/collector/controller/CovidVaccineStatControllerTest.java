package org.daeun.collector.controller;

import org.junit.jupiter.api.Test;

public class CovidVaccineStatControllerTest {

    @Test
    void covidVaccineStatRow() {
        CovidVaccineStatController controller = new CovidVaccineStatController();
        controller.readCovidVaccineStatFullData();

    }
}