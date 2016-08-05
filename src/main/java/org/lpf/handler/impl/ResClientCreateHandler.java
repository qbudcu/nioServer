package org.lpf.handler.impl;

import java.io.IOException;

import org.lpf.handler.IHandler;
import org.lpf.proto.MsgClient.ResConnectCreate;
import org.lpf.proto.MsgCode.GameCode;

public class ResClientCreateHandler implements IHandler{
	private ResConnectCreate res;
	public void init(Object[] obj) {
		// TODO Auto-generated method stub
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("ResConnectCreateHandler excuted.");
	}

	public int msgCode() {
		return GameCode.RES_CLIENT_CREATE.getNumber();
	}

	public void setMsgData(byte[] data) throws IOException {
		res = ResConnectCreate.parseFrom(data);
	}
}
