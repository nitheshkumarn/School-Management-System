package com.school.sba.entity;



import java.util.List;

import com.school.sba.entity.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@Column(unique = true)
	private String userName;
	private String userPass;
	private String userFirstName;
	private String userLastName;
	
	@Column(unique = true)
	private long userContact;
	
	@Column(unique = true)
	private String userEmail;
	
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	private boolean isDeleted;
	
	@ManyToOne
	private School school;
	
	@ManyToMany
	private List<AcademicProgram> academicPrograms;
	
	@ManyToOne
	private Subject subject;
	

}
