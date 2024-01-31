package com.school.sba.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Subject;


public interface ISubjectRepository extends JpaRepository<Subject, Integer>{

	Optional<Subject> findBySubjectName(String subject);

//	@Query(nativeQuery = true, value = "")
//	List<Subject> findAllByProgramId(int programId);

}