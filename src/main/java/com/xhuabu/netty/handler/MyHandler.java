package com.xhuabu.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xhuabu.netty.server.NettyClient;
import com.xhuabu.netty.server.NettyServer;
import com.xhuabu.netty.utils.CustomerPropertiesConfig;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */

public class MyHandler implements NettyHandler {
    private static Logger logger = Logger.getLogger(MyHandler.class);
    @Autowired
    private CustomerPropertiesConfig propCfg;
    static Channel ch;
    private static ExecutorService pool = Executors.newSingleThreadExecutor();
    static long lastTime = System.currentTimeMillis();
    private Heartbeat task;
    private Timer timer = new Timer();
    private long time = 5000;
    //线程个数
    public static int SERVER_COUNT = 1;
    public static int SERVER_PORT = 0;
    public static String URL;
    @Override
    public void channelRegistered(Channel channel) {
        ch = channel;
        task = new Heartbeat();
        if(timer == null) {
            timer = new Timer();
        }
        timer.schedule(task,time,time);
        long now = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("timeStamp", now);
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("DATA", data);
        ch.writeAndFlush(new Gson().toJson(req));
        System.out.println("连上了");

    }

    @Override
    public void handleMsg(Channel channel, String json) {
        JSONObject jsons = JSON.parseObject(json);
        Integer CMDID = jsons.getInteger("CMDID");
        if (CMDID != null && CMDID == 1001) {
            logger.info("心跳 == " + json);
            try {
                String DATA = jsons.getString("DATA");
                jsons = (JSONObject) JSON.parse(DATA);
                lastTime = jsons.getLong("timeStamp");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (CMDID != null && CMDID == 1000) {
            logger.info("接收到的内容 == " + json);
            //System.out.println("接收到的内容:"+json);
        }

    }

    @Override
    public void channelRemoved(Channel channel) {
        SERVER_PORT = Integer.valueOf(propCfg.getProperty("a50_port_1").trim());
        URL = propCfg.getProperty("a50_url_1").trim();
        ch = null;
        channel.close();
        pool.execute(new Runnable() {
            boolean flag = false;
            @Override
            public void run() {
                //断线重连机制
                while(!flag){

                    try{
                        flag = true;
                        System.out.println("重连```");
                        NettyClient.connect(MyHandler.this, SERVER_COUNT, URL, SERVER_PORT);
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
    private class Heartbeat extends TimerTask {

        @Override
        public void run() {
            if (ch != null) {
                long now = System.currentTimeMillis();
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("timeStamp", now);
                Map<String, Object> req = new HashMap<String, Object>();
                req.put("CMDID", 1001);
                req.put("DATA", data);
                ch.writeAndFlush(new Gson().toJson(req));
                if ((now - lastTime) > 10000) {
                    //logger.info("期货行情行心跳调超时");
                    System.out.println("心跳超时了```");
                    //断开连接
                    ch.disconnect();

                    //关闭连接
                    ch.close();


                    this.cancel();
                }
            } else {
                this.cancel();
            }
        }
        }

}
