package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface IClassHourRepository extends JpaRepository<ClassHour, Integer>{
	
	

	boolean existsByBeginsAtAndRoomNo(LocalDateTime beginsAt, int roomNo);
	
	List<ClassHour> findByUser(User user);



	List<ClassHour> findByAcademicProgramAndBeginsAtAfterAndBeginsAtBefore(AcademicProgram academicProgram,
			LocalDateTime plusDays, LocalDateTime plusDays2);

	List<ClassHour> findByAcademicProgramAndBeginsAtAfterAndEndsAtBefore(AcademicProgram academicProgram,
			LocalDateTime truncatedTo, LocalDateTime truncatedTo2);

	List<ClassHour> findAllByAcademicProgramAndBeginsAtBetween(AcademicProgram academicProgram,
			LocalDateTime fromDateTime, LocalDateTime toDateTime);

}

