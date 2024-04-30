package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信登录接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";


    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User userLogin(UserLoginDTO userLoginDTO) throws IOException {
        //使用UserLoginDTO传过来的code,获得当前微信用户的openid
//        //  1、拼接请求地址
//        String getUrl = WX_LOGIN + "?" + weChatProperties.getAppid()
//                + "&" + weChatProperties.getSecret()
//                + "&" + userLoginDTO.getCode()
//                + "&authorization_code";
        //  2、使用HttpClient发起请求
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(getUrl);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        Map<String,String> map = new HashMap<>();
//        map.put("appid",weChatProperties.getAppid());
//        map.put("secret",weChatProperties.getSecret());
//        map.put("js_code",userLoginDTO.getCode());
//        map.put("grant_type","authorization_code");
//        String json = HttpClientUtil.doGet(WX_LOGIN, map);
//        // 3、解析响应JSON字符串里面的openid
//        String openid = JSON.parseObject(json).getString("openid");
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否是新用户
        User user = userMapper.queryByOpenid(openid);
        //如果是新用户，则完成注册
        if(user == null){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insertUser(user);
        }

        //返回这个User对象
        return user;
    }

    //将请求微信获取用户openid的代码抽离出来形成一个方法
    private String getOpenid(String code){
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        // 3、解析响应JSON字符串里面的openid
        String openid = JSON.parseObject(json).getString("openid");
        return openid;
    }

}
