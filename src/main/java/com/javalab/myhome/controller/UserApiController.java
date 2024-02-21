package com.javalab.myhome.controller;

import com.javalab.myhome.model.Board;
import com.javalab.myhome.model.QUser;
import com.javalab.myhome.model.User;
import com.javalab.myhome.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
class UserApiController {
    @Autowired
    private UserRepository repository;

    @GetMapping("/users")
    Iterable<User> all(@RequestParam(required = false) String method,
                   @RequestParam(required = false) String text){
        Iterable <User> users = null;
        if("query".equals(method)) {
            users = repository.findByUsernameQuery(text);
        }else if("query".equals(method)) {
            users = repository.findByUsernameNativeQuery(text);
        }else if("querydsl".equals(method)) {
            QUser user = QUser.user;
            Predicate predicate = user.username.contains(text);

            users = repository.findAll(predicate);
        } else {
            users = repository.findAll();
        }
        return users;
    }

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }
    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        return repository.findById(id)
                .map(user -> {
                    user.getBoards().clear(); // 기존의 데이터 삭제
                    user.getBoards().addAll(newUser.getBoards());  // 지금 받은 데이터로 바꿈
                    for(Board board : user.getBoards()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}