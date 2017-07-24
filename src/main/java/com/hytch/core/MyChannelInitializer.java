package com.hytch.core;

import com.hytch.handler.EchoServerHandler;
import com.hytch.handler.HeartbeatHandler;
import com.hytch.handler.json.FastjsonHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by linfp on 2017/7/12.
 * 通道初始化
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger LOG = LoggerFactory.getLogger(MyChannelInitializer.class);
	
	private static final int MAX_IDLE_SECONDS = 5;
	private final ConfigurableApplicationContext applicationContext;
	
	@Autowired
	public MyChannelInitializer(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		// 添加到pipeline中的handler会被串行处理(PS: 类似工业生产中的流水线)
		ChannelPipeline pipeline = ch.pipeline();
		//它的作用就是用来检测客户端的读取超时的
		pipeline
				.addLast(new LoggingHandler())
				.addLast("idleStateCheck",
						new IdleStateHandler(MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, TimeUnit.SECONDS))
				//ChannelIn(是按顺序)
				.addLast(new HeartbeatHandler())
				//DelimiterBasedFrameDecoder可以自定义换行符
				//LineBasedFrameDecoder回车换行解码器，如果连续读取到最大长度后仍然没有发现换行符，就会抛出异常
				//同时忽略掉之前读到的异常码流
				//DelimiterBasedFrameDecoder可以自定义换行符使用自定义ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//				.addLast("base64_decoder", new Base64Decoder())  //1.base64解码
				.addLast("line_decoder", new LineBasedFrameDecoder(1024)) //2.按换行符解码
//				.addLast("json", new JsonObjectDecoder()) //参数为true表示数组流,json验证器
				.addLast("str_decoder", new StringDecoder(Charset.forName("UTF-8"))) //3.使用utf-8解码
				//ChannelOut(是按逆序)
//				.addLast("base64_encoder", new Base64Encoder())  //base64编码  //再base64编码
				//LineEncoder和StringEncoder作用等同，
				.addLast("line_encoder", new LineEncoder(Charset.forName("UTF-8")))  //1.先加入换行符
				.addLast("fastjson_en", new FastjsonHandler())
				.addLast("multiplexer", new EchoServerHandler(applicationContext)); //write以后会走out,往上找编码器,因此自定义编码器，一般放后边
	}
}
