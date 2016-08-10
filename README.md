#nioServer

    封装了java nio和protobuf的一款简单的服务器通信框架。

##1. 设计概要

    1. IO模型采用JDK NIO
    
    2. 消息封装采用的protobuf，具体格式为：消息号（4byte）+数据长度（4byte）+数据。消息号用来区分的消息实体，数据长度用来解决分包问题。
    
    3. 采用反射+工厂模式的结构，利用XML文件来注册消息处理类。使用者只需要实现接口编写自己的消息处理类，然后在配置文件中注册后即可使用，简化了开发工作。   
##2. Demo

    Demo中包含了两个消息的处理。大体过程如下：
    
    1. 客户端与服务器建立连接，服务器返回ResClientCreate消息给客户端；
    
    2. 客户端收到消息后，在ResClientCreateHandler类中进行处理，并发送ReqRegisterClient给服务器；
    
    3. 服务器收到消息，在ReqRegisterClientHandler进行处理。
    
    服务端：
        将本项目导入Eclipse，运行org/lpf包下的nioServerTest，即可启动服务器。
        public class NioServerTest {
            public static void main(String[] args) throws IOException {
                NioServer server = new NioServer(6018);
                server.listen();
            }
        }
    
    客户端：
        客户端demo参照我的nioClient项目https://github.com/qbudcu/nioClient，同样在org/lpf/nioClientTest.
        public class NioClientTest {
            public static void main(String[] args) throws IOException {
                ConnManager.getInstance().getClient("localhost", 6018).connect();   
            }
        }
##3. 定制自己的消息业务

    1. 定制自己的消息协议（proto文件），如下所示
        option java_package = "org.lpf.proto";   
        option java_outer_classname = "MsgClient";
        //RES_CLIENT_CREATE = 100001 连接已经建立
        message ResConnectCreate{
        }
        //REQ_REGISTER_CLIENT = 100002 请求注册客户端   
        message ReqRegisterClient  {    
          required string clientId = 1;   
        }  
    
    2. 利用protobuf代码生成工具，生成java代码加入到项目中，保证客户端与服务器代码一致；
    
    3. 根据需要编写消息处理类，需要实现org.lpf.handler.Ihandler接口
        public class ReqRegisterClientHandler implements IHandler{
            private ReqRegisterClient res;
            public void init(Object[] obj) {
            }
            public void run() {
                System.out.println("ReqRegisterClientHandler excuted.");
            }
            public int msgCode() {
                return GameCode.REQ_REGISTER_CLIENT.getNumber();
            }
            public void setMsgData(byte[] data) throws IOException {
                res = ReqRegisterClient.parseFrom(data);
            }
        }
    
    4. 在org/lpf/config/configure.xml注册编写的消息处理类。
        <?xml version="1.0" encoding="UTF-8"?>
        <package>
            <handler name="ReqRegisterClientHandler" class="org.lpf.handler.impl.ReqRegisterClientHandler"></handler>
        </package>
        
##4. 未来工作

    1. 代码重构
    2. 串行设计，同一连接的业务在单个线程执行
