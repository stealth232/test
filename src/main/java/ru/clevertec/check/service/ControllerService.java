package ru.clevertec.check.service;

import org.springframework.http.HttpStatus;

public interface ControllerService {

    HttpStatus generateHttpStatus(Integer result);

    HttpStatus generateHttpStatusForDeletion(Integer result);

    HttpStatus generateHttpStatusForView(Object result);

    HttpStatus generateHttpStatusForSave(Object result);

    HttpStatus generateHttpStatus(Object result);
}
