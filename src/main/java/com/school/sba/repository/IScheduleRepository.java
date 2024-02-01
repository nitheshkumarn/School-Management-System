package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.school.sba.entity.Schedule;

public interface IScheduleRepository extends JpaRepository<Schedule, Integer> {

}
