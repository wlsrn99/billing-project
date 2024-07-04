package com.billing.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.dto.VideoTop5DurationDTO;
import com.billing.dto.VideoTop5ViewDTO;
import com.billing.entity.VideoStatistic;

public interface VideoStatisticRepository extends JpaRepository<VideoStatistic, Long> {

	@Query("SELECT new com.billing.dto.VideoTop5ViewDTO(v.title, vs.dailyViewCount)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date = :date"
		+ " ORDER BY vs.dailyViewCount DESC")
	List<VideoTop5ViewDTO> findTop5ByDailyViewCount(@Param("date") LocalDate date, Pageable pageable);

	@Query("SELECT new com.billing.dto.VideoTop5ViewDTO(v.title, vs.weeklyViewCount)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date BETWEEN :startOfWeek AND :endOfWeek"
		+ " ORDER BY vs.weeklyViewCount DESC")
	List<VideoTop5ViewDTO> findTop5ByWeeklyViewCount(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek, Pageable pageable);

	@Query("SELECT new com.billing.dto.VideoTop5ViewDTO(v.title, vs.monthlyViewCount)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date BETWEEN :startOfMonth AND :endOfMonth"
		+ " ORDER BY vs.monthlyViewCount DESC")
	List<VideoTop5ViewDTO> findTop5ByMonthlyViewCount(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth, Pageable pageable);

	@Query("SELECT new com.billing.dto.VideoTop5DurationDTO(v.title, vs.dailyDuration)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date = :date"
		+ " ORDER BY vs.dailyDuration DESC")
	List<VideoTop5DurationDTO> findTop5ByDailyDuration(@Param("date") LocalDate date, Pageable pageable);

	@Query("SELECT new com.billing.dto.VideoTop5DurationDTO(v.title, vs.weeklyDuration)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date BETWEEN :startOfWeek AND :endOfWeek"
		+ " ORDER BY vs.weeklyDuration DESC")
	List<VideoTop5DurationDTO> findTop5ByWeeklyDuration(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek, Pageable pageable);

	@Query("SELECT new com.billing.dto.VideoTop5DurationDTO(v.title, vs.monthlyDuration)"
		+ " FROM Video v"
		+ " JOIN VideoStatistic vs ON v.id = vs.video.id"
		+ " WHERE vs.date BETWEEN :startOfMonth AND :endOfMonth"
		+ " ORDER BY vs.monthlyDuration DESC")
	List<VideoTop5DurationDTO> findTop5ByMonthlyDuration(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth, Pageable pageable);
}
