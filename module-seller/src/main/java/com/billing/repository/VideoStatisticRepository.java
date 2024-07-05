package com.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.dto.TotalBillDTO;
import com.billing.dto.VideoBillDTO;
import com.billing.entity.VideoStatistic;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface VideoStatisticRepository extends JpaRepository<VideoStatistic, Long> {

	@Query("SELECT new com.billing.dto.TotalBillDTO(vs.videoId, v.title, SUM(vs.dailyViewBill + vs.dailyAdBill), SUM(vs.dailyViewCount), SUM(vs.dailyAdViewCount)) "
		+ "FROM VideoStatistic vs "
		+ "JOIN Video v ON vs.videoId = v.id "
		+ "WHERE vs.date BETWEEN :startDate AND :endDate "
		+ "AND vs.videoId = :videoId "
		+ "GROUP BY vs.videoId, v.title")
	TotalBillDTO findTotalBillByVideo(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("videoId") Long videoId);


	@Query("SELECT new com.billing.dto.VideoBillDTO(vs.videoId, v.title, SUM(vs.dailyViewBill), SUM(vs.dailyViewCount)) "
		+ "FROM VideoStatistic vs "
		+ "JOIN Video v ON vs.videoId = v.id "
		+ "WHERE vs.date BETWEEN :startDate AND :endDate "
		+ "AND vs.videoId = :videoId "
		+ "GROUP BY vs.videoId, v.title")
	VideoBillDTO findViewBillByVideo(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("videoId") Long videoId);

	@Query("SELECT new com.billing.dto.VideoBillDTO(vs.videoId, v.title, SUM(vs.dailyAdBill), SUM(vs.dailyAdViewCount)) "
		+ "FROM VideoStatistic vs "
		+ "JOIN Video v ON vs.videoId = v.id "
		+ "WHERE vs.date BETWEEN :startDate AND :endDate "
		+ "AND vs.videoId = :videoId "
		+ "GROUP BY vs.videoId, v.title")
	VideoBillDTO findAdBillByVideo(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("videoId") Long videoId);


}

