package com.school.sba.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;

public interface IClassHourRepository extends JpaRepository<ClassHour, Integer>{
	
	

	boolean existsByBeginsAtAndRoomNo(LocalDateTime beginsAt, int roomNo);

}

