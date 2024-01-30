package com.school.sba.requestdto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.school.sba.entity.enums.ProgramType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcademicProgramRequest {

	private ProgramType programType;
	private String programName;
	private LocalDate programBeginsAt;
	private LocalDate programEndsAt;
	
}