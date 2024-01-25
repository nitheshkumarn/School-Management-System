package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.ISchoolService;
import com.school.sba.util.ResponseStructure;

@RestController

public class SchoolController {
	
	@Autowired
	private ISchoolService schoolService;
		
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(@PathVariable Integer userId, @RequestBody SchoolRequest schoolRequest){
		return schoolService.saveSchool(schoolRequest);
	}
	

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/{schoolId}")
	public ResponseEntity<ResponseStructure<SchoolResponse>> updateSchool(@PathVariable Integer schoolId, @RequestBody SchoolRequest schoolRequest){
		return schoolService.updateSchool(schoolId, schoolRequest);
	}
	
//	@GetMapping("/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> findSchool(@PathVariable Integer schoolId){
//		return schoolService.findSchool(schoolId);
//	}
//	
//	@PreAuthorize("hasAuthority('ADMIN')")
//	@DeleteMapping("/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> deleteSchool(@PathVariable Integer schoolId){
//		return schoolService.deleteSchool(schoolId);
//	}
//	
	
}