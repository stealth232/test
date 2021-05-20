package ru.clevertec.check.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.clevertec.check.service.ControllerService;

import java.util.Objects;

import static ru.clevertec.check.service.CheckConstants.ZERO_INT;

@AllArgsConstructor
@Service
public class ControllerServiceImpl implements ControllerService {

    @Override
    public HttpStatus generateHttpStatus(Integer result) {
        if (result > ZERO_INT) return HttpStatus.OK;
        else return HttpStatus.NOT_MODIFIED;
    }

    @Override
    public HttpStatus generateHttpStatusForDeletion(Integer result) {
        if (result > ZERO_INT) return HttpStatus.OK;
        else return HttpStatus.NO_CONTENT;
    }

    @Override
    public HttpStatus generateHttpStatusForView(Object result) {
        if (Objects.nonNull(result)) return HttpStatus.OK;
        return HttpStatus.NO_CONTENT;
    }

    @Override
    public HttpStatus generateHttpStatusForSave(Object result) {
        if (Objects.nonNull(result)) return HttpStatus.CREATED;
        return HttpStatus.NOT_ACCEPTABLE;
    }

    @Override
    public HttpStatus generateHttpStatus(Object result) {
        if (Objects.nonNull(result)) return HttpStatus.OK;
        else return HttpStatus.NOT_MODIFIED;
    }
}
