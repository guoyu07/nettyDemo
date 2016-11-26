package com.xhuabu.netty.server;
import com.xhuabu.netty.handler.InitHandler;
import com.xhuabu.netty.handler.MyHandler;
import com.xhuabu.netty.handler.MyHandler2;
import com.xhuabu.netty.handler.NettyHandler;
import com.xhuabu.netty.model.Config;
import com.xhuabu.netty.utils.CustomerPropertiesConfig;
import com.xhuabu.netty.utils.NettyChannelMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */
public class NettyClient  implements InitializingBean {
    @Autowired
    private CustomerPropertiesConfig propCfg;


    private static Logger logger = Logger.getLogger(NettyServer.class);

    //线程个数
    public static int SERVER_COUNT = 1;
    public static int SERVER_PORT = 0;
    public static String URL;
    private void init(){

        new Thread() {
            public void run() {
                try {
                    NettyClient.connect(new MyHandler2(), 1, "127.0.0.1", 33333);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    NettyClient.connect(new MyHandler2(), 1, "127.0.0.1", 33334);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        new Thread() {
            public void run() {
                try {
                    NettyClient.connect(new MyHandler2(), 1, "127.0.0.1", 33335);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        logger.info("start end============");
    }

    public static void connect(NettyHandler handler, int workers, String host, int port) throws Exception {
        ExecutorService pool= Executors.newFixedThreadPool(Math.max(workers, 1));
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class).handler(new InitHandler(handler, pool));

            b.connect(host, port).sync().channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            pool.shutdown();
        }
    }
    public static void main(String[] args) throws Exception {
        Map<Config,AtomicBoolean> configMap1 = new ConcurrentHashMap<>();
        configMap1.put(new Config("127.0.0.1",33333),new AtomicBoolean(true));

        Map<Config,AtomicBoolean> configMap2 = new ConcurrentHashMap<>();
        configMap2.put(new Config("127.0.0.1",33334),new AtomicBoolean(false));
//        NettyChannelMap.list.add(configMap1);
//        NettyChannelMap.list.add(configMap2);

        new Thread() {
                                        public void run() {
                                            try {
                                                NettyClient.connect(new MyHandler2(), 1, "127.0.0.1", 33333);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();

        new Thread() {
            public void run() {
                try {
                    NettyClient.connect(new MyHandler2(), 1, "127.0.0.1", 33334);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();



//                        for (Map<Config,AtomicBoolean> map:NettyChannelMap.list
//                             ) {
//                            for (final Map.Entry<Config,AtomicBoolean> entry:map.entrySet()
//                                 ) {
//
//                                    System.out.println("connect ===="+entry.getKey().getPort());
//
//                                    new Thread() {
//                                        public void run() {
//                                            try {
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }.start();
//                                }
//
//
//                        }

    }



    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("start client");
        init();
    }
}
