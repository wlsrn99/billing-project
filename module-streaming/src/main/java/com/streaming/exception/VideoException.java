package com.streaming.exception;

public class VideoException extends GlobalException {
	public VideoException(final VideoErrorCode errorCode) {
		super(errorCode.getStatus(), errorCode.getMessage());
	}

	public static class VideoNotFoundException extends VideoException {
		public VideoNotFoundException(VideoErrorCode errorCode) {
			super(errorCode);
		}
	}

}
