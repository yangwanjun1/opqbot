### 基于springboot封装的OpqBot机器人
本项目是基于OPQ进行封装的一个框架，通过简单的注解即可实现消息的收发，在使用该项目时，说明你已经运行起了你的机器人，并且掌握了一定的编程知识以及会使用springboot
【jdk 17+】， 接下来导入下面的依赖即可

```xml
  <dependencys>
<!--   opq -->
    <dependency>
        <groupId>io.github.yangwanjun1</groupId>
        <artifactId>OPQBot</artifactId>
        <version>1.0.2</version>
    </dependency>
  </dependencys>

```
配置yaml
```yaml
opq:
  ws: ws://127.0.0.1:9000/ws  #ws连接地址
  thread-poll:                    #线程池配置，一下参数是默认
    core: 2
    max-size: 4
    keep-alive-time: 30
    block-size: 50
```

通过下面的例子，实现消息的收发（一定要在spring扫描到的包下）
```java

@Opq
@Component
@Slf4j
public class OpqEvent {


//简单使用
    @OpqListener(type = GroupMessageEvent.class)
    public void test(GroupMessageEvent event){
        if (event.getContent()!=null) {
            log.info("收到群《{}》->《{}》的消息:{}", event.getGroup().getGroupName(), event.getGroup().getGroupCard(), event.getContent());
        }
        if (event.getImages()!=null){
            log.info("收到群《{}》->《{}》的图片:", event.getGroup().getGroupName(), event.getGroup().getGroupCard());
            event.getImages().forEach(i-> System.out.println(i.getUrl()));
        }
        if (event.getVideo()!=null){
            log.info("收到群《{}》->《{}》的视频:{}", event.getGroup().getGroupName(), event.getGroup().getGroupCard(), event.getVideo().getUrl());
        }
    }
    //配合正则使用
    @OpqListener(type = GroupMessageEvent.class,matcher = "^(你好)\\b")
    public void atMatcher(GroupMessageEvent event){
        log.info("收到消息:{}",event.getContent());
    }
    //红包事件
    @OpqListener(type = RedBagMessageEvent.class)
    public void red(RedBagMessageEvent event){
        if (event.getGroup()!=null){//空时为转账
            OnRedResult onRed = event.onRed();
            log.info("打开{}的红包，获得{}元",event.getGroup().getGroupCard(),onRed.getResponseData().getGetMoney()/100);
        }
    }
    //at事件
    @OpqListener(type = GroupMessageEvent.class,action = Action.AT)
    public void hallo(GroupMessageEvent event){
        log.info("收到at消息:{}",event.getContent());
    }
    //进群事件
    @OpqListener(type = InviteHandlerEvent.class)
    public void g(InviteHandlerEvent e){
        UserData info = e.getInviteeInfo();
        log.info("用户《{}》进入了群聊",info.getNick());
        e.sendGroupMsg(String.format("欢迎 %s 进入群聊", info.getNick()));
    }
    //退群事件
    @OpqListener(type = ExitGroupEvent.class)
    public void g(ExitGroupEvent e){
        UserData info = e.getUserInfo();
        log.info("用户《{}》离开了群聊",info.getNick());
        e.sendGroupMsg(String.format("用户 %s 离开了我们", info.getNick()));
    }
}
```

项目目前还在开发中（后续事件在逐渐完善）
