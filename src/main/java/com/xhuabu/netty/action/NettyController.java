package com.xhuabu.netty.action;

import com.xhuabu.netty.utils.IpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/7169.254.2.63
 * @since v1.0.0
 */
@Controller
@RequestMapping("netty")
public class NettyController {

    @ResponseBody
    @RequestMapping("index")
    public String index(HttpServletRequest request, HttpServletResponse response){
        String ip1 = IpUtils.getUserIP(request);
        String ip2 = IpUtils.getAddrIP(request);
        System.out.println("your ip1:---"+ip1+"---ip2:"+ip2);
        return ip1+"==="+ip2;
    }
}
