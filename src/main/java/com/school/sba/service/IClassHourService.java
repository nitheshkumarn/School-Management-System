package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;


public interface IClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> assignPeriods(List<ClassHourRequest> classHourRequest);

	ResponseEntity<ResponseStructure<String>> duplicateClassHoursForNextWeek(int programId);



}
