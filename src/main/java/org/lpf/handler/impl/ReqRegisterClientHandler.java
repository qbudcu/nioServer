package org.lpf.handler.impl;

import java.io.IOException;

import org.lpf.handler.IHandler;
import org.lpf.proto.MsgClient.ReqRegisterClient;
import org.lpf.proto.MsgCode.GameCode;

public class ReqRegisterClientHandler implements IHandler{
	private ReqRegisterClient res;
	public void init(Object[] obj) {
		// TODO Auto-generated method stub
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("ReqRegisterClientHandler excuted.");
	}

	public int msgCode() {
		return GameCode.REQ_REGISTER_CLIENT.getNumber();
	}

	public void setMsgData(byte[] data) throws IOException {
		res = ReqRegisterClient.parseFrom(data);
	}

}
