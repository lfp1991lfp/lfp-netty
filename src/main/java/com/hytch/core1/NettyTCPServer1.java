package com.hytch.core1;

import com.hytch.core.AbstractNettyServer;
import com.hytch.core.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by linfp on 2017/7/12.
 * netty服务端
 */
public class NettyTCPServer1 extends AbstractNettyServer {
	private static final Logger LOG = LoggerFactory.getLogger(NettyTCPServer1.class);
	
	private ServerBootstrap serverBootstrap;
	
	public NettyTCPServer1(NettyConfig nettyConfig,
	                       ChannelInitializer<? extends Channel> channelInitializer) {
		super(nettyConfig, channelInitializer);
	}
	
	@Override
	public TransmissionProtocol getTransmissionProtocol() {
		return TRANSMISSION_PROTOCOL.TCP;
	}
	
	@Override
	public void startServer() throws Exception {
		try {
			LOG.info("TCP server starting...");
			//InternalLoggerFactory默认使用Using SLF4J as the default logging framework
			serverBootstrap = new ServerBootstrap();
			Map<ChannelOption<Object>, Object> channelOptions = nettyConfig.getChannelOptions();
			if (null != channelOptions) {
				Set<ChannelOption<Object>> keySet = channelOptions.keySet();
				// 获取configuration配置到channelOption
				for (ChannelOption<Object> option : keySet) {
					serverBootstrap.option(option, channelOptions.get(option));
				}
				LOG.info("配置信息" + serverBootstrap.config().toString());
			}
			// reactor多线程模型，配置bossGroup和workGroup
			// bossGroup和workGroup使用spring容器管理
			serverBootstrap.group(getBossGroup(), getWorkerGroup())
					.channel(NioServerSocketChannel.class)
					.childHandler(getChannelInitializer());
			// 绑定端口，启动并监听
			
			Channel serverChannel = serverBootstrap
					.bind(nettyConfig.getSocketAddress())
					.sync()
					.channel();     //配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
			ALL_CHANNELS.add(serverChannel);
			LOG.info("TCP server started...");
			// 等待服务器  socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
//			serverChannel.closeFuture().sync();
		} catch (Exception e) {
			LOG.error("TCP Server start error {}, going to shut down", e);
			super.stopServer();
			throw e;
		}
	}
	
	@Override
	public void setChannelInitializer(ChannelInitializer<? extends Channel> initializer) {
		this.channelInitializer = initializer;
		serverBootstrap.childHandler(initializer);
	}
	
	@Override
	public String toString() {
		return "NettyTCPServer [socketAddress=" + nettyConfig.getSocketAddress()
				+ ", portNumber=" + nettyConfig.getPortNumber() + "]";
	}
}
