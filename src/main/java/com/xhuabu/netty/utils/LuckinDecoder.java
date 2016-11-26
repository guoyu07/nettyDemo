/*
 * Copyright (c) 2015 www.caniu.com - 版权所有
 * 
 * This software is the confidential and proprietary information of
 * luckin Group. You shall not disclose such confidential information 
 * and shall use it only in accordance with the terms of the license 
 * agreement you entered into with www.cainiu.com
 */
package com.xhuabu.netty.utils;

import com.xhuabu.netty.handler.NettyHandler;
import com.xhuabu.netty.server.NettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 描述:
 *
 * @author  boyce
 * @created 2015年7月23日 下午4:42:19
 * @since   v1.0.0
 */
public class LuckinDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		while (true) {
			if (in.readableBytes() <= 4) {
				break;
			}
			in.markReaderIndex();
			int length = in.readInt();
			if(length<=0){
				throw new Exception("a negative length occurd while decode!");
			}
			if (in.readableBytes() < length) {
				in.resetReaderIndex();
				break;
			}
			byte[] msg = new byte[length];
			in.readBytes(msg);
			out.add(new String(msg, "GBK"));
		}

	}
	public static void main(String[] args) {
		try {
			NettyServer.start(new NettyHandler() {
				AtomicInteger ai=new AtomicInteger();
				@Override
				public void handleMsg(final Channel channel, String json) {
					//System.out.println(ai.incrementAndGet()+" "+System.currentTimeMillis()+" "+json);
					System.out.println("3服务端向客户端 输入数据");

					Timer time = new Timer();
					time.schedule(new TimerTask() {
						@Override
						public void run() {

							channel.writeAndFlush("3接受数据"+ai.incrementAndGet());
						}
					},1000l,5000l);

				}

				@Override
				public void channelRemoved(Channel channel) {
					System.out.println("remove");
				}

				@Override
				public void channelRegistered(Channel channel) {
					channel.writeAndFlush("3初次握手");
					System.out.println("3有新的客户端连入");
				}
			}, 16, 33333);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
