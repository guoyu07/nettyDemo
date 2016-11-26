package com.xhuabu.netty.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xhuabu.netty.model.FuturesQuotaData;
import com.xhuabu.netty.vo.Response;
import com.xhuabu.netty.vo.Strategy;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/18
 * @since v1.0.0
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private static Logger logger = Logger.getLogger(NettyClientHandler.class);
    private List<MyHandler2> mHandlerList;
    private String mFutureType;

    public NettyClientHandler(List<MyHandler2> handlerList, String futureType) {
        mHandlerList = handlerList;
        mFutureType = futureType;
    }

    protected void onChannelActive(ChannelHandlerContext ctx) {
        System.out.println("onChannelActive");
    }

    protected void onChannelInActive(ChannelHandlerContext ctx) {
        System.out.println( "channelInactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

        if (mHandlerList != null) {
            if (!s.equalsIgnoreCase("null")) {
                if (s.indexOf("msgType") != -1) {
                    // RealTime strategy
                    System.out.println("收到策略消息");
                    handleRealTimeStrategy(s);
                } else { // RealTime quota data
                    System.out.println("收到行情消息");
                   // handleRealTimeQuotaData(s);
                    myhandlerMsg(s,channelHandlerContext.channel());
                }
            }

        }
    }

    private void myhandlerMsg(String s, Channel channel){
        for (NettyHandler handler:mHandlerList
             ) {
            handler.handleMsg(channel,s);
        }
    }

    private void handleRealTimeStrategy(String s) {

        Response<Strategy> response = new Gson().fromJson(s, new TypeToken<Response<Strategy>>() {
        }.getType());
        if (response.isSuccess()) {
            Strategy strategy = response.getData();
            if (mFutureType.equalsIgnoreCase(strategy.getFuturesCode())) {
                onReceiveStrategy(response.getData());
            }
        }
    }

    private void handleRealTimeQuotaData(String s) {
        try {


            FuturesQuotaData data = new Gson().fromJson(s, FuturesQuotaData.class);
            if (data.getInstrumentID().toUpperCase().contains(mFutureType.toUpperCase())) {
                onReceiveSingleData(data);
            }
        } catch (JsonSyntaxException e) {
            onError(e.getMessage());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");

        super.channelActive(ctx);
        onChannelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        super.channelInactive(ctx);
        onChannelInActive(ctx);
    }

    private void onReceiveSingleData(FuturesQuotaData data) {

        for (int i = 0; i < mHandlerList.size(); i++) {
            NettyHandler handler = mHandlerList.get(i);

        }
    }

    private void onReceiveStrategy(Strategy data) {
        for (int i = 0; i < mHandlerList.size(); i++) {

        }
    }

    private void onError(String msg) {
        for (int i = 0; i < mHandlerList.size(); i++) {

        }
    }

    @Override

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        onError(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}