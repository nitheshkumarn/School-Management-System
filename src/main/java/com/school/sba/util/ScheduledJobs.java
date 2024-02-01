package com.school.sba.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.IClassHourRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.serviceImpl.AcademicProgramServiceImpl;
import com.school.sba.serviceImpl.ClassHourServiceImplementation;
import com.school.sba.serviceImpl.UserServiceImpl;

@Component
public class ScheduledJobs {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AcademicProgramRepository academicProgramRepo;
	
	@Autowired
	UserServiceImpl usi;
	
	@Autowired
	AcademicProgramServiceImpl api;
	
	
	
	@Autowired
	ClassHourServiceImplementation chi;
	
	
	
	
	@Scheduled(fixedDelay = 1000l)
	void deleteUser() {
		
		List<User> users =userRepo.findByIsDeletedIsTrue();
		
		for (User user : users) {
			usi.hardDeleteUser(user.getUserId());
			System.out.println("deleted successfully");
		}
	}
		
		@Scheduled(fixedDelay = 1000l)
		void deleteAcademicProgram() {
			
			List<AcademicProgram> programs =academicProgramRepo.findByIsDeletedIsTrue();
			
			for (AcademicProgram program : programs) {
				api.hardDeleteAcademicProgram(program.getProgramId());
				System.out.println("deleted successfully");
			}
	}
		
		@Scheduled(cron = "0 0 0 ? * MON")
		void classHourWeekly() {
			
			AcademicProgram ap = academicProgramRepo.findById(1).orElseThrow();
			
			if(ap.isAutoRepeat()) {
				
				
				chi.duplicateClassHoursForNextWeek(ap);
				
			}
			
			
		}

}
