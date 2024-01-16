package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;



public interface lUserService {
	
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> findUser(Integer userId);

	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(Integer userId);

}
