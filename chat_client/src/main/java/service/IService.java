package service;

import annoation.RpcDiscovery;

@RpcDiscovery("IService")
public interface IService {
    String test(String msg);
}
