package com.xhuabu.netty.handler;

import com.xhuabu.netty.server.NettyClient;
import com.xhuabu.netty.utils.NettyChannelMap;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xhuabu.netty.utils.NettyChannelMap.badChannleMap;
import static com.xhuabu.netty.utils.NettyChannelMap.chanleMap;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/18
 * @since v1.0.0
 */
@Component("myHandler2")
public class MyHandler2 implements NettyHandler {

    private static Logger logger = Logger.getLogger(MyHandler2.class);
    static AtomicBoolean flag ;
    private static ExecutorService pool = Executors.newSingleThreadExecutor();

    @Override
    public void channelRegistered(Channel channel) {

        synchronized (MyHandler2.class) {

//            t = new Timer2();
//            Timer timer2 = new Timer();
//            timer2.schedule(t, 0, 3000l);

            //确保chanleMap中只有一个通道为true
            flag = new AtomicBoolean(false);
            for (Map.Entry<Channel, AtomicBoolean> entry :
                    chanleMap.entrySet()) {
                logger.info("flag" + "----" + flag);
                if (entry.getValue().get()) {
                    flag.compareAndSet(false, true);
                    logger.info("flag" + "====" + flag);
                    break;
                }
            }
            if (flag.get()) {
                chanleMap.put(channel, new AtomicBoolean(false));
            } else {
                chanleMap.put(channel, new AtomicBoolean(true));
            }
        }
        logger.info(chanleMap);


        logger.info("register");
        channel.writeAndFlush("连接");
    }

    @Override
    public void handleMsg(Channel channel, String json) {

        NettyChannelMap.BoolFlag = true;
        logger.info("====now all ====="+NettyChannelMap.chanleMap);

        if(NettyChannelMap.chanleMap.get(channel).get()) {
            logger.info("message received:" + json+"channle:"+channel);
        }
    }

    @Override
    public void channelRemoved(Channel channel) {




        for (Map.Entry<Channel, AtomicBoolean> entry :
                chanleMap.entrySet()) {

                if(entry.getValue().get()){
                    entry.getValue().compareAndSet(true,false);
                }else {
                    entry.getValue().compareAndSet(false,true);
                }


        }
        chanleMap.remove(channel);
        //获取当前断开连接的host 跟 port
        final InetSocketAddress socket = (InetSocketAddress) channel.remoteAddress();
        final String host = socket.getHostString();
        final Integer port = socket.getPort();

        channel.close();
        pool.execute(new Runnable() {
            boolean flag = false;
            @Override
            public void run() {
                //断线重连机制
                while(!flag){

                    try{
                        flag = true;
                        System.out.println("重连```"+port);
                        NettyClient.connect(MyHandler2.this, 1, host, port);
                    }catch(Exception e){
                        flag = false;
                        System.out.println("重连``异常`");
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });
    }

}
