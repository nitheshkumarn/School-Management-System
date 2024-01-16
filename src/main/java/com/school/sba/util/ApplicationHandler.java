package com.school.sba.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.sba.exception.SchoolInsertionFailedException;
import com.school.sba.exception.SchoolObjectNotFoundByIdException;

@RestControllerAdvice
public class ApplicationHandler {
	
	@ExceptionHandler
	public ResponseEntity<ResponseStructure<String>> schoolInsertionFailedExceptionHandler(SchoolInsertionFailedException exception){
		ResponseStructure<String> rs = new ResponseStructure<String>();
		rs.setStatus(HttpStatus.FORBIDDEN.value());
		rs.setMessage(exception.getMessage());
		rs.setData("School Object could not be inserted due to null data insertion in fields");
		
		return new ResponseEntity<ResponseStructure<String>>(rs, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler
	public ResponseEntity<ResponseStructure<String>> schoolObjectNotFoundByIdException(SchoolObjectNotFoundByIdException exception){
		ResponseStructure<String> rs = new ResponseStructure<String>();
		rs.setStatus(HttpStatus.NOT_FOUND.value());
		rs.setMessage(exception.getMessage());
		rs.setData("School object not found by ID in database");
		
		return new ResponseEntity<ResponseStructure<String>>(rs, HttpStatus.NOT_FOUND);
	}

}
