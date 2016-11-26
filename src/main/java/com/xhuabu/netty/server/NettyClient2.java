package com.xhuabu.netty.server;

import com.xhuabu.netty.handler.MyHandler2;
import com.xhuabu.netty.handler.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 行情连接客户端
 *
 * @author 陈润发
 * @created 16/11/18
 * @since v1.0.0
 */
public class NettyClient2 {

    private static Logger logger = Logger.getLogger(NettyClient2.class);
    private final static String DEFAULT_HOST = "127.0.0.1";
    //private final static String DEFAULT_HOST = "192.168.1.19"; // Debug
    private final static int DEFAULT_PORT = 33333;

    private EventLoopGroup mWorkerGroup;
    private Bootstrap mBootstrap;

    private String mHost;
    private Integer mPort;
    private String mFutureType;
    private boolean mClosed;

    private List<MyHandler2> mHandlerList;

    private static NettyClient2 mInstance;

    public static NettyClient2 getInstance() {
        if (mInstance == null) {
            mInstance = new NettyClient2();
        }
        return mInstance;
    }

    private NettyClient2() {
        this.mHandlerList = new ArrayList<MyHandler2>();
    }

    public void addNettyHandler(MyHandler2 handler) {
        if (mHandlerList != null) {
            mHandlerList.add(handler);
        }
    }

    public void removeNettyHandler(MyHandler2 handler) {
        if (mHandlerList != null) {
            mHandlerList.remove(handler);
        }
    }

    public void setHostAndPort(String host, String port) {
        try {
            this.mHost = host;
            this.mPort = Integer.valueOf(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mPort = null;
        }
    }

    public void setFutureType(final String futureType) {
        mFutureType = futureType;
    }

    public void start() {
        mClosed = false;
        System.out.println("start!");
        mWorkerGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap()
                .group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(mHandlerList, mFutureType) {
                    @Override
                    protected void onDisconnectCallback(ChannelHandlerContext ctx) {
                        super.onDisconnectCallback(ctx);
                        ctx.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("try reconnect");
                                connect();
                            }
                        }, 1, TimeUnit.MILLISECONDS);
                    }
                });

        connect();
    }

    private void connect() {
        System.out.println("connect");
        if (mClosed && mBootstrap != null) return;

        if (mHost == null || mPort == null) {
            mHost = DEFAULT_HOST;
            mPort = DEFAULT_PORT;
        }

        ChannelFuture channelFuture = mBootstrap.connect(mHost, mPort);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("连接成功!");
                    Channel channel = channelFuture.sync().channel();
                    if (mFutureType!=null&&!mFutureType.equals("")) {

                        channel.writeAndFlush(NettyLoginFactory.createLoginJson(mFutureType).toString());
                    }
                } else {
                    handleException(channelFuture.cause());
                }
            }
        });
    }

    private void handleException(Throwable cause) {
        cause.printStackTrace();
        for (int i = 0; i < mHandlerList.size(); i++) {
            //mHandlerList.get(i).onError("行情服务器连接失败");
            logger.info("行情服务连接失败");
        }
    }

    public void stop() {
        mClosed = true;
        if (mWorkerGroup != null) {
            mWorkerGroup.shutdownGracefully();
        }
    }
    public static void main(String args[]){
        NettyClient2.getInstance().setFutureType("cl");
        NettyClient2.getInstance().addNettyHandler(new MyHandler2());
        NettyClient2.getInstance().start();
    }

}
