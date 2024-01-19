package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ScheduleRequest;

import com.school.sba.responsedto.ScheduleResponse;

import com.school.sba.service.IScheduleService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ScheduleController {
	
	@Autowired
	private IScheduleService scheduleService;
	
	@PostMapping("/schools/{schoolId}/schedules")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> saveSchool(@PathVariable Integer schoolId, @RequestBody ScheduleRequest scheduleRequest){
		return scheduleService.saveSchedule(schoolId,scheduleRequest);
	}

}
