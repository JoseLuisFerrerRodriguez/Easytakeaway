package com.proyecto.easytakeaway.excepciones;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundError(Model model) {
        String errorMessage = "OOops! Hay algo que ha ido mal.";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @ExceptionHandler(AplicationIException.class)
    public String AplicationExcepcion(Model model) {
        String errorMessage = "OOops! Hay algo que ha ido mal.";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}

