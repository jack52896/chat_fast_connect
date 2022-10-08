import server.http.HttpRegsiterServer;
import server.rpc.RpcServer;

/**
 * @author yujie
 * @createTime 2022/10/8 11:36
 * @description
 */
public class ServiceApplication {

    public static void start(){
        RpcServer.start();
        HttpRegsiterServer.start();
    }

    public static void main(String[] args) {
        start();
    }

}
