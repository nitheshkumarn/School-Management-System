package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassHourNotFoundByIdException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5288076155019738538L;
	private String message;

}
