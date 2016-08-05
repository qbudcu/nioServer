package org.lpf.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lpf.handler.IHandler;
import org.lpf.proto.MsgClient;
import org.lpf.proto.MsgClient.ReqRegisterClient;
import org.lpf.proto.MsgCode;
import org.lpf.utils.BitConverter;

import com.google.protobuf.GeneratedMessage;

public class NioServer 
{
    private static int DEFAULT_SERVERPORT = 6018;
    private static int DEFAULT_BUFFERSIZE = 1024;
    private ServerSocketChannel channel;
    private Selector selector;
    private ByteBuffer buffer;
    private int port;
    ExecutorService executorService = Executors.newCachedThreadPool();
    
    public NioServer(int port) throws IOException
    {
        this.port = port;
        this.channel = null;
        this.selector = Selector.open();
        this.buffer = ByteBuffer.allocate(DEFAULT_BUFFERSIZE);
        
    }
    

    protected void handleKey(SelectionKey key) throws IOException
    {
          if (key.isAcceptable()) 
          { // 鎺ユ敹璇锋眰
        	  System.out.println("recieve acceptable key.");
              ServerSocketChannel server = (ServerSocketChannel) key.channel();
              SocketChannel channel = server.accept();
              channel.configureBlocking(false);
              MsgClient.ResConnectCreate.Builder req = MsgClient.ResConnectCreate.newBuilder();
              MsgClient.ResConnectCreate info = req.build();
              sendMsg(channel, MsgCode.GameCode.RES_CLIENT_CREATE_VALUE, info);
             //瀹㈡埛socket閫氶亾娉ㄥ唽璇绘搷浣�
              channel.register(selector, SelectionKey.OP_READ);
          }
          else if (key.isReadable()) 
          {  // 璇讳俊鎭�
              SocketChannel channel = (SocketChannel) key.channel();
              int count = channel.read(buffer);
              if (count > 0) 
              {
            	 byte[] recieve_data = buffer.array();
            	 byte[] codeBytes = new byte[4];
             	 byte[] lengthBytes = new byte[4];
             	 System.arraycopy(recieve_data, 0, codeBytes, 0, 4);
             	 System.arraycopy(recieve_data, 4, lengthBytes, 0, 4);
             	 int code = BitConverter.bytesToInt(codeBytes);
             	 int length = BitConverter.bytesToInt(lengthBytes);
             	 byte[] data = new byte[length];
             	 System.arraycopy(recieve_data, 8, data, 0, length);
             	 IHandler handler = HandlerManager.getInstance().getHandler(code);
             	 handler.setMsgData(data);
             	 executorService.submit(handler);
             	 ReqRegisterClient res = ReqRegisterClient.parseFrom(data);
				 System.out.println("msgCode: "+code+" length: "+length+" ClientId: " + res.getClientId());
              }
              this.buffer.clear();//娓呯┖缂撳啿鍖�
          }

    }
    public void listen() throws IOException
    { //鏈嶅姟鍣ㄥ紑濮嬬洃鍚鍙ｏ紝鎻愪緵鏈嶅姟
        ServerSocket socket;
        channel = ServerSocketChannel.open(); // 鎵撳紑閫氶亾
        socket = channel.socket();   //寰楀埌涓庨�氬埌鐩稿叧鐨剆ocket瀵硅薄
        socket.bind(new InetSocketAddress(port));   //灏唖coket缁戝畾鍦ㄥ埗瀹氱殑绔彛涓�
        System.out.println("Server start. port: "+ port);
        //閰嶇疆閫氬埌浣跨敤闈為樆濉炴ā寮忥紝鍦ㄩ潪闃诲妯″紡涓嬶紝鍙互缂栧啓澶氶亾绋嬪簭鍚屾椂閬垮厤浣跨敤澶嶆潅鐨勫绾跨▼
        channel.configureBlocking(false);    
        channel.register(selector, SelectionKey.OP_ACCEPT);
        try 
        {
            while(true) 
            {
                this.selector.select();
                Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();
                while(iter.hasNext())
                {
                    SelectionKey key = (SelectionKey)iter.next();
                    iter.remove();
                    this.handleKey(key); 
                }
            }
        } 
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static void sendMsg(SocketChannel socket, int code, GeneratedMessage info) throws IOException{
		 byte[] data = info.toByteArray();
		 byte[] send_datas = new byte[data.length+8];
		 byte[] msg_code = BitConverter.intToBytes(code);
		 byte[] length = BitConverter.intToBytes(data.length);
		 System.arraycopy(msg_code, 0, send_datas, 0, 4);
		 System.arraycopy(length, 0, send_datas, 4, 4);
		 System.arraycopy(data, 0, send_datas, 8, data.length);
		 socket.write(ByteBuffer.wrap(send_datas));
		 //System.out.println(socketChannel.socket().getSendBufferSize());
		 System.out.println("data sent. Message code: "+code);
	}
}