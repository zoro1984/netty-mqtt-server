package com.mqtt.server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetAddress;

public class serverhandler extends SimpleChannelInboundHandler<Object>
{

    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println(InetAddress.getLocalHost().getHostName());
        ctx.writeAndFlush("loginok");
        mqttgroup.channelGroup.add(ctx.channel());
        System.out.print(ctx.channel().id()+""+ctx.channel().localAddress());
        
        
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        // 从channelGroup中移除，当有客户端退出后，移除channel。
        mqttgroup.channelGroup.remove(ctx.channel());

        System.out.println(ctx.channel().localAddress().toString() + " channelInactive");
    }

    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	System.out.print("error");
        cause.printStackTrace();
        ctx.close();
    }

    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object req1) throws Exception
    {
        try
        {
            if (((MqttMessage)req1).decoderResult().isSuccess())
            {
                MqttMessage req = (MqttMessage)req1;
                switch (req.fixedHeader().messageType())
                {
                    case CONNECT:
                        mqttmessage.connect(ctx, req);
                        return;
                    case DISCONNECT:
                        ctx.close();
                        return;
                    case SUBSCRIBE:
                    	mqttmessage.subscribe(ctx, req);
                        return;
                    case PUBLISH:
                    	mqttmessage.publish(ctx, req);
                        return;
                    case PINGREQ:
                    	mqttmessage.ping(ctx, req);
                        return;
                    default:
                        return;
                }
            }
        }
        catch (Exception ex)
        {
            
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
    
        if (evt instanceof IdleStateEvent)
        {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE))
            {
            }else if(event.state().equals(IdleState.ALL_IDLE))
            {
            	heartbeat(ctx);
           
            }
        }
        super.userEventTriggered(ctx, evt);
    }

   private void heartbeat(ChannelHandlerContext ctx)
   {
	   	MqttFixedHeader mqttFixedHeader=new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
       	MqttMessage message=new MqttMessage(mqttFixedHeader);
       	ctx.writeAndFlush(message);
   }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

  
  
   

 
}
