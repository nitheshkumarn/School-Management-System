package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;



public interface lUserService {
	
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest);
	
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> findUser(Integer userId);

	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(Integer userId);
	
	public ResponseEntity<ResponseStructure<UserResponse>> updateUser(int userId, UserRequest userRequest);
	
	public ResponseEntity<ResponseStructure<UserResponse>> assignSubjectToTeacher(int subjectId, int userId);

	public ResponseEntity<ResponseStructure<UserResponse>> assignUserToProgram(Integer programId, Integer userId);

	public ResponseEntity<ResponseStructure<List<UserResponse>>> getUsersByRoleAndAcademicProgram(int programId,
			String userRole);

}
