package com.xhuabu.netty.utils;

import com.alibaba.fastjson.JSONObject;
import com.xhuabu.netty.handler.NettyHandler;
import com.xhuabu.netty.model.Config;
import com.xhuabu.netty.server.NettyServer;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyChannelMap {

	/**
	 * 1线服务标签 （true 由FuturesQuotaClientHander 分发行情数据 Map<flag,true>）
	 */
	public static Map<String, AtomicBoolean> flagBool =  new HashMap<String,AtomicBoolean>();
	/**
	 * 2线服务标签 （true 由FuturesQuotaDoubleClientHander 分发行情数据  Map<flag,true>）
	 */
	public static Map<String, AtomicBoolean> flagBoolDouble =  new HashMap<String,AtomicBoolean>();
	/**
	 * 确定当前使用的通道（1线or2线 ，Map< "端口",true >）
	 */
	public static boolean BoolFlag ;

	public static List<Channel> badChannelsList = new ArrayList<Channel>();
	public static Map<Channel,String> badChannleMap = new ConcurrentHashMap<>();
	public static Map<String, AtomicBoolean> flagBoolPort =  new HashMap<String,AtomicBoolean>();
	public static Map<Channel,AtomicBoolean> chanleMap = new ConcurrentHashMap<>();
	public static List<Map<Channel,AtomicBoolean>> list = new ArrayList<Map<Channel,AtomicBoolean>>();

	public static Channel listEffect = null;

	
	public static List<String> ls =new ArrayList<String>();

	public static void main(String args[]){

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
			}, 16, 33334);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
