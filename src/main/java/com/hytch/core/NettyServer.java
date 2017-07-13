package com.hytch.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Created by linfp on 2017/7/12.
 * 服务端的基本配置
 */
public interface NettyServer extends Server {
	/**
	 * ServerBootstrap创建成功后会有一个ChannelInitializer(即pipeline factory), 本方法主要用于获取这个
	 * ChannelInitializer
	 *
	 * @return
	 */
	ChannelInitializer<? extends Channel> getChannelInitializer();
	
	/**
	 * 设置自己的ChannelInitializer
	 *
	 * @param initializer pipeline的工厂类，主要为每个新的链接创建一个pipeline
	 */
	void setChannelInitializer(ChannelInitializer<? extends Channel> initializer);
	
	/**
	 * 获取netty server的configuration
	 *
	 * @return .
	 */
	NettyConfig getNettyConfig();
}
