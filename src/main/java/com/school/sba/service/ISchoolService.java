package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.School;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.util.ResponseStructure;



public interface ISchoolService {

	ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(SchoolRequest schoolRequest);
	
	ResponseEntity<ResponseStructure<School>> deleteSchool(Integer schoolId);

	ResponseEntity<ResponseStructure<SchoolResponse>> updateSchool(Integer schoolId, SchoolRequest schoolRequest);

	ResponseEntity<ResponseStructure<School>> findSchool(Integer schoolId);

	
	

}