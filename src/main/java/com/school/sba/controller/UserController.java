package com.school.sba.controller;

import java.util.List;

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

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.lUserService;
import com.school.sba.util.ResponseStructure;

@RestController

public class UserController {

	@Autowired
	private lUserService userService;

	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(@RequestBody UserRequest userRequest) {// @RequestParam(name
																											// =
																											// "userId",
																											// required
																											// = false)
																											// Integer
																											// userId,
		ResponseEntity<ResponseStructure<UserResponse>> rs = userService.registerAdmin(userRequest);
		return rs;
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(@RequestBody UserRequest userRequest){
		return userService.addOtherUser(userRequest);
	}

	@GetMapping("users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(@PathVariable Integer userId) {
		ResponseEntity<ResponseStructure<UserResponse>> findUser = null;

		findUser = userService.findUser(userId);

		return findUser;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(@PathVariable Integer userId) {
		ResponseEntity<ResponseStructure<UserResponse>> deleteUser = null;

		deleteUser = userService.softDeleteUser(userId);

		return deleteUser;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToProgram(@PathVariable Integer programId,
			@PathVariable Integer userId) {
		ResponseEntity<ResponseStructure<UserResponse>> AssignUser = null;
		AssignUser = userService.assignUserToProgram(programId,userId);
		return AssignUser;
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> updateUser(@PathVariable("userId") int userId,
			@RequestBody UserRequest userRequest){
		return userService.updateUser(userId, userRequest);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> assignSubjectToTeacher(@PathVariable("subjectId") int subjectId,
			@PathVariable("userId") int userId){
		return userService.assignSubjectToTeacher(subjectId,userId);
	}
	
	@GetMapping("/academic-programs/{programId}/user-roles/{role}/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>> getUsersByRoleAndAcademicProgram(@PathVariable("programId") int programId,
			@PathVariable String role){
		return userService.getUsersByRoleAndAcademicProgram(programId, role);
	}

	
}
