import com.xhuabu.netty.handler.NettyHandler;
import com.xhuabu.netty.server.NettyServer;
import io.netty.channel.Channel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述:
 *
 * @author 陈润发
 * @created 16/9/3
 * @since v1.0.0
 */
public class NettyTest2 {
    public static void main(String args[]){

        try {
            NettyServer.start(new NettyHandler() {
                AtomicInteger ai=new AtomicInteger();
                @Override
                public void handleMsg(final Channel channel, String json) {
                    //System.out.println(ai.incrementAndGet()+" "+System.currentTimeMillis()+" "+json);
                    System.out.println("服务端2向客户端 输入数据");

                    Timer time = new Timer();
                    time.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            channel.writeAndFlush("2接受数据"+ai.incrementAndGet());
                        }
                    },1000l,5000l);

                }

                @Override
                public void channelRemoved(Channel channel) {
                    System.out.println("2remove");
                }

                @Override
                public void channelRegistered(Channel channel) {
                    channel.writeAndFlush("2初次握手");
                    System.out.println("2有新的客户端连入");
                }
            }, 1, 33332);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
