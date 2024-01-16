package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolObjectNotFoundByIdException;
import com.school.sba.service.ISchoolService;
//import com.school.sba.util.ApplicationHandler;
import com.school.sba.util.ResponseStructure;

@RestController
@RequestMapping("/school")
public class SchoolController {
	
	@Autowired
	private ISchoolService schoolService;
	
	//@Autowired
	//private ApplicationHandler applicationHandler;
	
	@PostMapping("/insert")
	public ResponseEntity<ResponseStructure<School>> insertSchool(@RequestBody School school){
		
		ResponseEntity<ResponseStructure<School>> schoolResponse = schoolService.addSchool(school);
		
		return schoolResponse;
		
	}
	
	@DeleteMapping("/delete-school/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> deleteSchoolObj(@PathVariable Integer schoolId){
		ResponseEntity<ResponseStructure<School>> deleteSchool = null;
		
		try {
			deleteSchool = schoolService.deleteSchool(schoolId);
			
		} catch (SchoolObjectNotFoundByIdException e) {
			e.printStackTrace();
			
//			return applicationHandler.schoolObjectNotFoundByIdException(e);
		}
		return deleteSchool;
	}
	
	
	@PutMapping("/update-school/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> updateSchoolObj(@PathVariable Integer schoolId, @RequestBody School school){
		ResponseEntity<ResponseStructure<School>> updateSchool = null;
		
		try {
			updateSchool = schoolService.updateSchool(schoolId, school);
			
		} catch (SchoolObjectNotFoundByIdException e) {
			e.printStackTrace();
			
//			return applicationHandler.schoolObjectNotFoundByIdException(e);
		}
		return updateSchool;
	}
	
	
	@GetMapping("/get-school/{schoolId}")
	public ResponseEntity<ResponseStructure<School>> findSchoolObj(@PathVariable Integer schoolId){
		
		ResponseEntity<ResponseStructure<School>> findSchool = null;
		
		try {
			findSchool = schoolService.findSchool(schoolId);
			
		} catch (SchoolObjectNotFoundByIdException e) {
			e.printStackTrace();
			
//			return applicationHandler.schoolObjectNotFoundByIdException(e);
		}
		return findSchool;
		
		
	}
	
	
	
	@GetMapping("/get-all-school")
	public ResponseEntity<ResponseStructure<List<School>>> findAllSchoolObj(){
		
		ResponseEntity<ResponseStructure<List<School>>> findallSchool = null;
		
//		try {
			findallSchool = schoolService.findAllSchool();
			
//		} catch (SchoolObjectNotFoundByIdException e) {
//			e.printStackTrace();
//			
////			return applicationHandler.schoolObjectNotFoundByIdException(e);
//		}
		return findallSchool;
		
		
	}
	
}
