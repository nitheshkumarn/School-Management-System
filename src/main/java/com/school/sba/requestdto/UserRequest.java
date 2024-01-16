package com.school.sba.requestdto;


import com.school.sba.entity.enums.UserRole;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {


	private String userName;
	private String userPass;
	private String userFirstName;
	private String userLastName;
	
	private long userContact;
	
	private String userEmail;
	private UserRole userRole;
}
