package com.school.sba.serviceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.enums.ClassStatus;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.IClassHourRepository;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.IClassHourService;
import com.school.sba.util.ResponseStructure;

public class ClassHourServiceImplementation implements IClassHourService {

	@Autowired
	private IClassHourRepository classHourRepo;

	@Autowired
	private AcademicProgramRepository academicProgramRepo;

	@Autowired
	private ResponseStructure<String> structure;
	
	

	

	private boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMin().toMinutes());

		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}

	private boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime LunchTimeStart = schedule.getLunchTime();
		LocalTime LunchTimeEnd = LunchTimeStart.plusMinutes(schedule.getLunchLengthInMin().toMinutes());

		return (currentTime.toLocalTime().isAfter(LunchTimeStart) && currentTime.toLocalTime().isBefore(LunchTimeEnd));

	}

	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHourForProgram(int programId) {

		return academicProgramRepo.findById(programId)
				.map(academicProgram -> {

					Schedule schedule = academicProgram.getSchool().getSchedule();

					if(schedule != null) {

						int classHoursPerDay = schedule.getClassHoursPerDay();
						int classHourLengthInMinutes = (int)schedule.getClassHoursLengthInMin().toMinutes();

						LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
						System.out.println(currentTime);

						LocalTime breakTimeStart = schedule.getBreakTime();
						System.out.println(breakTimeStart);
						LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMin().toMinutes());
						System.out.println(breakTimeEnd);

						LocalTime lunchTimeStart = schedule.getLunchTime();
						System.out.println(lunchTimeStart);

						LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMin().toMinutes());
						System.out.println(lunchTimeEnd);

						for(int day = 1; day <= 6; day++) {

							for(int hour = 0; hour<classHoursPerDay+2;hour++) {

								ClassHour classHour = new ClassHour();

								if(!currentTime.toLocalTime().equals(lunchTimeStart) && !isLunchTime(currentTime, schedule)) {

									if(!currentTime.toLocalTime().equals(breakTimeStart) && !isBreakTime(currentTime, schedule))
									{
										LocalDateTime beginsAt = currentTime;
										LocalDateTime endsAt = beginsAt.plusMinutes(classHourLengthInMinutes);
										System.out.println("inside if start time "+ beginsAt);
										System.out.println("inside if ends time "+ endsAt);

										classHour.setBeginsAt(beginsAt);
										classHour.setEndsAt(endsAt);
										classHour.setClassStatus(ClassStatus.NOT_SCHEDULED);

										currentTime = endsAt;
									}
									else
									{
										System.out.println("inside else");
										classHour.setBeginsAt(currentTime);
										classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));

										classHour.setClassStatus(ClassStatus.BREAK_TIME);
										currentTime = currentTime.plusMinutes(schedule.getBreakLengthInMin().toMinutes());
									}

								}
								else {
									classHour.setBeginsAt(currentTime);
									classHour.setEndsAt(LocalDateTime.now().with(lunchTimeEnd));
									classHour.setClassStatus(ClassStatus.LUNCH_TIME);
									currentTime = currentTime.plusMinutes(schedule.getLunchLengthInMin().toMinutes());
								}
								classHour.setAcademicProgram(academicProgram);
								classHourRepo.save(classHour);
							}
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
						}
					}
					else {
						throw new ScheduleNotFoundException("schedule not found");
					}
					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("ClassHour generated successfully for the academic progarm");
					structure.setData("Class Hour generated for the current week successfully");

					return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
				})
				.orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
	}

}
