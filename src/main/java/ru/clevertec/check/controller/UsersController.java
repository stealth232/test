package ru.clevertec.check.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.check.model.product.Order;
import ru.clevertec.check.model.user.User;
import ru.clevertec.check.service.ControllerService;
import ru.clevertec.check.service.DataOrderService;
import ru.clevertec.check.service.UserService;

import java.util.List;

import static javax.mail.event.FolderEvent.DELETED;
import static ru.clevertec.check.service.CheckConstants.DONT_EXIST;
import static ru.clevertec.check.service.CheckConstants.ZERO_INT;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/admin/users", produces = "application/json")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final ControllerService cs;
    private final DataOrderService dos;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, cs.generateHttpStatusForView(users));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        Integer deleted = userService.deleteUserById(id);
        return deleted > ZERO_INT ? new ResponseEntity<>(DELETED + id, cs.generateHttpStatusForDeletion(deleted)) :
                new ResponseEntity<>(DONT_EXIST, cs.generateHttpStatusForDeletion(deleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, cs.generateHttpStatusForView(user));
    }

    @GetMapping("/users/roles")
    public ResponseEntity<?> getUsersRoles() {
        return new ResponseEntity<>(userService.getUserByRoles(),
                cs.generateHttpStatusForView(userService.getUserByRoles()));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Integer id) {
        List<Order> orders = dos.getOrdersByUserId(id);
        return new ResponseEntity<>(orders, cs.generateHttpStatusForView(orders));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deleteOrdersByUserId(@PathVariable Integer id) {
        Integer deleted = dos.removeDataOrdersByUserId(id);
        return new ResponseEntity<>(deleted,
                cs.generateHttpStatusForDeletion(deleted));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeRole(@PathVariable Integer id) {
        return new ResponseEntity<>(cs.generateHttpStatus(userService.changeRole(id)));
    }
}
