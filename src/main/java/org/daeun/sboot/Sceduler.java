package org.daeun.sboot;

import org.daeun.sboot.controller.RestTestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Sceduler {
	
	@Autowired
	RestTestController controller;
	
	
	@Scheduled(cron = "0 * * * * *")	// 1분마다
	public void test3() throws Exception {
		log.info("health check");
		
	}
}
