package com.school.sba.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SchoolAlreadyExistException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String message;

	@Override
	public String getMessage() {
		return message;
	}
}
