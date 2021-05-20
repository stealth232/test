package ru.clevertec.check.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.check.dao.OrderRepository;
import ru.clevertec.check.dao.UserRepository;
import ru.clevertec.check.dao.WarehouseRepository;
import ru.clevertec.check.model.user.Role;
import ru.clevertec.check.model.user.User;
import ru.clevertec.check.service.UserService;
import ru.clevertec.check.service.WarehouseService;

import java.util.*;

import static ru.clevertec.check.service.CheckConstants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;
    private final Role roleUser;
    private final Role roleAdmin;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUserByLogin(String login) {
        Optional<User> userByLogin = findUserByLogin(login);
        if(userByLogin.isPresent()){
        return userRepository.getUserByLogin(login);}
        return null;
    }

    @Override
    public User save(User user) {
        User userFromDB = userRepository.getUserByLogin(user.getLogin());
        if (Objects.isNull(userFromDB)) {
            user.setRoles(Collections.singleton(roleUser));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Integer deleteUserById(Integer id) {
        orderRepository.removeDataOrdersByUserId(id);
        return userRepository.deleteUserById(id);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Override
    public Integer changeRole(Integer id) {
        User user = userRepository.getUserById(id);
        Set<Role> role = new HashSet<>();
        if(Objects.nonNull(user)){
            if (user.getRoles().stream().findFirst().get().getName().equals(ROLE_USER)) {
                role.add(roleAdmin);
            }
            if (user.getRoles().stream().findFirst().get().getName().equals(ROLE_ADMIN)) {
                role.add(roleUser);
            }
            user.setRoles(role);
            userRepository.save(user);
            return user.getId();
        }
        return ZERO_INT;
    }

    @Override
    public User getUserById(Integer id){
       if(userRepository.existsById(id))
        return userRepository.getUserById(id);
       return null;
    }

    @Override
    public User findUserById(Integer id){
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<User> getUser(String login, String password) {
        User user = userRepository.getUserByLogin(login);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println(password);
            System.out.println(user.getPassword());
            throw new BadCredentialsException("Bad credentials");
        }
        return userRepository.findUserByLogin(login);
    }

    @SneakyThrows
    @Override
    public Map<String, String> getUserByRoles() {
        List<Object[]> objects = userRepository.getUserByRoles();
        Map<String, String> roles = new HashMap<>();
        for (Object[] obj : objects ) {
            String userName = (String) obj[1];
            String role = (String) obj[2];
            roles.put(userName, role);
        }
       return  roles;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                grantedAuthorities);
    }
}
