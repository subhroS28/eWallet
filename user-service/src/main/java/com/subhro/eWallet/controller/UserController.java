package com.subhro.eWallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.subhro.eWallet.model.User;
import com.subhro.eWallet.request.UserCreateRequest;
import com.subhro.eWallet.service.UserSearchTypeEnum;
import com.subhro.eWallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user")
    private User getUserByField(@RequestParam("paramType") String paramType,
                               @RequestParam("paramValue") String paramValue) throws Exception {

        UserSearchTypeEnum userSearchTypeEnum = UserSearchTypeEnum.valueOf(paramType);

        switch (userSearchTypeEnum){
            case EMAIL:
                return userService.getUserByEmail(paramValue);
            case PHONE:
                return userService.getUserByPhoneNumber(Integer.parseInt(paramValue));
            case ID:
                return userService.getUserById(Integer.parseInt(paramValue));
            default:
                throw new Exception("Invalid paramType Entered.!!!");
        }
    }

    /*@GetMapping("/user2")
    public UserCreateRequest getUserByField2(@RequestParam("paramType") String paramType,
                               @RequestParam("paramValue") String paramValue) throws Exception {

        UserSearchTypeEnum userSearchTypeEnum = UserSearchTypeEnum.valueOf(paramType);
        UserCreateRequest userCreateRequest;
        User user;

        switch (userSearchTypeEnum) {
            case EMAIL:
                user = userService.getUserByEmail(paramValue);
                break;
            case PHONE:
                user = userService.getUserByPhoneNumber(Integer.parseInt(paramValue));
                break;
            case ID:
                user = userService.getUserById(Integer.parseInt(paramValue));
                break;
            default:
                throw new Exception("Invalid paramType Entered.!!!");
        }


        userCreateRequest = UserCreateRequest.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();

        return userCreateRequest;
    }*/

    @PostMapping("/user")
    public void createUser(@RequestBody UserCreateRequest user) throws JsonProcessingException {
        userService.createNewUser(user);
    }
}
