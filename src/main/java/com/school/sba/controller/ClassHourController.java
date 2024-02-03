package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequest;
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
    
    @PutMapping("/academic-program/{programId}/class-hours")
    public ResponseEntity<ResponseStructure<String>> duplicateClassHoursForNextWeek(@PathVariable("programId") int programId){	
    	return classHourService.duplicateClassHoursForThisWeek(programId);
    	
    }
    
    @PutMapping("/academic-program/{programId}/class-hours/write-excel")
    public String ExcelSheet(@PathVariable("programId") int programId,@RequestBody ExcelRequest excelRequest){
    	return classHourService.toWorkBook(programId,excelRequest);
    }
    
    @PostMapping("/academic-program/{programId}/class-hours/fromDate/{from}/toDate/{to}/write-excel")
    public ResponseEntity<?> generateExcel(@PathVariable("programId") int programId,@PathVariable LocalDate from,@PathVariable LocalDate to,@RequestParam("file") MultipartFile file) throws IOException {
    return classHourService.generateExcel(programId,from,to,file);
    }
}