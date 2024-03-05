package com.frozendo.pennysave.handler;

import com.frozendo.pennysave.enums.ApiMessageEnum;
import com.frozendo.pennysave.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionObject handleBusinessException(BusinessException ex) {
        return new ExceptionObject(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ExceptionObject> handleMethodArgumentException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(i -> ApiMessageEnum.getValueByKey(i.getDefaultMessage()))
                .map(i -> new ExceptionObject(i.getCode(), i.getMessage()))
                .toList();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionObject handleMethodException(Exception ex) {
        return new ExceptionObject("Error", ex.getMessage());
    }

}
