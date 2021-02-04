# netty-mqtt-server

这是基于netty 4.1.1 的mqtt最简单服务器
第一次用netty写mqtt,网上的代码过于复杂，于是参考了许多代码，简化了netty mqtt服务器

1.mqtt解码主要是这句 ch.pipeline().addLast(new MqttDecoder(1024*5));
2.群推消息broadmessage(String topic,String msg,ChannelHandlerContext ctx)，需要把所有channel放到hashmap，发布时遍历取出就可以了，这个地方还没写。
3.协议在mqttdefine里，心跳没啥不同。
4.客户端是丛网上down的，生成了运行jar，便于调试，代码就不传了。
5.代码可以用于学习或直接拿去使用。
