package com.xhuabu.netty.handler;

import com.xhuabu.netty.model.FuturesQuotaData;
import com.xhuabu.netty.vo.Strategy;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/22
 * @since v1.0.0
 */
public abstract class AndroidQuotaHandler
{
    protected void onReceiveSingleData(FuturesQuotaData data) {
        System.out.println(data.toString());
    }

    protected void onReceiveStrategy(Strategy strategy) {
    }

    protected void onError(String message) {
    }
}
