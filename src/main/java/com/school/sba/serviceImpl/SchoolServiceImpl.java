package com.school.sba.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.school.sba.entity.School;

import com.school.sba.entity.enums.UserRole;
import com.school.sba.exception.AdminAlreadyExistException;
import com.school.sba.exception.SchoolAlreadyExistException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.ISchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements ISchoolService {

	@Autowired
	private ISchoolRepository schoolRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ResponseStructure<School> responseStructure;

	@Autowired
	private ResponseStructure<SchoolResponse> responseS;

	private School mapToSchool(SchoolRequest schoolRequest) {
		return School.builder().schoolName(schoolRequest.getSchoolName())
				.schoolEmailId(schoolRequest.getSchoolEmailId())
				.schoolContactNumber(schoolRequest.getSchoolContactNumber())
				.schoolAddress(schoolRequest.getSchoolAddress()).build();
	}

	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder().schoolId(school.getSchoolId()).schoolName(school.getSchoolName()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(Integer userId, SchoolRequest schoolRequest) {

		 return userRepo.findById(userId).map(u->{
			
			if (u.getUserRole().equals(UserRole.ADMIN)) {
				if(u.getSchool()==null) {
				School saveSchool = schoolRepo.save(mapToSchool(schoolRequest));
				u.setSchool(saveSchool);
				userRepo.save(u);
				responseS.setStatus(HttpStatus.CREATED.value());
				responseS.setMessage("School data inserted successfully");
				responseS.setData(mapToSchoolResponse(saveSchool));

				return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseS, HttpStatus.CREATED);

			}
				else
					throw new SchoolAlreadyExistException("School present for the admin provided");

			}else
				throw new AdminAlreadyExistException("You are not the admin");
			
		})
				.orElseThrow(() -> new UserNotFoundByIdException(null));

	

	}

	@Override
	public ResponseEntity<ResponseStructure<School>> deleteSchool(Integer schoolId) {

		School existingSchool = schoolRepo.findById(schoolId).orElseThrow(
				() -> new SchoolNotFoundByIdException("school object cannot be deleted due to absence of school id"));

		schoolRepo.deleteById(schoolId);

		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("School data deleted successfully from database");
		responseStructure.setData(existingSchool);

		return new ResponseEntity<ResponseStructure<School>>(responseStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> updateSchool(Integer schoolId, SchoolRequest schoolRequest)
			throws SchoolNotFoundByIdException {

		School existingSchool = schoolRepo.findById(schoolId).map(u -> {
			School school = mapToSchool(schoolRequest);
			school.setSchoolId(schoolId);
			return schoolRepo.save(school);
		}).orElseThrow(() -> new SchoolNotFoundByIdException(
				"school object cannot be updated due to absence of technical problems"));

		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("School data updated successfully in database");
		responseStructure.setData(existingSchool);

		return new ResponseEntity<ResponseStructure<School>>(responseStructure, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<ResponseStructure<School>> findSchool(Integer schoolId) throws SchoolNotFoundByIdException {

		School fetchedSchool = schoolRepo.findById(schoolId)
				.orElseThrow(() -> new SchoolNotFoundByIdException("SCHOOL NOT PRESENT"));

		responseStructure.setStatus(HttpStatus.FOUND.value());
		responseStructure.setMessage("School data found in database");
		responseStructure.setData(fetchedSchool);

		return new ResponseEntity<ResponseStructure<School>>(responseStructure, HttpStatus.FOUND);

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