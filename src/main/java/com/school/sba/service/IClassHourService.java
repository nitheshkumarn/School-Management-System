package com.school.sba.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;


public interface IClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> assignPeriods(List<ClassHourRequest> classHourRequest);

	ResponseEntity<ResponseStructure<String>> duplicateClassHoursForThisWeek(int programId);

	String toWorkBook(int programId, ExcelRequest excelRequest);

	//ResponseEntity<byte[]> generateExcel(int programId, ExcelRequest excelRequest, MultipartFile file);

	//ResponseEntity<byte[]> generateExcel(int programId, int from, int to, MultipartFile file);

	ResponseEntity<?> generateExcel(int programId, LocalDate from, LocalDate to, MultipartFile file);

	



}
