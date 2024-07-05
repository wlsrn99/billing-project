package com.billing.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseMessage<T> {
	private String message;
	private T data;
}
