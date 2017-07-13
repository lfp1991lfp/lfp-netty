package com.hytch.core;

import java.net.InetSocketAddress;

/**
 * Created by linfp on 2017/7/12.
 * 服务器
 */
public interface Server {
	TransmissionProtocol getTransmissionProtocol();
	
	// 启动服务器
	void startServer() throws Exception;
	
	void startServer(int port) throws Exception;
	
	void startServer(InetSocketAddress socketAddress) throws Exception;
	
	// 关闭服务器
	void stopServer() throws Exception;
	
	InetSocketAddress getSocketAddress();
	
	// 服务器使用的协议
	enum TRANSMISSION_PROTOCOL implements TransmissionProtocol {
		TCP, UDP
	}
	
	interface TransmissionProtocol {
	
	}
}
