package com.mqtt.server;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class usergroup {
	public static  ConcurrentHashMap<String, ChannelHandlerContext> ChannelHandlerContextMap = new ConcurrentHashMap<>();
    public boolean hasUser(ChannelHandlerContext ChannelHandlerContext) {
        AttributeKey<String> key = AttributeKey.valueOf("user");
        return (ChannelHandlerContext.hasAttr(key) || ChannelHandlerContext.attr(key).get() != null);
    }

    public static void online(ChannelHandlerContext ctx, String userId) {

          ChannelHandlerContextMap.put(userId, ctx);
            AttributeKey<String> key = AttributeKey.valueOf("user");
            ctx.attr(key).set(userId);
    }


    public static ChannelHandlerContext getChannelHandlerContextByUserId(String userId) {
        return ChannelHandlerContextMap.get(userId);
    }

  
    public static Boolean isonline(String userId) {
        return ChannelHandlerContextMap.containsKey(userId) && ChannelHandlerContextMap.get(userId) != null;
    }
}
