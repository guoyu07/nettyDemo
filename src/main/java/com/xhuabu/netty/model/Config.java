package com.xhuabu.netty.model;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/11/25
 * @since v1.0.0
 */
public class Config {
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Config(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    String host;
    Integer port;
}
