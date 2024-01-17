package com.school.sba.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.AdminAlreadyExistException;
import com.school.sba.exception.SchoolAlreadyExistException;
import com.school.sba.exception.SchoolInsertionFailedException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;

@RestControllerAdvice
public class ApplicationHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ResponseStructure<String>> schoolObjectNotFoundByIdException(
			SchoolNotFoundByIdException exception) {
		ResponseStructure<String> rs = new ResponseStructure<String>();
		rs.setStatus(HttpStatus.NOT_FOUND.value());
		rs.setMessage(exception.getMessage());
		rs.setData("School object not found by ID in database");

		return new ResponseEntity<ResponseStructure<String>>(rs, HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(Map.of("status", status.value(), "message", message, "rootCause", rootCause),
				status);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleAdminAlreadyExist(AdminAlreadyExistException exception) {

		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Admin Already Exist to this server");
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleUserNotFoundById(UserNotFoundByIdException exception) {

		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "User with the given ID is not present");
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleSchoolAlreadyExist(SchoolAlreadyExistException exception) {

		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "School Already Exist to this ADMIN");
	}
	
	
	
	
}
