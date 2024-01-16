package com.school.sba.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SchoolInsertionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	@Override
	public String getMessage() {
		return message;
	}

}
