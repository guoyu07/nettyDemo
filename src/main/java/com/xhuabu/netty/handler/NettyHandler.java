package com.xhuabu.netty.handler;

import org.springframework.stereotype.Service;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */
public interface NettyHandler {
    public void channelRegistered(io.netty.channel.Channel channel);
    public void handleMsg(io.netty.channel.Channel channel, String json);
    public void channelRemoved(io.netty.channel.Channel channel);
}
