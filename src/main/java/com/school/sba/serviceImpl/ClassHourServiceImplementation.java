package com.school.sba.serviceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.entity.enums.ClassStatus;
import com.school.sba.entity.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.ClassHourNotFoundByIdException;
import com.school.sba.exception.RoomIsOccupiedException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SubjectNotAssignedToClassHourException;
import com.school.sba.exception.SubjectNotFoundException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.IClassHourRepository;
import com.school.sba.repository.ISubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.IClassHourService;
import com.school.sba.util.ResponseStructure;

@Service
public class ClassHourServiceImplementation implements IClassHourService {

	@Autowired
	private IClassHourRepository classHourRepo;

	@Autowired
	private AcademicProgramRepository academicProgramRepo;

	@Autowired
	private ResponseStructure<String> structure;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ISubjectRepository subjectRepo;

	@Autowired
	private ResponseStructure<List<ClassHourResponse>> listStructure;

	private List<ClassHourResponse> mapTOListOfClassHourResponse(List<ClassHour> listOfClassHours) {
		List<ClassHourResponse> listOfClassHourResponse = new ArrayList<>();

		listOfClassHours.forEach(classHour -> {
			ClassHourResponse cr = new ClassHourResponse();
			cr.setClassHourId(classHour.getClassHourId());
			cr.setBeginsAt(classHour.getBeginsAt());
			cr.setEndsAt(classHour.getEndsAt());
			cr.setRoomNo(classHour.getRoomNo());
			cr.setClassStatus(classHour.getClassStatus());
			listOfClassHourResponse.add(cr);
		});

		return listOfClassHourResponse;
	}

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

		return academicProgramRepo.findById(programId).map(academicProgram -> {

			Schedule schedule = academicProgram.getSchool().getSchedule();

			if (schedule != null) {

				int classHoursPerDay = schedule.getClassHoursPerDay();
				int classHourLengthInMinutes = (int) schedule.getClassHoursLengthInMin().toMinutes();

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

				for (int day = 1; day <= 6; day++) {

					for (int hour = 0; hour < classHoursPerDay + 2; hour++) {

						ClassHour classHour = new ClassHour();

						if (!currentTime.toLocalTime().equals(lunchTimeStart) && !isLunchTime(currentTime, schedule)) {

							if (!currentTime.toLocalTime().equals(breakTimeStart)
									&& !isBreakTime(currentTime, schedule)) {
								LocalDateTime beginsAt = currentTime;
								LocalDateTime endsAt = beginsAt.plusMinutes(classHourLengthInMinutes);
								System.out.println("inside if start time " + beginsAt);
								System.out.println("inside if ends time " + endsAt);

								classHour.setBeginsAt(beginsAt);
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.NOT_SCHEDULED);

								currentTime = endsAt;
							} else {
								System.out.println("inside else");
								classHour.setBeginsAt(currentTime);
								classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));

								classHour.setClassStatus(ClassStatus.BREAK_TIME);
								currentTime = currentTime.plusMinutes(schedule.getBreakLengthInMin().toMinutes());
							}

						} else {
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
			} else {
				throw new ScheduleNotFoundException("schedule not found");
			}
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("ClassHour generated successfully for the academic progarm");
			structure.setData("Class Hour generated for the current week successfully");

			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
		}).orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> assignPeriods(
			List<ClassHourRequest> classHourRequest) {

		List<ClassHour> listOfClassHours = new ArrayList<>();
		for (ClassHourRequest classH : classHourRequest) {
			User user = userRepo.findById(classH.getUserId())
					.orElseThrow(() -> new UserNotFoundByIdException("User Not Present"));
			Subject subject = subjectRepo.findById(classH.getSubjectId())
					.orElseThrow(() -> new SubjectNotFoundException("subject Not Found Exception"));

			ClassHour classHour = classHourRepo.findById(classH.getClassHourId())
					.orElseThrow(() -> new ClassHourNotFoundByIdException("ClassHour not found for the given id"));

			if (user.getUserRole().equals(UserRole.TEACHER) && user.getSubject().equals(subject) && user.getAcademicPrograms().contains(classHour.getAcademicProgram())) {

				if (classHourRepo.existsByBeginsAtAndRoomNo(classHour.getBeginsAt(), classHour.getRoomNo()) == false) {

					LocalDateTime currentTime = LocalDateTime.now();
					if (currentTime.isAfter(classHour.getBeginsAt()) && currentTime.isBefore(classHour.getEndsAt())) {
						classHour.setUser(user);
						classHour.setClassStatus(ClassStatus.ONGOING);
						classHour.setRoomNo(classH.getRoomNo());
						classHour.setSubject(subject);
						listOfClassHours.add(classHour);
					}

					else if (currentTime.isAfter(classHour.getEndsAt())) {
						classHour.setUser(user);
						classHour.setClassStatus(ClassStatus.FINISHED);
						classHour.setRoomNo(classH.getRoomNo());
						classHour.setSubject(subject);
						listOfClassHours.add(classHour);
					}

					else {

						classHour.setUser(user);
						classHour.setClassStatus(ClassStatus.UPCOMING);
						classHour.setRoomNo(classH.getRoomNo());
						classHour.setSubject(subject);
						listOfClassHours.add(classHour);
					}
				} else
					throw new RoomIsOccupiedException("Room is currently occupied");

			} else
				throw new SubjectNotAssignedToClassHourException("ClassHour Couldnt be assigned ");
		}

		classHourRepo.saveAll(listOfClassHours);

		listStructure.setStatus(HttpStatus.FOUND.value());
		listStructure.setMessage("list of classHours found");
		listStructure.setData(mapTOListOfClassHourResponse(listOfClassHours));

		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(listStructure, HttpStatus.FOUND);

	}

}
