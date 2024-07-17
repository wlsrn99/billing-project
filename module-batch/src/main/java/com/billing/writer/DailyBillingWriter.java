package com.billing.writer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.billing.entity.VideoBill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyBillingWriter implements ItemWriter<VideoBill> {

	private final JdbcTemplate jdbcTemplate;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
	public void write(Chunk<? extends VideoBill> chunk) throws Exception {
		String sql = "INSERT INTO video_bills (video_id, date, daily_view_bill, daily_ad_bill, total_bill) " +
			"VALUES (?, ?, ?, ?, ?) " +
			"ON DUPLICATE KEY UPDATE " +
			"daily_view_bill = VALUES(daily_view_bill), " +
			"daily_ad_bill = VALUES(daily_ad_bill), " +
			"total_bill = VALUES(total_bill)";

		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					VideoBill bill = chunk.getItems().get(i);
					ps.setLong(1, bill.getVideoId());
					ps.setDate(2, java.sql.Date.valueOf(bill.getDate()));
					ps.setLong(3, bill.getDailyViewBill());
					ps.setLong(4, bill.getDailyAdBill());
					ps.setLong(5, bill.getTotalBill());
				}

				@Override
				public int getBatchSize() {
					return chunk.getItems().size();
				}
			});
			log.info("Successfully saved {} VideoBill items", chunk.size());
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation while saving VideoBill items", e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while saving VideoBill items", e);
			throw e;
		}
	}
}
