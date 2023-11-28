package com.cooksys.socialmedia.controllers.advice;

import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.model.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = { "com.cooksys.socialmedia.controllers" })
@ResponseBody
public class SocialmediaControllerAdvice {
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  public ErrorDto handleBadRequestException(BadRequestException badRequestException) {
    return new ErrorDto(badRequestException.getMessage());
  }
}
