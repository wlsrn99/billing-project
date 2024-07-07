package com.streaming.dto.bill;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillResponseMessage<T> {
	private String message;
	private T data;
}
