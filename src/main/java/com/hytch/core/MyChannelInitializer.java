package com.hytch.core;

import com.hytch.handler.EchoClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by linfp on 2017/7/12.
 * 通道初始化
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger LOG = LoggerFactory.getLogger(MyChannelInitializer.class);
	
	private static final int MAX_IDLE_SECONDS = 5;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		// 添加到pipeline中的handler会被串行处理(PS: 类似工业生产中的流水线)
		ChannelPipeline pipeline = ch.pipeline();
		//它的作用就是用来检测客户端的读取超时的
		pipeline
				.addLast("idleStateCheck",
						new IdleStateHandler(MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, TimeUnit.SECONDS))
				.addLast(new StringEncoder(Charset.forName("UTF-8")))
				.addLast(new StringDecoder(Charset.forName("UTF-8")))
				.addLast(new EchoClientHandler());
		// 使用addLast来添加自己定义的handler到pipeline中
		// pipeline.addLast("multiplexer", createMyProtcolDecoder());
	}
}
