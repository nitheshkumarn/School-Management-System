package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.entity.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.AdminAlreadyExistException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.lUserService;
import com.school.sba.util.ResponseStructure;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements lUserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired 
	private AcademicProgramRepository acaRepo;

	@Autowired
	private ResponseStructure<UserResponse> rsu;

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest) {
		User userV = mapToUser(userRequest);
		if (userV.getUserRole().equals(UserRole.ADMIN)) {
			log.info("admin getting created");
			boolean rs = userRepo.existsByUserRole(userV.getUserRole());
			System.out.println("validating if admin present or not");
			if (rs == false) {
				System.out.println("admin not present");
				User user = userRepo.save(mapToUser(userRequest));
				rsu.setStatus(HttpStatus.CREATED.value());
				rsu.setMessage("user data inserted successfully");
				rsu.setData(mapToUserResponse(user));

				return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.CREATED);
			} else
				throw new AdminAlreadyExistException("You are not the admin");
		}

		else {
			
			User user = userRepo.save(mapToUser(userRequest));
			rsu.setStatus(HttpStatus.CREATED.value());
			rsu.setMessage("user data inserted successfully");
			rsu.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.CREATED);
		}
			
		
	}

	private UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).userFirstName(user.getUserFirstName())
				.userLastName(user.getUserLastName()).userRole(user.getUserRole()).userContact(user.getUserContact())
				.build();
	}

	private User mapToUser(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
				.userPass(userRequest.getUserPass()).userFirstName(userRequest.getUserFirstName())
				.userLastName(userRequest.getUserLastName()).userRole(userRequest.getUserRole())
				.userContact(userRequest.getUserContact()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(Integer userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User Not Found"));
		
		rsu.setStatus(HttpStatus.FOUND.value());
		rsu.setMessage("user data Fetched successfully");
		rsu.setData(mapToUserResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(Integer userId) {
		User userd = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User Not Found"));
		
		userd.setIsdeleted(true);
		User user = userRepo.save(userd);
		
		rsu.setStatus(HttpStatus.OK.value());
		rsu.setMessage("user data soft deleted successfully");
		rsu.setData(mapToUserResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);
		
		
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToProgram(Integer programId, Integer userId) {
		User userd = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("User Not Found"));
		
		AcademicProgram apd = acaRepo.findById(programId)
				.orElseThrow(()-> new AcademicProgramNotFoundException("Academic program not found"));
		
		userd.getAcademicPrograms().add(apd);
		apd.getUsers().add(userd);
		
		User user = userRepo.save(userd);
		acaRepo.save(apd);
		
		rsu.setStatus(HttpStatus.OK.value());
		rsu.setMessage("user data mapped successfully");
		rsu.setData(mapToUserResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);
		
		
		
		
	}



}
