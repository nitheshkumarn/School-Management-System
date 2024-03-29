package com.school.sba.service;



import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.util.ResponseStructure;

public interface IAcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> createProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest);

	ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(int schoolId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> softDeleteacademicProgram(Integer programId);




//	ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(int programId,
//			SubjectRequest subjectRequest);
}