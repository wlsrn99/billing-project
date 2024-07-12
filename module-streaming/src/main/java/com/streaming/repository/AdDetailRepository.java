package com.streaming.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.aop.annotations.ReadOnly;
import com.streaming.entity.AdDetail;

public interface AdDetailRepository extends JpaRepository<AdDetail, Long> {

	@ReadOnly
	@Transactional(readOnly = true)
	List<AdDetail> findByOrderByPriorityAsc(Pageable pageable);
}
