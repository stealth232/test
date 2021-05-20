package ru.clevertec.check.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.check.model.user.User;
import ru.clevertec.check.service.ControllerService;
import ru.clevertec.check.service.UserService;
import ru.clevertec.check.validators.UserValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@EnableAutoConfiguration
@RequestMapping("")
@RequiredArgsConstructor
public class StartController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final ControllerService cs;

    @PostMapping("/registration")
    public ResponseEntity<?> save(@RequestBody @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(userService.save(user), cs.generateHttpStatusForSave(user));
    }

    @GetMapping("/login")
    public ResponseEntity<Optional<User>> login(String login, String password) {
        return new ResponseEntity<>(userService.getUser(login, password),
                cs.generateHttpStatus(userService.getUser(login, password)));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, cs.generateHttpStatusForView(users));
    }
}
