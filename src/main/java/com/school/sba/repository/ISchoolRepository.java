package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface ISchoolRepository extends JpaRepository<School, Integer>{

}
