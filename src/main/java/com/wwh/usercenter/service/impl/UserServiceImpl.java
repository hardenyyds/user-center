package com.wwh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwh.usercenter.common.ErrorCode;
import com.wwh.usercenter.exception.BusinessException;
import com.wwh.usercenter.model.domain.User;
import com.wwh.usercenter.service.UserService;
import com.wwh.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wwh.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author wwh29
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-06-04 16:01:30
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    private  static final String salt = "jh13";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4){
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }
        if (planetCode.length() > 5){
            return -1;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!#\\$%^&*()+=|{}'Aa:;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）9——+|{}【】\\\\\"‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return -1;
        }
        // 校验密码和验证密码是否相同
        if (!userPassword.equals(checkPassword)){
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0){
            return -1;
        }
        // 编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(userQueryWrapper);
         if (count > 0){
            return -1;
        }

        // 2 加密密码
        String hashPwd = DigestUtils.md5DigestAsHex((salt+ userPassword).getBytes());

        // 3 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashPwd);
        user.setPlanetCode(planetCode);
        boolean save = this.save(user);
        if (!save){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if (userPassword.length() < 8){
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!#\\$%^&*()+=|{}'Aa:;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）9——+|{}【】\\\\\"‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return null;
        }
        // 2 加密密码
        String hashPwd = DigestUtils.md5DigestAsHex((salt+ userPassword).getBytes());
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", hashPwd);
        User user = userMapper.selectOne(userQueryWrapper);
        // user不存在
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User returnUser = new User();
        returnUser.setId(originUser.getId());
        returnUser.setUsername(originUser.getUsername());
        returnUser.setUserAccount(originUser.getUserAccount());
        returnUser.setAvatarUrl(originUser.getAvatarUrl());
        returnUser.setGender(originUser.getGender());
        returnUser.setPlanetCode(originUser.getPlanetCode());
        returnUser.setPhone(originUser.getPhone());
        returnUser.setUserRole(originUser.getUserRole());
        returnUser.setEmail(originUser.getEmail());
        returnUser.setUserStatus(originUser.getUserStatus());
        returnUser.setCreateTime(originUser.getCreateTime());
        return returnUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




