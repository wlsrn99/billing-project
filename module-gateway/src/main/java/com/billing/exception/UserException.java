package com.billing.exception;

import com.billing.common.exception.GlobalException;

public class UserException extends GlobalException {

	public UserException(final UserErrorCode errorCode) {
		super(errorCode.getStatus(), errorCode.getMessage());
	}

	public static class UserNotFoundException extends UserException {
		public UserNotFoundException(final UserErrorCode errorCode) {
			super(errorCode);
		}
	}

}