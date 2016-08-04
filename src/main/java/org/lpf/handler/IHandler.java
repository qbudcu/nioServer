package org.lpf.handler;

import java.io.IOException;

public interface IHandler extends Runnable{
	void init(Object[] obj);
	int msgCode();
	void setMsgData(byte[] data) throws IOException;
}
