package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolObjectNotFoundByIdException;
import com.school.sba.util.ResponseStructure;

public interface ISchoolService {

	ResponseEntity<ResponseStructure<School>> addSchool(School school);
	
	ResponseEntity<ResponseStructure<School>> deleteSchool(Integer schoolId) throws SchoolObjectNotFoundByIdException;

	ResponseEntity<ResponseStructure<School>> updateSchool(Integer schoolId, School school) throws SchoolObjectNotFoundByIdException;

	ResponseEntity<ResponseStructure<School>> findSchool(Integer schoolId) throws SchoolObjectNotFoundByIdException;

	ResponseEntity<ResponseStructure<List<School>>> findAllSchool();

}
