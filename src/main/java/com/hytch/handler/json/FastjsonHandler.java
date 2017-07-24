package com.hytch.handler.json;

import com.alibaba.fastjson.JSON;
import com.hytch.handler.model.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * json handler编码(encoder)处理器
 */
@ChannelHandler.Sharable
public class FastjsonHandler extends MessageToMessageEncoder<Response> {
	
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Response response, List<Object> list) throws
			Exception {
		String responseMsg = JSON.toJSONString(response);
		list.add(responseMsg);
	}
}
