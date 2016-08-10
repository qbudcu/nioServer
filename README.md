nioServer

��װ��java nio��protobuf��һ��򵥵ķ�����ͨ�ſ�ܡ�

1. ��Ƹ�Ҫ

1. IOģ�Ͳ���JDK NIO

2. ��Ϣ��װ���õ�protobuf�������ʽΪ����Ϣ�ţ�4byte��+���ݳ��ȣ�4byte��+���ݡ���Ϣ���������ֵ���Ϣʵ�壬���ݳ�����������ְ����⡣

3. ���÷���+����ģʽ�Ľṹ������XML�ļ���ע����Ϣ�����ࡣʹ����ֻ��Ҫʵ�ֽӿڱ�д�Լ�����Ϣ�����࣬Ȼ���������ļ���ע��󼴿�ʹ�ã����˿���������   
2. Demo

Demo�а�����������Ϣ�Ĵ�������������£�

1. �ͻ�����������������ӣ�����������ResClientCreate��Ϣ���ͻ��ˣ�

2. �ͻ����յ���Ϣ����ResClientCreateHandler���н��д���������ReqRegisterClient����������

3. �������յ���Ϣ����ReqRegisterClientHandler���д���

����ˣ�
    ������Ŀ����Eclipse������org/lpf���µ�nioServerTest������������������
    public class NioServerTest {
        public static void main(String[] args) throws IOException {
            NioServer server = new NioServer(6018);
            server.listen();
        }
    }

�ͻ��ˣ�
    �ͻ���demo�����ҵ�nioClient��Ŀhttps://github.com/qbudcu/nioClient��ͬ����org/lpf/nioClientTest.
    public class NioClientTest {
        public static void main(String[] args) throws IOException {
            ConnManager.getInstance().getClient("localhost", 6018).connect();   
        }
    }
3. �����Լ�����Ϣҵ��

1. �����Լ�����ϢЭ�飨proto�ļ�����������ʾ
    option java_package = "org.lpf.proto";   
    option java_outer_classname = "MsgClient";
    //RES_CLIENT_CREATE = 100001 �����Ѿ�����
    message ResConnectCreate{
    }
    //REQ_REGISTER_CLIENT = 100002 ����ע��ͻ���   
    message ReqRegisterClient  {    
      required string clientId = 1;   
    }  

2. ����protobuf�������ɹ��ߣ�����java������뵽��Ŀ�У���֤�ͻ��������������һ�£�

3. ������Ҫ��д��Ϣ�����࣬��Ҫʵ��org.lpf.handler.Ihandler�ӿ�
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

4. ��org/lpf/config/configure.xmlע���д����Ϣ�����ࡣ
    <?xml version="1.0" encoding="UTF-8"?>
    <package>
        <handler name="ReqRegisterClientHandler" class="org.lpf.handler.impl.ReqRegisterClientHandler"></handler>
    </package>
4. δ������

1. �����ع�
2. ������ƣ�ͬһ���ӵ�ҵ���ڵ����߳�ִ��