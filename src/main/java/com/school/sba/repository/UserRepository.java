package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.entity.enums.UserRole;


public interface UserRepository extends JpaRepository<User, Integer> {
	
	boolean existsByUserRole(UserRole userRole);
	
	boolean existsByIsDeletedAndUserRole(boolean b, UserRole userRole);
	
	Optional<User> findByUserName(String username);

	List<User> findByUserRoleAndAcademicPrograms(UserRole role, AcademicProgram academicProgram);
	
	List<User> findByIsDeletedIsTrue();
}
