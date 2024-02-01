package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface ISchoolRepository extends JpaRepository<School, Integer>{
List<School> findByIsDeleted(boolean b);
}
