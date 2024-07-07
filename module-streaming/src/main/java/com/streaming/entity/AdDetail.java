package com.streaming.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ad_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ad_detail_id")
	private Long id;

	private String title;

	private int priority;

	@Builder
	public AdDetail(String title, int priority) {
		this.title = title;
		this.priority = priority;
	}
}
