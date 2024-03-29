package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.IClassHourRepository;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.repository.ISubjectRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.IAcademicProgramService;
import com.school.sba.util.ResponseStructure;

import jakarta.transaction.Transactional;

@Service
public class AcademicProgramServiceImpl implements IAcademicProgramService {

	@Autowired
	private AcademicProgramRepository academicProgramRepository;

	@Autowired
	private ISubjectRepository subjectRepository;

	@Autowired
	private ISchoolRepository schoolRepository;
	
	@Autowired
	private IClassHourRepository classHourRepo;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;

	@Autowired
	private ResponseStructure<List<AcademicProgramResponse>> listStructure;

	public AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {

		List<String> subjects = new ArrayList<String>();
		List<Subject> listOfSubject = academicProgram.getListOfSubject();

		if (listOfSubject != null) {
			listOfSubject.forEach(sub -> {
				subjects.add(sub.getSubjectName());
			});
		}
		return AcademicProgramResponse.builder().programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType()).programName(academicProgram.getProgramName())
				.programBeginsAt(academicProgram.getProgramBeginsAt()).programEndsAt(academicProgram.getProgramEndsAt())
				.listOfSubjects(subjects).build();
	}

	private AcademicProgram mapToAcademicProgram(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder().programType(academicProgramRequest.getProgramType())
				.programName(academicProgramRequest.getProgramName())
				.programBeginsAt(academicProgramRequest.getProgramBeginsAt())
				.programEndsAt(academicProgramRequest.getProgramEndsAt()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest) {
		return schoolRepository.findById(schoolId).map(school -> {
			AcademicProgram academicProgram = academicProgramRepository
					.save(mapToAcademicProgram(academicProgramRequest));

			school.getListOfAcademicPrograms().add(academicProgram);

			school = schoolRepository.save(school);
			academicProgram.setSchool(school);

			academicProgram = academicProgramRepository.save(academicProgram);

			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Academic program created successfully");
			structure.setData(mapToAcademicProgramResponse(academicProgram));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
		}).orElseThrow(() -> new SchoolNotFoundByIdException("school not found"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(int schoolId) {
		return schoolRepository.findById(schoolId).map(school -> {
			List<AcademicProgram> listOfAcadmicProgram = academicProgramRepository.findAll();

			List<AcademicProgramResponse> listOfAcademicProgramResponse = listOfAcadmicProgram.stream()
					.map(this::mapToAcademicProgramResponse).collect(Collectors.toList());

			if (listOfAcadmicProgram.isEmpty()) {
				listStructure.setStatus(HttpStatus.NO_CONTENT.value());
				listStructure.setMessage("no programs has been found");
				listStructure.setData(listOfAcademicProgramResponse);

				return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(listStructure,
						HttpStatus.NO_CONTENT);
			} else {
				listStructure.setStatus(HttpStatus.FOUND.value());
				listStructure.setMessage("found list of academic programs");
				listStructure.setData(listOfAcademicProgramResponse);

				return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(listStructure,
						HttpStatus.FOUND);
			}
		}).orElseThrow(() -> new SchoolNotFoundByIdException("school not found"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> softDeleteacademicProgram(Integer programId) {
		return academicProgramRepository.findById(programId).map(ap -> {
			if (ap.isDeleted()) {
				throw new AcademicProgramNotFoundException("academic program already deleted");
			}

			ap.setDeleted(true);
			academicProgramRepository.save(ap);

			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("academic program deleted successfully");
			structure.setData(mapToAcademicProgramResponse(ap));

			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.OK);
		}).orElseThrow(() -> new AcademicProgramNotFoundException("school not found"));

	}
	
	@Transactional
	public void hardDeleteAcademicProgram(int academicProgramId) {
		AcademicProgram ac = academicProgramRepository.findById(academicProgramId).orElseThrow(()-> new AcademicProgramNotFoundException("academic program not found "));
		classHourRepo.deleteAll(ac.getClassHours());
		academicProgramRepository.delete(ac);
	}
	

	
	

//	@Override
//	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(int programId,
//			SubjectRequest subjectRequest) {
//		return academicProgramRepository.findById(programId).map(academicProgram -> {
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
//				if (check) {
//					listOfSubjects.add(sub);
//				} else {
//					setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
//				}
//
//			});
//
//			listOfSubjectsFromDB.forEach(sub -> {
//				subjectNames.forEach(name -> {
//					boolean b = sub.getSubjectName().toLowerCase().equals(name.toLowerCase());
//
//					if (b) {
//						listOfSubjects.add(sub);
//					}
//					if (b == false) {
//						setOfSubjectNames.remove(sub.getSubjectName().toLowerCase());
//					}
//
//				});
//			});
//
//			setOfSubjectNames.forEach(name -> {
//				Subject subject = new Subject();
//				subject.setSubjectName(name);
//				listOfSubjects.add(subject);
//				subjectRepository.save(subject);
//			});
//
//			System.out.println(setOfSubjectNames);
//
//			academicProgram.setListOfSubject(listOfSubjects);
//			academicProgramRepository.save(academicProgram);
//
//			structure.setStatus(HttpStatus.CREATED.value());
//			structure.setMessage("subjects have been updated successfully");
//			structure.setData(mapToAcademicProgramResponse(academicProgram));
//
//			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
//
//		}).orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
//
//	}
//
}
