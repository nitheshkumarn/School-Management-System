package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.IClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {
	
	@Autowired
	private IClassHourService classHourService;
	
	@PostMapping("/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> generateClassHourForAcademicProgram(@PathVariable("programId") int programId){
    return classHourService.generateClassHourForProgram(programId);
	}
	
    @PutMapping("/class-hours") 
    public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> assigningPeriods(@RequestBody List<ClassHourRequest> classHourRequest){
    	return classHourService.assignPeriods(classHourRequest);
    
}
}