package edu.pnu.exception;

public class DuplicatedIdException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public DuplicatedIdException(String message) {
		super(message);
	}

}
