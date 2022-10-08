import proxy.RpcProxy;
import service.IService;

/**
 * @author yujie
 * @createTime 2022/10/7 18:12
 * @description
 */
public class test {
    public static void main(String[] args) {
        IService rpcService = (IService) RpcProxy.getRpcService(IService.class);
        System.out.println("结果是"+rpcService.test());
    }
}