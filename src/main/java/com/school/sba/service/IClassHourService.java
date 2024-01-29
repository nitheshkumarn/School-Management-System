package com.school.sba.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.util.ResponseStructure;

@Service
public interface IClassHourService {

	ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId);

}
