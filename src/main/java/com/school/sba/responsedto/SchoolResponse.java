package com.school.sba.responsedto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SchoolResponse {
	private Integer schoolId;
	private String schoolName;
	private Long schoolContactNumber;
	private String schoolEmailId;
	private String schoolAddress;
}
