package com.xhuabu.netty.server;

import com.xhuabu.netty.handler.InitHandler;
import com.xhuabu.netty.handler.MyHandler;
import com.xhuabu.netty.handler.NettyHandler;
import com.xhuabu.netty.utils.CustomerPropertiesConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.rtsp.RtspHeaders.Values.SERVER_PORT;


/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */

public class NettyServer  {

    @Autowired
    private CustomerPropertiesConfig propCfg;

    @Autowired
    private MyHandler MyHandler;
    private static Logger logger = Logger.getLogger(NettyServer.class);

    //线程个数
    public static int SERVER_COUNT = 1;
    public static int SERVER_PORT = 0;
    public static String URL;
    private void init(){

        SERVER_PORT = Integer.valueOf(propCfg.getProperty("server_port").trim());
        URL = propCfg.getProperty("ctp_url").trim();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    NettyClient.connect(MyHandler, SERVER_COUNT,URL,SERVER_PORT);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("行情服务端启动异常", e);
                }

            }
        }).start();
    }

    public static void start(NettyHandler handler, int workers, int port) throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(Math.max(workers, 1));
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new InitHandler(handler,pool));
            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {



        try {
            NettyServer.start(new NettyHandler() {
                AtomicInteger ai=new AtomicInteger();
                @Override
                public void handleMsg(final Channel channel, String json) {
                    //System.out.println(ai.incrementAndGet()+" "+System.currentTimeMillis()+" "+json);
                    System.out.println("服务端4向客户端 输入数据");

                    Timer time = new Timer();
                    time.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            channel.writeAndFlush("4接受数据"+ai.incrementAndGet());
                        }
                    },1000l,5000l);

                }

                @Override
                public void channelRemoved(Channel channel) {
                    System.out.println("4remove");
                }

                @Override
                public void channelRegistered(Channel channel) {
                    channel.writeAndFlush("4初次握手");
                    System.out.println("4有新的客户端连入");
                }
            }, 16, 33335);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
//    public class MyThread extends Thread{
//        public void run(){
//
//            try {
//
//                NettyClient.connect(MyHandler, SERVER_COUNT,URL,SERVER_PORT);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error("行情服务端启动异常", e);
//            }
//        }
//    }

}
