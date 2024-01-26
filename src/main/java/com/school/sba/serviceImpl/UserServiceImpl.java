package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.entity.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.AdminAlreadyExistException;
import com.school.sba.exception.AdminCannotBeAssignedToAcademicProgram;
import com.school.sba.exception.AdminNotFoundException;
import com.school.sba.exception.OnlyTeacherCanBeAssignedToSubjectException;
import com.school.sba.exception.SubjectCannotBeAssignedToStudentException;
import com.school.sba.exception.SubjectNotFoundException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ISchoolRepository;
import com.school.sba.repository.ISubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.lUserService;
import com.school.sba.util.ResponseStructure;

@Service

public class UserServiceImpl implements lUserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ISubjectRepository subjectRepository;

	@Autowired
	private ISchoolRepository schoolRepository;

	@Autowired
	private AcademicProgramRepository academicProgramRepository;

	@Autowired
	private ResponseStructure<UserResponse> rsu;

	private User mapToUser(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).userEmail(userRequest.getUserEmail())
				.userPass(passwordEncoder.encode(userRequest.getUserPass()))
				.userFirstName(userRequest.getUserFirstName()).userLastName(userRequest.getUserLastName())
				.userRole(userRequest.getUserRole()).userContact(userRequest.getUserContact()).build();
	}

	private UserResponse mapToUserResponse(User user) {
		List<String> listOfProgramName = new ArrayList<>();

		if (user.getAcademicPrograms() != null) {
			user.getAcademicPrograms().forEach(academicProgram -> {
				listOfProgramName.add(academicProgram.getProgramName());
			});
		}

		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).userFirstName(user.getUserFirstName())
				.userLastName(user.getUserLastName()).userRole(user.getUserRole()).userContact(user.getUserContact())
				.academicPrograms(listOfProgramName).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {

		if (userRequest.getUserRole().equals(UserRole.ADMIN)) {

			if (userRepo.existsByIsDeletedAndUserRole(false, userRequest.getUserRole())) {
				throw new AdminAlreadyExistException("Admin already exist");
			} else {
				if (userRepo.existsByIsDeletedAndUserRole(true, userRequest.getUserRole())) {
					User user = userRepo.save(mapToUser(userRequest));

					rsu.setStatus(HttpStatus.CREATED.value());
					rsu.setMessage("user saved successfully");
					rsu.setData(mapToUserResponse(user));

					return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.CREATED);
				} else {
					User user = userRepo.save(mapToUser(userRequest));

					rsu.setStatus(HttpStatus.CREATED.value());
					rsu.setMessage("user saved successfully");
					rsu.setData(mapToUserResponse(user));

					return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.CREATED);
				}
			}
		} else {
			throw new AdminNotFoundException("You are not the Admin");
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUser(Integer userId) {
		return userRepo.findById(userId).map(user -> {
			rsu.setStatus(HttpStatus.FOUND.value());
			rsu.setMessage("user found successfully");
			rsu.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.FOUND);
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(Integer userId) {
		return userRepo.findById(userId).map(user -> {
			if (user.isDeleted()) {
				throw new UserNotFoundByIdException("User already deleted");
			}

			user.setDeleted(true);
			userRepo.save(user);

			rsu.setStatus(HttpStatus.OK.value());
			rsu.setMessage("user deleted successfully");
			rsu.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> assignUserToProgram(Integer programId, Integer userId) {
		return userRepo.findById(userId).map(user -> {
			if (user.getUserRole().equals(UserRole.ADMIN)) {
				throw new AdminCannotBeAssignedToAcademicProgram("admin cannot be assigned");
			} else {
				return academicProgramRepository.findById(programId).map(academicProgram -> {
					if (academicProgram.getListOfSubject().contains(user.getSubject())) {

						if (user.getUserRole().equals(UserRole.TEACHER)) {

							academicProgram.getUsers().add(user);
							user.getAcademicPrograms().add(academicProgram);

							userRepo.save(user);
							academicProgramRepository.save(academicProgram);

							rsu.setStatus(HttpStatus.OK.value());
							rsu.setMessage("assigned to academic program successfully");
							rsu.setData(mapToUserResponse(user));

							return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);

						} else {
							throw new SubjectCannotBeAssignedToStudentException("subject can't be assigned to subject");
						}
					} else {
						throw new SubjectNotFoundException("subject not found");
					}

				}).orElseThrow(() -> new AcademicProgramNotFoundException("academic program not found"));
			}
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(UserRequest userRequest) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userRequest.getUserRole().equals(UserRole.ADMIN)) {
			throw new AdminAlreadyExistException("admin already found");
		} else {
			return userRepo.findByUserName(username).map(admin -> {
				School school = admin.getSchool();

				User user = userRepo.save(mapToUser(userRequest));
				user.setSchool(school);
				user = userRepo.save(user);

				rsu.setStatus(HttpStatus.CREATED.value());
				rsu.setMessage(user.getUserRole().name().toLowerCase() + " saved successfully");
				rsu.setData(mapToUserResponse(user));

				return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.CREATED);

			}).orElseThrow(() -> new AdminNotFoundException("admin not found"));
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> updateUser(int userId, UserRequest userRequest) {
		return userRepo.findById(userId).map(user -> {
			User mappedUser = mapToUser(userRequest);
			mappedUser.setUserId(userId);
			user = userRepo.save(mappedUser);

			rsu.setStatus(HttpStatus.OK.value());
			rsu.setMessage("user updated successfully");
			rsu.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> assignSubjectToTeacher(int subjectId, int userId) {
		return userRepo.findById(userId).map(user -> {
			if (user.getUserRole().equals(UserRole.TEACHER)) {
				return subjectRepository.findById(subjectId).map(subject -> {
					user.setSubject(subject);
					userRepo.save(user);

					rsu.setStatus(HttpStatus.OK.value());
					rsu.setMessage("subject assigned to teacher successfully");
					rsu.setData(mapToUserResponse(user));

					return new ResponseEntity<ResponseStructure<UserResponse>>(rsu, HttpStatus.OK);
				}).orElseThrow(() -> new SubjectNotFoundException("subject not found"));
			} else {
				throw new OnlyTeacherCanBeAssignedToSubjectException("only teacher can be assigned to the subject");
			}
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found"));
	}

}
