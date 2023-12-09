package com.junit.service;

import com.junit.dto.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    public void test(){
        var userService = new UserService();
        var users = userService.getAll();

//        Согласно источнику, сообщение печатается только в том случае, если утверждение не выполнено.
//        assertTrue(users.isEmpty(),() -> "Users is empty");
        assertFalse(users.isEmpty(),() -> "Users should be empty");
    }
}
