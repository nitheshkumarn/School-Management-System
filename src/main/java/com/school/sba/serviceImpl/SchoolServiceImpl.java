package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.enums.UserRole;
import com.school.sba.exception.SchoolAlreadyExistException;
import com.school.sba.exception.SchoolInsertionFailedException;
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
		return SchoolResponse.builder().schoolId(school.getSchoolId()).schoolName(school.getSchoolName()).schoolEmailId(school.getSchoolEmailId()).schoolContactNumber(school.getSchoolContactNumber())
				.schoolAddress(school.getSchoolAddress()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(SchoolRequest schoolRequest) {
		String username = SecurityContextHolder.getContext()
				.getAuthentication()
				.getName();
		
		// no need of taking the userId from the url beacuse we are retriving the username from the securityContextHolder.
		// no need of passing the userId unless or until we have to deal with the another user.
		
		return userRepo.findByUserName(username)
				.map(user -> {
					if(user.getUserRole().equals(UserRole.ADMIN)) {
						if(user.getSchool() == null) {
							School school = schoolRepo.save(mapToSchool(schoolRequest));

							userRepo.findAll().forEach(userFromRepo -> {
								userFromRepo.setSchool(school);
								userRepo.save(user);
							});

							responseS.setStatus(HttpStatus.CREATED.value());
							responseS.setMessage("School inserted successfully");
							responseS.setData(mapToSchoolResponse(school));

							return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseS, HttpStatus.CREATED);
						}
						else {
							throw new SchoolAlreadyExistException("school is already present");
						}
					}
					else {
						throw new SchoolInsertionFailedException("school can be created only by ADMIN");
					}
				})
				.orElseThrow(() -> new UserNotFoundByIdException("user not found"));
	}

//	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> updateSchool(Integer schoolId, SchoolRequest schoolRequest)
			throws SchoolNotFoundByIdException {

		return schoolRepo.findById(schoolId)
				.map( school -> {
					school = mapToSchool(schoolRequest);
					school.setSchoolId(schoolId);
					school = schoolRepo.save(school);
					
					responseS.setStatus(HttpStatus.OK.value());
					responseS.setMessage("School data updated successfully in database");
					responseS.setData(mapToSchoolResponse(school));

					return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseS, HttpStatus.OK);
				})
				.orElseThrow(() -> new SchoolNotFoundByIdException("school object cannot be updated due to absence of technical problems"));

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
	public ResponseEntity<ResponseStructure<SchoolResponse>> softDeleteSchool(Integer schoolId) {
		return schoolRepo.findById(schoolId).map(school -> {
			if (school.isDeleted()) {
				throw new SchoolNotFoundByIdException("school already deleted");
			}

			school.setDeleted(true);
			schoolRepo.save(school);

			responseS.setStatus(HttpStatus.OK.value());
			responseS.setMessage("School deleted successfully");
			responseS.setData(mapToSchoolResponse(school));

			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseS, HttpStatus.OK);
		}).orElseThrow(() -> new SchoolNotFoundByIdException("school not found"));

	}

//	@Override
//	public ResponseEntity<ResponseStructure<School>> deleteSchool(Integer schoolId) {
//
//		School existingSchool = schoolRepo.findById(schoolId).orElseThrow(
//				() -> new SchoolNotFoundByIdException("school object cannot be deleted due to absence of school id"));
//
//		schoolRepo.deleteById(schoolId);
//
//		responseStructure.setStatus(HttpStatus.OK.value());
//		responseStructure.setMessage("School data deleted successfully from database");
//		responseStructure.setData(existingSchool);
//
//		return new ResponseEntity<ResponseStructure<School>>(responseStructure, HttpStatus.OK);
//	}


}