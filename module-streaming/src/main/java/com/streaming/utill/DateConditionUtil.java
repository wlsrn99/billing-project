package com.streaming.utill;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.streaming.dto.bill.PeriodType;

@Component
public class DateConditionUtil {

	public BooleanExpression createDateCondition(DatePath<LocalDate> datePath, LocalDate inputDate, PeriodType periodType) {
		switch (periodType) {
			case DAILY:
				return datePath.eq(inputDate);
			case WEEKLY:
				LocalDate startOfWeek = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
				LocalDate endOfWeek = startOfWeek.plusDays(6);
				return datePath.between(startOfWeek, endOfWeek);
			case MONTHLY:
				LocalDate startOfMonth = inputDate.withDayOfMonth(1);
				LocalDate endOfMonth = inputDate.with(TemporalAdjusters.lastDayOfMonth());
				return datePath.between(startOfMonth, endOfMonth);
			default:
				throw new IllegalArgumentException("Invalid period type");
		}
	}
}
