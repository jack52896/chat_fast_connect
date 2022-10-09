轻量级Web快速开发框架
-------------

[//]: # (> 关于我，欢迎关注  )

[//]: # (博客：[一旬一题]&#40;http://greens1995.com&#41;  掘金：[https://juejin.im/user/57ff552d5bbb50005b4e9ef9]&#40;https://juejin.im/user/57ff552d5bbb50005b4e9ef9&#41;)

[//]: # ()
[//]: # (持续关注和分享：Android 性能调优、单元测试和重构、实用中间件、各种好玩的特效和黑科技、和小众刁钻没卵用需求的折腾记录。)

[//]: # (&#40;如果你想打造个人品牌，把自己的介绍放在这里也是可以的&#41;)

[//]: # ()
[//]: # (Github项目README.md模板  )

[//]: # (（项目背景/作用介绍）)

[//]: # ()
[//]: # (#### 示例:)

[//]: # (把使用了该项目的案例放在这里。可以放APK下载链接，或者简单放几张截图。  )

[//]: # (（示例一开始就放出来，方便浏览者一眼就看出是不是想找的东西）)

### 特性
支持http，rpc请求，以及数据库连接开箱即用，只需配置可一键启动web服务

### 原理说明
本项目采用netty框架开发

chat_common --数据库服务

chat_server --web服务

chat_client --rpc客户端

chat_register_center --注册中心

可以根据自己的需要引入对应的依赖
### 下载安装
maven:
``` xml
maven clean install
```
之后在你的项目中引入对应模块的包
```
<dependency>
    <groupId>org.example</groupId>
    <artifactId>chat_client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.example</groupId>
    <artifactId>chat_server</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.example</groupId>
    <artifactId>chat_register_center</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```

[//]: # (&#40;说明项目的配置方法，android开源库多用Gradle导入&#41;)

### application.properties配置示例
``` xml
#http请求端口
netty.port=8080
#web接口所在的包
controller.path=controller
#是否主动开启数据库连接
dataSource.enable=true
#数据库驱动
driverName=com.mysql.cj.jdbc.Driver
#数据库连接
dataSource.url=jdbc:mysql://127.0.0.1:3306/chat?characterEncoding=UTF-8
#数据库用户
dataSource.userName=root
#数据库密码
dataSource.pwd=admin
#数据库映射文件目录
application.mapper.classPath=mapper.xml
# 初始化连接池
dataSource.initConnections=1
# 最大的连接池
dataSource.maxConnections=5
# 可支持最大的连接数
dataSource.maxActiveConnections=5
# 数据连接等待时间
dataSource.waitTime=1

#是否开启RPC传输
rpc.enable=true
#RPC服务注册目录
rpc.service.discovery.path=service
#RPC端口
rpc.port=8081
#RPC注册中心是否开启(客户端)
rpc.register.enbale=true
#RPC注册中心ip(客户端)
rpc.register.host=127.0.0.1
#RPC注册中心port(客户端)
rpc.register.port=8089

#注册中心端口
register.port=8089

#注册中心http端口
register.http.port=8090

#需要调用的服务名
rpc.application.name=server1

#服务名称
application.name=server1
```

### 启动代码示例
```
public class start {
    public static void main(String[] args) {
        Access.start();
    }
}
```
### TODO（可选）
注册中心 -v1.0已上线, 依赖导入
```
<dependency>
    <groupId>org.example</groupId>
    <artifactId>chat_register_center</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## License
遵守的协议 MulanPSL-2.0