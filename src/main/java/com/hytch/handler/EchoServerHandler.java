package com.hytch.handler;

import com.hytch.handler.event.OneEvent;
import com.hytch.handler.model.Response;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;

/**
 * Created by linfp on 2017/7/13.
 * 事件捕获处理机制
 */
@Sharable//注解@Sharable可以让它在channels间共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static final Logger LOG = LoggerFactory.getLogger(EchoServerHandler.class);
	private static final String TAG = EchoServerHandler.class.getSimpleName();
	private static final String ECHO_REQ = "Hi，Lilinfeng. Welcome to Netty.$_";//\r\n
	private int counter;
	
	
	private final ConfigurableApplicationContext applicationContext;   //接收到客户端的消息进行处理
	
	public EchoServerHandler(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		LOG.info("ctx远程地址为" + ctx.channel().remoteAddress() + ",添加成功");
		channels.add(ctx.channel());  //以channelId作为唯一key，ctx.channel()为value
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		LOG.info("ctx远程地址为" + ctx.channel().remoteAddress() + ",移除成功");
		channels.remove(ctx.channel());
	}
	
	/**
	 * 连接建立的时候调用该方法
	 *
	 * @param ctx 事件流上下文
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		for (int i = 0; i < 1; i++) {
			Response<String> response = new Response<>();
			response.setResult(0);
			response.setMessage(ECHO_REQ);
			response.setData("you are right");
			ChannelFuture future = ctx.writeAndFlush(response);   //写事件
			future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		}
	}
	
	/**
	 * 关闭连接时，调用该方法
	 *
	 * @param ctx 事件流上下文
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		LOG.info("the handler关闭");
		channels.remove(ctx.channel());
	}
	
	/**
	 * 读取消息时调用
	 *
	 * @param ctx 事件流上下文
	 * @param msg 接收的消息体
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Response<OneEvent> response = new Response<>();
		OneEvent oneEvent = new OneEvent();
		oneEvent.setMessage("one");
		
		response.setResult(1);
		response.setData(oneEvent);
		response.setMessage("receive is ok");
		
		String content = (String) msg;
//		String[] temp = saveChannelUnicode(content, ctx.channel());
		LOG.info("This is " + ++counter + " times receive server : [" + content + "]");
		if ("close".equals(content)) {
			ctx.writeAndFlush("closed ok")
					.addListener(ChannelFutureListener.CLOSE);
		} else if ("webClosed".equals(content)) {
			applicationContext.close();
		} else {
			ctx.writeAndFlush(response);
		}
//		channels.writeAndFlush("I am OK", new IdMatcher(temp[1]));
	}
	
	/**
	 * 读取完成时调用
	 *
	 * @param ctx 事件流上下文
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) //flush掉所有写回的数据
//				.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE); //当flush完成后关闭channel
	}
	
	/**
	 * 异常捕获
	 *
	 * @param ctx   事件流上下文
	 * @param cause 异常原因
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		StringBuilder sb = new StringBuilder();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		cause.printStackTrace(printWriter);
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		LOG.error("异常编码" + sb.toString());
		ctx.writeAndFlush("send no json, close");
		ctx.close();
	}
	
	private String[] saveChannelUnicode(String msg, Channel channel) {
		String[] temp = msg.split("to");  //规则1to2
		channel.attr(AttributeKey.valueOf(temp[0])).setIfAbsent(temp[0]);
		
		return temp;
	}
	
	private final class IdMatcher implements ChannelMatcher {
		private final String id;
		
		IdMatcher(String id) {
			this.id = id;
		}
		
		@Override
		public boolean matches(Channel channel) {
			String userId = (String) channel.attr(AttributeKey.valueOf(id + "")).get();
			return this.id.equals(userId);
		}
	}
}
