package com.xhuabu.netty.vo;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/18
 * @since v1.0.0
 */
/**
 * /futuresquota/tradeStrategy
 *
 * { "code": 200, "msg": "查询成功", "msgType": 1, "errparam": "",
 * "data": {
 *      "analystName": "金牌分析师",
 *      "content": "999999",
 *      "futuresType": 2,
 *      "expiredDate": "2015-10-21 10:10:10",
 *      "createDate": "2015-10-19 18:44:06",
 *      }
 * }
 *
 *  analystName :分析师名称
 content :策略内容
 futuresType :品种标识
 expiredDate :有效期至
 createDate :发布时间
 *
 { "code": 404, "msg": "没有数据", "msgType": 0, "errparam": "", "data": null }
 */
public class Strategy {
    private String analystName;
    private String content;
    private String futuresCode;
    private String expiredDate;
    private String createDate;

    public String getAnalystName() {
        return analystName;
    }

    public String getContent() {
        return content;
    }

    public String getFuturesCode() {
        return futuresCode;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public String getCreateDate() {
        return createDate;
    }
}
