package edu.pnu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.pnu.domain.dto.ApiResponse;
import edu.pnu.exception.DuplicatedIdException;
import edu.pnu.exception.ExpiredCodeException;
import edu.pnu.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(DuplicatedIdException.class)
	public ResponseEntity<?> DuplicatedIdException(DuplicatedIdException e) {
		ApiResponse response = new ApiResponse(e.getMessage());
		return ResponseEntity.status(226).body(response);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> ResourceNotFoundException(ResourceNotFoundException e) {
		ApiResponse response = new ApiResponse(e.getMessage());
		return ResponseEntity.status(422).body(response);
	}
	
	@ExceptionHandler(ExpiredCodeException.class)
	public ResponseEntity<?> ExpiredCodeException(ExpiredCodeException e) {
		ApiResponse response = new ApiResponse(e.getMessage());
		return ResponseEntity.status(422).body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ApiResponse response = new ApiResponse(e.getMessage());
		return ResponseEntity.status(400).body(response);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> unexpectedException(Exception e) {
		e.printStackTrace();
		ApiResponse response = new ApiResponse(e.getMessage());
		return ResponseEntity.internalServerError().body(response);
	}
}
