package com.wwh.usercenter.service;

import com.wwh.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("jh13");
        user.setUserAccount("harden");
        user.setAvatarUrl("https://123");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123@qq.com");
        boolean save = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(save);
    }

    @Test
    void userRegister() {
        String userAccount = "jh13";
        String password = "12345678";
        String checkPassword = "12345678";

        long l = userService.userRegister(userAccount, password, checkPassword, "123");
        System.out.println(l);

    }
}