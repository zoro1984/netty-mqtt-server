package com.mqtt.server;
import java.util.concurrent.TimeUnit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public final class mqttserver {
    public static void main(String[] args) throws Exception {
        System.out.println(Runtime.getRuntime().availableProcessors());
        EventLoopGroup group = new NioEventLoopGroup(1);
        EventLoopGroup group1 = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group, group1)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
         	    protected void initChannel(SocketChannel ch) throws Exception {
         	   ch.pipeline().addLast(new MqttDecoder(1024*5));
         	   ch.pipeline().addLast(MqttEncoder.INSTANCE);
         	   ch.pipeline().addLast(new IdleStateHandler(20, 0, 20,TimeUnit.SECONDS));      
         	   ch.pipeline().addLast(new serverhandler());
               
         	    }
         	 })
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT);
            b.bind(1883).sync().channel();
            b.bind(1884).sync().channel().closeFuture().sync();
            
        } finally {
            group.shutdownGracefully();
            group1.shutdownGracefully();
        }
      
        
    }
    
    	
}
