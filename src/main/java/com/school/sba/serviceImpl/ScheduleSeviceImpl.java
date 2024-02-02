package com.school.sba.serviceImpl;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exception.InvalidScheduleBreakTimeException;
import com.school.sba.exception.InvalidScheduleClassStartsException;
import com.school.sba.exception.InvalidScheduleCloseTimeException;
import com.school.sba.exception.InvalidScheduleLunchTimeException;
import com.school.sba.exception.ScheduleAlreadyPresentException;
import com.school.sba.exception.ScheduleNotFoundException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.IScheduleRepository;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.IScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleSeviceImpl implements IScheduleService {

	@Autowired
	private ResponseStructure<ScheduleResponse> responseS;

	@Autowired
	private IScheduleRepository scheduleRepo;

	@Autowired
	private ISchoolRepository schoolRepo;

	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
		return Schedule.builder().opensAt(scheduleRequest.getOpensAt()).breakTime(scheduleRequest.getBreakTime())
				.breakLengthInMin(Duration.ofMinutes(scheduleRequest.getBreakLengthInMin()))
				.closesAt(scheduleRequest.getClosesAt()).classHoursPerDay(scheduleRequest.getClassHoursPerDay())
				.classHoursLengthInMin(Duration.ofMinutes(scheduleRequest.getClassHoursLengthInMin()))
				.lunchTime(scheduleRequest.getLunchTime())
				.lunchLengthInMin(Duration.ofMinutes(scheduleRequest.getLunchLengthInMin())).build();
	}

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {

		return ScheduleResponse.builder().scheduleId(schedule.getScheduleId()).opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt()).classHoursPerDay(schedule.getClassHoursPerDay())
				.classHoursLengthInMin(
						(int) (Duration.ofMinutes(schedule.getClassHoursLengthInMin().toMinutes()).toMinutes()))
				.breakTime(schedule.getBreakTime())
				.breakLengthInMin(((int) (Duration.ofMinutes(schedule.getBreakLengthInMin().toMinutes()).toMinutes())))
				.lunchLengthInMin((int) (Duration.ofMinutes(schedule.getLunchLengthInMin().toMinutes()).toMinutes()))
				.lunchTime(schedule.getLunchTime()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> saveSchedule(Integer schoolId,
			ScheduleRequest scheduleRequest) {

		return schoolRepo.findById(schoolId).map(s -> {
			if (s.getSchedule() == null) {
				
				LocalTime opensAt = scheduleRequest.getOpensAt();
				LocalTime closesAt = scheduleRequest.getClosesAt();
				long classHourLength = Duration.ofMinutes(scheduleRequest.getClassHoursLengthInMin()).toMinutes();
				long breakHourLength = Duration.ofMinutes(scheduleRequest.getBreakLengthInMin()).toMinutes();
				long lunchHourLength = Duration.ofMinutes(scheduleRequest.getLunchLengthInMin()).toMinutes();
				LocalTime lunchTime = scheduleRequest.getLunchTime();
				LocalTime breakTime = scheduleRequest.getBreakTime();

				if (closesAt.isBefore(opensAt) || closesAt.isBefore(breakTime) || closesAt.isBefore(lunchTime))
					throw new InvalidScheduleCloseTimeException("Close Time must be after Open Time");

				LocalTime classStarts = null;
				for (int i = 0; i < scheduleRequest.getClassHoursPerDay() + 2; i++) {
					classStarts = opensAt;
					LocalTime classEnds = classStarts.plusMinutes(classHourLength);
					
					if (breakTime.isBefore(classEnds) && breakTime.isAfter(classStarts))
						throw new InvalidScheduleBreakTimeException("break time should be before Close time");
					else {
						if (breakTime.equals(classEnds)) {
							opensAt = breakTime.plusMinutes(breakHourLength);
							continue;
						}
					}

					if (lunchTime.isBefore(classEnds) && lunchTime.isAfter(classStarts))
						throw new InvalidScheduleLunchTimeException("lunch time should be before Close time");
					else {
						if (lunchTime.equals(classEnds)) {
							opensAt = lunchTime.plusMinutes(lunchHourLength);
							continue;
						}
					}

					opensAt = classEnds;
				}
				
				if (!classStarts.minusHours(1).equals(closesAt))
					throw new InvalidScheduleClassStartsException("Closes At is not valid for the classes");

				
				Schedule saveSchedule = scheduleRepo.save(mapToSchedule(scheduleRequest));
				s.setSchedule(saveSchedule);
				schoolRepo.save(s);
				responseS.setStatus(HttpStatus.CREATED.value());
				responseS.setMessage("Schedule data inserted successfully");
				responseS.setData(mapToScheduleResponse(saveSchedule));

				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseS, HttpStatus.CREATED);
			} else
				throw new ScheduleAlreadyPresentException("Schedule already Present");
		}).orElseThrow(() -> new SchoolNotFoundByIdException("School not found for the given Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId) {

		School school = schoolRepo.findById(schoolId)
				.orElseThrow(() -> new SchoolNotFoundByIdException("School not found"));

		return scheduleRepo.findById(school.getSchedule().getScheduleId()).map(schedule -> {
			responseS.setStatus(HttpStatus.FOUND.value());
			responseS.setMessage("schedule found");
			responseS.setData(mapToScheduleResponse(schedule));

			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseS, HttpStatus.FOUND);
		}).orElseThrow(() -> new ScheduleNotFoundException("schedule not found"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId,
			ScheduleRequest scheduleRequest) {

		return scheduleRepo.findById(scheduleId).map(schedule -> {
			Schedule mapToSchedule = mapToSchedule(scheduleRequest);
			mapToSchedule.setScheduleId(scheduleId);
			schedule = scheduleRepo.save(mapToSchedule);

			responseS.setStatus(HttpStatus.OK.value());
			responseS.setMessage("schedule updated successfully");
			responseS.setData(mapToScheduleResponse(schedule));

			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseS, HttpStatus.OK);
		}).orElseThrow(() -> new ScheduleNotFoundException("schedule not found"));

	}

	public ResponseEntity<ResponseStructure<ScheduleResponse>> deleteSchedule(Schedule schedule) {

		schedule = scheduleRepo.findById(schedule.getScheduleId())
				.orElseThrow(() -> new ScheduleNotFoundException("User Not Found"));

		scheduleRepo.deleteById(schedule.getScheduleId());

		responseS.setStatus(HttpStatus.OK.value());
		responseS.setMessage("user data DELETED successfully");
		responseS.setData(mapToScheduleResponse(schedule));

		return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseS, HttpStatus.OK);
	}
}
