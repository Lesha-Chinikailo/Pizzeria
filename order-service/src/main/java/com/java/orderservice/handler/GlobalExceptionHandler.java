package com.java.orderservice.handler;

import com.java.orderservice.exception.OrderNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice()
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({OrderNotFoundException.class})
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        exception.getConstraintViolations().forEach(System.out::println);
        Map<String, String> errorMap = new HashMap<>();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            errorMap.put(
                    violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMap);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, Locale locale) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {

            Object[] objects = new Object[error.getArguments().length - 1];
            for (int i = 0; i < objects.length; i++) {
                objects[i] = error.getArguments()[i + 1];
            }

            String message = messageSource.getMessage(error.getObjectName()+ "." + error.getField() + "." + error.getCode(),
                    objects,
                    locale);

            errorMap.put(error.getField(), message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMap);
    }

//    @ExceptionHandler({RuntimeException.class})
//    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(exception.getMessage());
//    }
}
