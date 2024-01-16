package com.school.sba.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolObjectNotFoundByIdException;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.service.ISchoolService;
import com.school.sba.util.ResponseStructure;


@Service
public class SchoolServiceImpl implements ISchoolService{

	@Autowired
	private ISchoolRepository schoolRepo;

	@Override
	public ResponseEntity<ResponseStructure<School>> addSchool(School school){

		School saveStudent = schoolRepo.save(school);

		ResponseStructure<School> rs = new ResponseStructure<School>();
		rs.setStatus(HttpStatus.CREATED.value());
		rs.setMessage("School data inserted successfully");
		rs.setData(saveStudent);

		return new ResponseEntity<ResponseStructure<School>>(rs, HttpStatus.CREATED);

	}

	@Override
	public ResponseEntity<ResponseStructure<School>> deleteSchool(Integer schoolId) throws SchoolObjectNotFoundByIdException {
		Optional<School> optional = schoolRepo.findById(schoolId);

		if(optional.isPresent()) {
			School school = optional.get();
			schoolRepo.deleteById(schoolId);

			ResponseStructure<School> rs = new ResponseStructure<School>();
			rs.setStatus(HttpStatus.OK.value());
			rs.setMessage("School data deleted successfully from database");
			rs.setData(school);

			return new ResponseEntity<ResponseStructure<School>>(rs, HttpStatus.OK);
		}
		else {
			throw new SchoolObjectNotFoundByIdException("school object cannot be deleted due to absence of school id");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> updateSchool(Integer schoolId, School updatedSchool)
			throws SchoolObjectNotFoundByIdException {
		Optional<School> optional = schoolRepo.findById(schoolId);

		if(optional.isPresent()) {
			School existingSchool = optional.get();
			updatedSchool.setSchoolId(existingSchool.getSchoolId());
			School save = schoolRepo.save(updatedSchool);

			ResponseStructure<School> rs = new ResponseStructure<School>();
			rs.setStatus(HttpStatus.OK.value());
			rs.setMessage("School data updated successfully in database");
			rs.setData(save);

			return new ResponseEntity<ResponseStructure<School>>(rs, HttpStatus.OK);
		}
		else {
			throw new SchoolObjectNotFoundByIdException("school object cannot be updated due to absence of technical problems");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> findSchool(Integer schoolId)
			throws SchoolObjectNotFoundByIdException {
		
		Optional<School> optional = schoolRepo.findById(schoolId);
		
		if(optional.isPresent()) {
			School school = optional.get();
			
			ResponseStructure<School> rs = new ResponseStructure<School>();
			rs.setStatus(HttpStatus.FOUND.value());
			rs.setMessage("School data found in database");
			rs.setData(school);

			return new ResponseEntity<ResponseStructure<School>>(rs, HttpStatus.FOUND);
		}
		else {
			throw new SchoolObjectNotFoundByIdException("School object cannot be fetched because it is not present in DB");
		}
		
	}

	@Override
	public ResponseEntity<ResponseStructure<List<School>>> findAllSchool() {
		
		List<School> all = schoolRepo.findAll();
		
		
		ResponseStructure<List<School>> rs = new ResponseStructure<List<School>>();
		rs.setStatus(HttpStatus.FOUND.value());
		rs.setMessage("School data found in database");
		rs.setData(all);

		return new ResponseEntity<ResponseStructure<List<School>>>(rs, HttpStatus.FOUND);
		
	}

	
	
	
	



}
