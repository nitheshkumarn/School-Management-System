package com.school.sba.service;

import org.springframework.http.ResponseEntity;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.util.ResponseStructure;

public interface IScheduleService {
	ResponseEntity<ResponseStructure<ScheduleResponse>> saveSchedule(Integer schoolId, ScheduleRequest scheduleRequest);

	ResponseEntity<ResponseStructure<ScheduleResponse>> findSchedule(int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId, ScheduleRequest scheduleRequest);
}
