package com.school.sba.requestdto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassHourRequest {
private int classHourId;
private int subjectId;
private int userId;
private int roomNo;
}
