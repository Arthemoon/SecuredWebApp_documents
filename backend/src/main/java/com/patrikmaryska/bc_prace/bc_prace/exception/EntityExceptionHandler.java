package com.patrikmaryska.bc_prace.bc_prace.exception;

import javassist.NotFoundException;
import javassist.tools.web.BadHttpRequest;
import org.apache.http.HttpException;
import org.apache.http.conn.HttpHostConnectException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class EntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JDBCConnectionException.class)
    public final ResponseEntity<ErrorMessage> handleAllExceptions(JDBCConnectionException ex, WebRequest request) {

        ErrorMessage errorObj = new ErrorMessage(new Date(), "Could not get data! Try it again later!",
                request.getDescription(false));
        return new ResponseEntity<>(errorObj, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMissingRequestBody(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorMessage errorObj = new ErrorMessage(new Date(), "Wrong parameters occurred. Try it again please.",
                request.getDescription(false));
        return new ResponseEntity<>(errorObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> handleNotFound(HttpException ex, WebRequest request) {
        ErrorMessage errorObj = new ErrorMessage(new Date(), "Your requested URL was not found.",
                request.getDescription(false));
        return new ResponseEntity<>(errorObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<?> handleNotFound(NoResultException ex, WebRequest request) {
        ErrorMessage errorObj = new ErrorMessage(new Date(), "No result was found!",
                request.getDescription(false));
        return new ResponseEntity<>(errorObj, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("message", errors);

        return new ResponseEntity<>(body, headers, status);
    }
}

