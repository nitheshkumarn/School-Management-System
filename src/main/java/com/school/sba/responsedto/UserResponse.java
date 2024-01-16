package com.school.sba.responsedto;

import com.school.sba.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserResponse {

	private Integer userId;
	private String userName;
	private String userFirstName;
	private String userLastName;
	private long userContact;
	private String userEmail;
	private UserRole userRole;

}
