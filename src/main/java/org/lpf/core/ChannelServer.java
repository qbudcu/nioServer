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
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lpf.handler.IHandler;
import org.lpf.proto.MsgClient.ReqRegisterClient;
import org.lpf.utils.BitConverter;

public class ChannelServer 
{
    private static int DEFAULT_SERVERPORT = 6018;//默认端口
    private static int DEFAULT_BUFFERSIZE = 1024;//默认缓冲区大小为1024字节
    private ServerSocketChannel channel;
    private Selector selector;//选择器
    private ByteBuffer buffer;//字节缓冲区
    private int port;
    ExecutorService executorService = Executors.newCachedThreadPool();
    
    public ChannelServer(int port) throws IOException
    {
        this.port = port;
        this.channel = null;
        this.selector = Selector.open();
        this.buffer = ByteBuffer.allocate(DEFAULT_BUFFERSIZE);
        
    }
    

    protected void handleKey(SelectionKey key) throws IOException
    {
          if (key.isAcceptable()) 
          { // 接收请求
        	  System.out.println("recieve acceptable key.");
              ServerSocketChannel server = (ServerSocketChannel) key.channel();
              SocketChannel channel = server.accept();
              channel.configureBlocking(false);
             //客户socket通道注册读操作
              channel.register(selector, SelectionKey.OP_READ);
          }
          else if (key.isReadable()) 
          {  // 读信息
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
              /*BufferedInputStream in = new BufferedInputStream(channel.socket().getInputStream());
              byte[] recieve_data = new byte[1024];
              int count = in.read(recieve_data);
              if (count > 0) 
              {
            	 byte[] codeBytes = new byte[4];
             	 byte[] lengthBytes = new byte[4];
             	 
             	 System.arraycopy(recieve_data, 0, codeBytes, 0, 4);
             	 System.arraycopy(recieve_data, 4, lengthBytes, 0, 4);
             	 int code = BitConverter.bytesToInt(codeBytes);
             	 int length = BitConverter.bytesToInt(lengthBytes);
             	 byte[] data = new byte[length];
             	 System.arraycopy(recieve_data, 8, data, 0, length);
             	 ReqRegisterClient res = ReqRegisterClient.parseFrom(data);
				 System.out.println("msgCode: "+code+" length: "+length+" ClientId: " + res.getClientId());
				 try {
					 Thread.sleep(1000);
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
              } */
              this.buffer.clear();//清空缓冲区
          }

    }
    public void listen() throws IOException
    { //服务器开始监听端口，提供服务
        ServerSocket socket;
        channel = ServerSocketChannel.open(); // 打开通道
        socket = channel.socket();   //得到与通到相关的socket对象
        socket.bind(new InetSocketAddress(port));   //将scoket绑定在制定的端口上
        //配置通到使用非阻塞模式，在非阻塞模式下，可以编写多道程序同时避免使用复杂的多线程
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
                	System.out.println("recieve a key.");
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
}