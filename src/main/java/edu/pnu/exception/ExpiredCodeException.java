package edu.pnu.exception;

public class ExpiredCodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExpiredCodeException(String message) {
		super(message);
	}

}
