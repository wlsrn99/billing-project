package com.billing.exception;

public class UserException extends GlobalException {

	public UserException(final UserErrorCode errorCode) {
		super(errorCode.getStatus(), errorCode.getMessage());
	}

	public static class InvalidPasswordException extends UserException {
		public InvalidPasswordException(final UserErrorCode errorCode) {
			super(errorCode);
		}
	}

	public static class InvalidEmailException extends UserException {
		public InvalidEmailException(final UserErrorCode errorCode){
			super(errorCode);}
	}

	public static class EmailDuplicatedException extends UserException {
		public EmailDuplicatedException(final UserErrorCode errorCode) {
			super(errorCode);
		}
	}

	public static class UserNotFoundException extends UserException {
		public UserNotFoundException(final UserErrorCode errorCode) {
			super(errorCode);
		}
	}

	public static class UserUnauthorizedException extends UserException {
		public UserUnauthorizedException(final UserErrorCode errorCode) {
			super(errorCode);
		}
	}
}