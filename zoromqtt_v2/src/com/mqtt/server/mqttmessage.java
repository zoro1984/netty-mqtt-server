package com.mqtt.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;

public class mqttmessage {
	//广播群发消息
	  static void broadmessage(String topic,String msg,ChannelHandlerContext ctx)
	    {
	            msgdefine sendMessage = new msgdefine();
	            sendMessage.setTopicName(topic);
	       
	            sendMessage.setContent(msg.getBytes());
	            MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,
	            		sendMessage.isRetain(), MqttQoS.valueOf(sendMessage.getQoS()), sendMessage.isRetain(),
	            		sendMessage.getRemainingLength());
		      MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic,-1);
		        ByteBuf byteBuf = Unpooled.directBuffer();
		        byteBuf.writeBytes(sendMessage.getContent());
		        MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
	            mqttgroup.channelGroup.writeAndFlush(publishMessage);
	    }
	  
	  
	  
	/* 回复消息
	  String topicName = message.variableHeader().topicName();
      msg sendMessage = new msg();
      sendMessage.setTopicName(topicName);
      sendMessage.setStatus(MqttMessageType.PUBLISH);
      sendMessage.setContent("12345".getBytes());
      sendmsg(sendMessage, ctx);
   */
	  
	  //连接
	   static void connect(ChannelHandlerContext ctx, Object req)
	    {
	    	MqttConnAckVariableHeader variableheader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
	    	MqttFixedHeader CONNACK_HEADER = new MqttFixedHeader(MqttMessageType.CONNACK, false,MqttQoS.AT_MOST_ONCE,false,0); 
	    	MqttConnAckMessage connAckMessage = new MqttConnAckMessage(CONNACK_HEADER, variableheader);
	        ctx.write(connAckMessage);

	    }
	   //订阅主题
	   static void subscribe(ChannelHandlerContext ctx, Object req)
	    {
	        MqttSubscribeMessage message = (MqttSubscribeMessage)req;
	        int msgId = message.variableHeader().messageId();
	        MqttMessageIdVariableHeader header = MqttMessageIdVariableHeader.from(msgId);
	        MqttSubAckPayload payload = new MqttSubAckPayload(0);
	   	 	MqttFixedHeader SUBACK_HEADER = new MqttFixedHeader(MqttMessageType.SUBACK, false,MqttQoS.AT_MOST_ONCE,false,0);
	        MqttSubAckMessage suback = new MqttSubAckMessage(SUBACK_HEADER, header, payload);
	        ctx.write(suback);
	    }
	   
	   //发布主题
	   static void publish(ChannelHandlerContext ctx, Object req)
	    {
	    MqttPublishMessage message = (MqttPublishMessage)req;
	        ByteBuf buf = message.payload();
	        String msg = new String(ByteBufUtil.getBytes(buf));
	        System.out.println(msg);
	        //发送信息
	        if(msg.length()>6 && "@Notic".equals(msg.substring(0, 6))){
	        	//输入@Notic+消息内容 群发消息
	        	 msg=msg.substring(6);
	        	 broadmessage("info",msg,ctx);
	        }
	        else if(msg.length()>6 && "@Auser".equals(msg.substring(0, 6))){
	        	//输入 @Auser+用户名 绑定channel和用户
	        	String username=msg.substring(6);
	 
	        	 usergroup.online(ctx, username);
	        }
	        else if(msg.length()>6 && "@Muser".equals(msg.substring(0, 6))){
	        	//对指定用户推送消息  @Muser"username"#"message"
	        	msg=msg.substring(6);
	        	String umessage[]=msg.split("#");
	        	String username=umessage[0];
	        	System.out.print(username);
	        	String usermsg=umessage[1];
	        	
	        	if( usergroup.isonline(username)){
	        		
	        		ChannelHandlerContext userctx= usergroup.getChannelHandlerContextByUserId(username);
	       
		          	String topicName = message.variableHeader().topicName();
		            msgdefine sendMessage = new msgdefine();
		            sendMessage.setTopicName(topicName);
		            sendMessage.setStatus(MqttMessageType.PUBLISH);
		            sendMessage.setContent(usermsg.getBytes());
		            sendmsg(sendMessage, userctx);
		            
		            String msgrep="SendOk.";
		          	String topicName1 = message.variableHeader().topicName();
		            msgdefine sendMessage1 = new msgdefine();
		            sendMessage1.setTopicName(topicName1);
		            sendMessage1.setStatus(MqttMessageType.PUBLISH);
		            sendMessage1.setContent(msgrep.getBytes());
		            sendmsg(sendMessage1, ctx);
	        	}
	        	else
	        	{
	        		String msgrep="User dose not online.";
		          	String topicName = message.variableHeader().topicName();
		            msgdefine sendMessage = new msgdefine();
		            sendMessage.setTopicName(topicName);
		            sendMessage.setStatus(MqttMessageType.PUBLISH);
		            sendMessage.setContent(msgrep.getBytes());
		            sendmsg(sendMessage, ctx);
	        	}
	        	
	        }
	        else
	        {  
	        	//其他自定义 回复消息 
	        	String msgrep="I have a message.";
	          	String topicName = message.variableHeader().topicName();
	            msgdefine sendMessage = new msgdefine();
	            sendMessage.setTopicName(topicName);
	            sendMessage.setStatus(MqttMessageType.PUBLISH);
	            sendMessage.setContent(msgrep.getBytes());
	            sendmsg(sendMessage, ctx);
	        }
	        
	        
	        
	        
	        
	    }
	    
	 
	   static void sendmsg(msgdefine message,ChannelHandlerContext  channel) {
	        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH,
	                message.isDup(), MqttQoS.valueOf(message.getQoS()), message.isRetain(),
	                message.getRemainingLength());
	        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(message.getTopicName(), message.getPackageId());
	        ByteBuf byteBuf = Unpooled.directBuffer();
	        byteBuf.writeBytes(message.getContent());
	        MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
	        channel.writeAndFlush(publishMessage);
	    }
	 

	    static void ping(ChannelHandlerContext ctx, Object req)
	    {
	 
	        MqttFixedHeader header = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
	        MqttMessage pingespMessage = new MqttMessage(header);
	        ctx.write(pingespMessage);
	    }
	    
	    
}
