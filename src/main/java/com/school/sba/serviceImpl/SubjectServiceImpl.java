package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ISubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.ISubjectService;
import com.school.sba.util.ResponseStructure;


@Service
public class SubjectServiceImpl implements ISubjectService{

	@Autowired
	private ISubjectRepository subjectRepository;

	@Autowired
	private AcademicProgramRepository academicProgramRepository;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;
	
	@Autowired
	private ResponseStructure<List<SubjectResponse>> listStructure;
	
	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;
	
	private List<SubjectResponse> mapTOListOfSubjectResponse(List<Subject> listOfSubjects) {
		List<SubjectResponse> listOfSubjectResponse = new ArrayList<>();

		listOfSubjects.forEach(subject -> {
			SubjectResponse sr = new SubjectResponse();
			sr.setSubjectId(subject.getSubjectId());
			sr.setSubjectNames(subject.getSubjectName()); 
			listOfSubjectResponse.add(sr);
		});

		return listOfSubjectResponse;
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(int programId, SubjectRequest subjectRequest) {
		return academicProgramRepository.findById(programId)
				.map(academicProgram -> {
					List<Subject> listOfSubjects = new ArrayList<Subject>();
					
					subjectRequest.getSubjectNames().forEach(name -> {
						Subject fetchedSubject = subjectRepository.findBySubjectName(name.toLowerCase()).map(subject -> {
							return subject;
						}).orElseGet( () -> {
							Subject subject = new Subject();
							subject.setSubjectName(name.toLowerCase());
							subjectRepository.save(subject);
							return subject;
						});
						listOfSubjects.add(fetchedSubject);
					});
					
					academicProgram.setListOfSubject(listOfSubjects);
					academicProgramRepository.save(academicProgram);
					
					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("subjects have been updated successfully");
					structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));
					
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
					
				})
				.orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects() {
		List<Subject> listOfSubjects = subjectRepository.findAll();

		if(listOfSubjects.isEmpty()) {
			listStructure.setStatus(HttpStatus.NOT_FOUND.value());
			listStructure.setMessage("No subjects found");
			listStructure.setData(mapTOListOfSubjectResponse(listOfSubjects));

			return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(listStructure, HttpStatus.NOT_FOUND);
		}
		else {
			listStructure.setStatus(HttpStatus.FOUND.value());
			listStructure.setMessage("list of subjects found");
			listStructure.setData(mapTOListOfSubjectResponse(listOfSubjects));

			return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(listStructure, HttpStatus.FOUND);
		}

	}


//	@Override
//	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(int programId,
//			SubjectRequest subjectRequest) {
//		
//		return academicProgramRepository.findById(programId)
//		.map(academicProgram -> {
//			
//			List<Subject> listOfSubjects = new ArrayList<Subject>();
//			
//			List<Subject> listOfSubjectsFromDB = subjectRepository.findAll();
//			
//			List<String> subjectNames = subjectRequest.getSubjectNames();
//			 
//			Set<String> setOfSubjectNames = new HashSet<String>();
//
//			subjectNames.forEach(name -> {
//				setOfSubjectNames.add(name.toLowerCase());
//			});
//			
//			listOfSubjectsFromDB.forEach(sub -> {
//				
//				boolean check = setOfSubjectNames.add(sub.getSubjectName().toLowerCase());
//				if(check) {
//					listOfSubjects.add(sub);
//				}
//				else {
//					setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
//				}
//				
//			});
//
//            listOfSubjectsFromDB.forEach(sub -> {
//                subjectNames.forEach(name -> {
//                    boolean b = sub.getSubjectName().toLowerCase().equals(name.toLowerCase());
//
//                    if(b) {
//                    	listOfSubjects.add(sub);
//                    }
//                    if(b == false){
//                        setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
//                    }
//
//                });
//            });
//
//			setOfSubjectNames.forEach(name -> {
//				Subject subject = new Subject();
//				subject.setSubjectName(name);
//				listOfSubjects.add(subject);
//				subjectRepository.save(subject);
//			});
//			System.out.println(setOfSubjectNames);
//			
//			academicProgram.setListOfSubject(listOfSubjects);
//			academicProgramRepository.save(academicProgram);
//			
//			structure.setStatus(HttpStatus.CREATED.value());
//			structure.setMessage("subjects have been updated successfully");
//			structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));
//			
//			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
//			
//		})
//		.orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
//	
//	}


}