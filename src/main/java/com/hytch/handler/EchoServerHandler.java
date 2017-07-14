package com.hytch.handler;

import com.hytch.core.MyChannelInitializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
/**
 * Created by linfp on 2017/7/13.
 * 事件捕获处理机制
 */
@Sharable//注解@Sharable可以让它在channels间共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	private static final String ECHO_REQ = "Hi, Lilinfeng. Welcome to Netty.$_";
	private static final Logger LOG = LoggerFactory.getLogger(MyChannelInitializer.class);
	private int counter;
	
	public EchoServerHandler() {
	}
	
	/**
	 * 连接建立的时候调用该方法
	 *
	 * @param ctx 事件流上下文
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		for (int i = 0; i < 1; i++) {
			ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));   //写事件
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
	}
	
	/**
	 * 读取消息时调用
	 *
	 * @param ctx 事件流上下文
	 * @param msg 接收的消息体
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String content = (String) msg;
		LOG.info("This is " + ++counter + " times receive server : [" + content + "]");
		if ("close".equals(content)) {
			ctx.writeAndFlush("同意关闭成功")
					.addListener(ChannelFutureListener.CLOSE);
		}
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
		ctx.close();
	}
	
	/**
	 * 空闲时调用
	 *
	 * @param ctx 事件流上下文
	 * @param evt 空闲事件体
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
//			IdleStateEvent event = (IdleStateEvent) evt;
//			if (event.state() == IdleState.READER_IDLE)
//				LOG.info("read idle");
//			else if (event.state() == IdleState.WRITER_IDLE)
//				LOG.info("write idle");
//			else if (event.state() == IdleState.ALL_IDLE)
//				LOG.info("all idle");
//		}
	}
}
