package com.xhuabu.netty.handler;

import com.xhuabu.netty.utils.LuckinDecoder;
import com.xhuabu.netty.utils.LuckinEncoder;
import com.xhuabu.netty.utils.NettyChannelMap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */
@Component
public class InitHandler extends ChannelInitializer<SocketChannel> {
    private final static int readerIdleTimeSeconds = 40;//读操作空闲30秒
    private final static int writerIdleTimeSeconds = 50;//写操作空闲60秒
    private final static int allIdleTimeSeconds = 100;//读写全部空闲100秒
    NettyHandler handler;
    ExecutorService pool;

    public InitHandler(NettyHandler handler,ExecutorService worker){
        this.handler=handler;
        pool=worker;
    }
    private SimpleChannelInboundHandler<String> coreHandler=new SimpleChannelInboundHandler<String>(){
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
          //  channelsList.add(ctx.channel());

            handler.channelRegistered(ctx.channel());

        };
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {

            pool.execute(new Runnable() {

                @Override
                public void run() {

                    handler.handleMsg(ctx.channel(), msg);
                }
            });
        }
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            handler.channelRemoved(ctx.channel());
        };
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            cause.printStackTrace();
            handler.channelRemoved(ctx.channel());
        };
        public boolean isSharable() {
            return true;
        }
    };
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LuckinDecoder());
        pipeline.addLast(new LuckinEncoder());
        pipeline.addLast("idleStateHandler",new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds,allIdleTimeSeconds));
        pipeline.addLast(coreHandler);
    }

}
