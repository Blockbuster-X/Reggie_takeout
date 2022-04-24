package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.R;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户手机登录
 * @author Blockbuster
 * @date 2022/4/21 11:39:37 星期四
 */

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendmsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();

        if (!phone.isEmpty()) {
            // 生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("验证码为{}", code);

            // 调用阿里云提供的短信API完成发送短信
            // SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            // 将生成的验证码保存到session
            // session.setAttribute(phone, code);

            // 将生成的验证码保存到 redis 有效期五分钟
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        // 获取手机号 验证码
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        // 从 Session 中获取保存的验证码
        // Object attribute = session.getAttribute(phone);

        // 从 redis 中获取保存的验证码
        Object attribute = stringRedisTemplate.opsForValue().get(phone);

        // 进行验证码比对
        if (attribute != null && attribute.equals(code)) {
            // 成功登录

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

            wrapper.eq(User::getPhone, phone);

            User user = userService.getOne(wrapper);

            // 判断是否新用户
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            // 存入 session
            session.setAttribute("user", user.getId());

            // 登录完成删除验证码
            stringRedisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败");
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");

        return R.success("退出成功");
    }
}
