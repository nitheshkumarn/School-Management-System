package com.school.sba.responsedto;


import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleResponse {
	
	private Integer scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private Integer classHoursPerDay;
	private Integer classHoursLengthInMin;
	private LocalTime breakTime;
	private Integer breakLengthInMin;
	private LocalTime lunchTime;
	private Integer lunchLengthInMin;
	

}
