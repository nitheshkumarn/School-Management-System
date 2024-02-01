package com.school.sba.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
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

				LocalDateTime starts = academicProgram.getClassHours().getFirst().getBeginsAt();
				LocalDateTime ends = academicProgram.getClassHours().getLast().getEndsAt();

				if ((LocalDate.now().isAfter(starts.toLocalDate())
						&& LocalDate.now().isBefore(ends.toLocalDate()) == false)
						&& (LocalDate.now().isAfter(academicProgram.getProgramBeginsAt())
								&& LocalDate.now().isBefore(academicProgram.getProgramEndsAt())))
					; //checking the existence of class hour must be validated

				LocalDate nextSaturday = null;
				if (LocalDate.now().equals(DayOfWeek.MONDAY) == false) {
					nextSaturday = LocalDate.now().plusWeeks(1).with(DayOfWeek.SATURDAY);

				} else
					nextSaturday = LocalDate.now().with(DayOfWeek.SATURDAY);

				int classHoursPerDay = schedule.getClassHoursPerDay();
				int classHourLengthInMinutes = (int) schedule.getClassHoursLengthInMin().toMinutes();
				LocalDate currentDate = LocalDate.now();

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

				while (currentDate.isBefore(nextSaturday)) {
					if (currentTime.getDayOfWeek() != DayOfWeek.SUNDAY) {

						for (int hour = 0; hour < classHoursPerDay + 2; hour++) {

							ClassHour classHour = new ClassHour();

							if (!currentTime.toLocalTime().equals(lunchTimeStart)
									&& !isLunchTime(currentTime, schedule)) {

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
					currentTime = currentTime.minusDays(1);
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

			if (user.getUserRole().equals(UserRole.TEACHER) && user.getSubject().equals(subject)
					&& user.getAcademicPrograms().contains(classHour.getAcademicProgram())) {

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

	@Override
	public ResponseEntity<ResponseStructure<String>> duplicateClassHoursForNextWeek(int programId) {
		AcademicProgram academicProgram = academicProgramRepo.findById(programId)
				.orElseThrow(() -> new AcademicProgramNotFoundException("Academic program not found"));

		duplicateClassHoursForNextWeek(academicProgram);

		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("Class hours duplicated for the next week successfully");
		structure.setData("Class hours duplicated for the next week successfully");

		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}

	private void duplicateClassHoursForNextWeek(AcademicProgram academicProgram) {
		List<ClassHour> classHoursForCurrentWeek = classHourRepo.findByAcademicProgramAndBeginsAtAfterAndBeginsAtBefore(
				academicProgram, LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS),
				LocalDateTime.now().with(DayOfWeek.SUNDAY).truncatedTo(ChronoUnit.DAYS));

		List<ClassHour> duplicatedClassHoursForNextWeek = new ArrayList<>();

		for (ClassHour classHour : classHoursForCurrentWeek) {
			ClassHour duplicatedClassHour = ClassHour.builder().academicProgram(academicProgram)
					.subject(classHour.getSubject()).beginsAt(classHour.getBeginsAt().plusWeeks(1))
					.endsAt(classHour.getEndsAt().plusWeeks(1)).roomNo(classHour.getRoomNo())
					.classStatus(classHour.getClassStatus()).user(classHour.getUser()).build();

			duplicatedClassHoursForNextWeek.add(duplicatedClassHour);
		}

		classHourRepo.saveAll(duplicatedClassHoursForNextWeek);
	}

}
