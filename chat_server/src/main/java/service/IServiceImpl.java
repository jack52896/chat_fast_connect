package service;

import annoation.RpcDiscovery;
import domain.User;

/**
 * @author yujie
 * @createTime 2022/10/7 18:11
 * @description
 */
@RpcDiscovery("IService")
public class IServiceImpl implements IService{
    @Override
    public String test(User msg){
        return msg+"21";
    }
}
