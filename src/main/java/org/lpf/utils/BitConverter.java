package org.lpf.utils;
public class BitConverter {
	/**
	 * byte数组转int
	 * @param b
	 * @return
	 */
	public static int bytesToInt(byte[] b){
		int res = 0;
		res |= b[0] & 0xFF;
		res |= (b[1]<<8)&0xFF00;
		res |= (b[2]<<16)&0xFF0000;
		res |= (b[3]<<24)&0xFF000000;
		return res;
	}
	
	public static byte[] intToBytes(int num){
		byte[] res = new byte[4];
		res[0] = (byte)(num & 0xFF);
		res[1] = (byte)((num & 0xFF00)>>8);
		res[2] = (byte)((num & 0xFF0000)>>16);
		res[3] = (byte)((num & 0xFF000000)>>24);
		return res;
	}
}
