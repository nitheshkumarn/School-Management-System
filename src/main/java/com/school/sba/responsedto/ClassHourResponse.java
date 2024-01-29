package com.school.sba.responsedto;

import java.time.LocalTime;

import com.school.sba.entity.enums.ClassStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassHourResponse {

	private int classHourId;
	private LocalTime beginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;
}
