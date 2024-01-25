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
import com.school.sba.exception.AdminCannotBeAssignedToAcademicProgram;
import com.school.sba.exception.AdminNotFoundException;
import com.school.sba.exception.OnlyTeacherCanBeAssignedToSubjectException;
import com.school.sba.exception.ScheduleAlreadyPresentException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolAlreadyExistException;
import com.school.sba.exception.SchoolInsertionFailedException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.SubjectNotFoundException;
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
	
	@ExceptionHandler(ScheduleNotFoundException.class)
	public ResponseEntity<Object> handleScheduleNotFoundException(ScheduleNotFoundException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Schedule not found, Try adding the schedule first");
	}
	
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//		List<ObjectError> allErrors = ex.getAllErrors();
//		Map<String, String> errors = new HashMap<String, String>();
//		allErrors.forEach(error -> {
//			FieldError fieldError = (FieldError) error;
//
//		return structure(HttpStatus.BAD_REQUEST, Exception.getMessage(), "Schedule is already present and assigned to school");
//	}
	
		@ExceptionHandler(ScheduleAlreadyPresentException.class)
		public ResponseEntity<Object> handleScheduleAlreadyPresentException(ScheduleAlreadyPresentException exception) {
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Schedule is already present and assigned to school");
		}
		
		@ExceptionHandler(AdminNotFoundException.class)
		public ResponseEntity<Object> handleAdminNotFoundException(AdminNotFoundException exception) {
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Login as Admin to register");
		}
		
		@ExceptionHandler(SubjectNotFoundException.class)
		public ResponseEntity<Object> handleSubjectNotFoundException(SubjectNotFoundException exception) {
			return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "subject not found by id");
		}
		
		@ExceptionHandler(OnlyTeacherCanBeAssignedToSubjectException.class)
		public ResponseEntity<Object> handleOnlyTeacherCanBeAssignedToSubjectException(OnlyTeacherCanBeAssignedToSubjectException exception) {
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "subject can only assigned to  teacher");
		}
	
		@ExceptionHandler(AdminCannotBeAssignedToAcademicProgram.class)
		public ResponseEntity<Object> handleAdminCannotBeAssignedToAcademicProgram(AdminCannotBeAssignedToAcademicProgram exception) {
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "admin cannot be assigned to academic programs");
		}
	
}
