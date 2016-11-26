package com.xhuabu.netty.handler;

import com.xhuabu.netty.utils.LuckinDecoder;
import com.xhuabu.netty.utils.LuckinEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/18
 * @since v1.0.0
 */
public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    private List<MyHandler2> mHandlerList;
    private String mFutureType;

    public NettyInitializer(List<MyHandler2> handlerList, String futureType) {
        mHandlerList = handlerList;
        mFutureType = futureType;
    }


    protected void onDisconnectCallback(ChannelHandlerContext ctx) {
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LuckinDecoder());
        pipeline.addLast(new LuckinEncoder());

        pipeline.addLast(new NettyClientHandler(mHandlerList, mFutureType) {
            @Override
            protected void onChannelInActive(ChannelHandlerContext ctx) {
                super.onChannelInActive(ctx);
                onDisconnectCallback(ctx);
            }
        });
    }


}
