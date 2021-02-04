package com.mqtt.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
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

public class mqttdifine {
	//�㲥Ⱥ����Ϣ
	  static void broadmessage(String topic,String msg,ChannelHandlerContext ctx)
	    {
	            msg sendMessage = new msg();
	            sendMessage.setTopicName(topic);
	            sendMessage.setStatus(MqttMessageType.PUBLISH);
	            sendMessage.setContent(msg.getBytes());
	            sendmsg(sendMessage, ctx);
	        
	    }
	/* �ظ���Ϣ
	  String topicName = message.variableHeader().topicName();
      msg sendMessage = new msg();
      sendMessage.setTopicName(topicName);
      sendMessage.setStatus(MqttMessageType.PUBLISH);
      sendMessage.setContent("12345".getBytes());
      sendmsg(sendMessage, ctx);
   */
	  
	  //����
	   static void connect(ChannelHandlerContext ctx, Object req)
	    {
	    	MqttConnAckVariableHeader variableheader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
	    	MqttFixedHeader CONNACK_HEADER = new MqttFixedHeader(MqttMessageType.CONNACK, false,MqttQoS.AT_MOST_ONCE,false,0); 
	    	MqttConnAckMessage connAckMessage = new MqttConnAckMessage(CONNACK_HEADER, variableheader);
	        ctx.write(connAckMessage);

	    }
	   //��������
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
	   
	   //��������
	   static void publish(ChannelHandlerContext ctx, Object req)
	    {
	    MqttPublishMessage message = (MqttPublishMessage)req;
	        ByteBuf buf = message.payload();
	        String msg = new String(ByteBufUtil.getBytes(buf));
	        System.out.println(msg);
	        //������Ϣ
	        if("x".equals(msg)){
	        	//Ⱥ����Ϣ
	        	 broadmessage("info","dddda",ctx);
	        }
	        else
	        {  
	        	//�ظ���Ϣ
	          	String topicName = message.variableHeader().topicName();
	            msg sendMessage = new msg();
	            sendMessage.setTopicName(topicName);
	            sendMessage.setStatus(MqttMessageType.PUBLISH);
	            sendMessage.setContent("12345".getBytes());
	            sendmsg(sendMessage, ctx);
	        }
	    }
	    
	 //����
	   static void sendmsg(msg message,ChannelHandlerContext  channel) {
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
