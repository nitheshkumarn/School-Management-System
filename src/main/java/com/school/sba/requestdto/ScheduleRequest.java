package com.school.sba.requestdto;


import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleRequest {
	
	private LocalTime opensAt;
	private LocalTime closesAt;
	private Integer classHoursPerDay;
	private Integer classHoursLengthInMin;
	private LocalTime breakTime;
	private Integer breakLengthInMin;
	private LocalTime lunchTime;
	private Integer lunchLengthInMin;
}
