package com.example.Controller;

import com.example.Model.user;
import com.example.RequestDtos.userCreateRequest;
import com.example.Service.userService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class userController {

//    API's
//    user creation
//    getting user
//    /admin/user/userId
//    /admin/allusers
//    /user-put
//    /user-delete

    @Autowired
    userService userService;

    @PostMapping("/user")
    //@PreAuthorize("hasAuthority('user')")
    public ResponseEntity createUser(@RequestBody userCreateRequest userCreateRequest) throws JsonProcessingException {
        userService.create(userCreateRequest);
        return new ResponseEntity("user created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/user")
   public user getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        user user = (user)auth.getPrincipal();
        return (user)userService.loadUserByUsername(user.getPhoneNo());
    }

    @GetMapping("/admin/all/users")
    public List<user> getAll(){
        return userService.getAll();
    }

    @GetMapping("/admin/user/{userId}")
    public user getUserById(@PathVariable("userId") String userId){
        return (user) userService.loadUserByUsername(userId);
    }
}
