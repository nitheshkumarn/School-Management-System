package com.school.sba.entity;

import java.time.Duration;
import java.time.LocalTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private Integer classHoursPerDay;
	private Duration classHoursLengthInMin;
	private LocalTime breakTime;
	private Duration breakLengthInMin;
	private LocalTime lunchTime;
	private Duration lunchLengthInMin;
	
}
