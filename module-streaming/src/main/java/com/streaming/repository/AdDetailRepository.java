package com.streaming.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.AdDetail;

public interface AdDetailRepository extends JpaRepository<AdDetail, Long> {

	List<AdDetail> findByOrderByPriorityAsc(Pageable pageable);
}
