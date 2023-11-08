# <center> ![](https://avatars.githubusercontent.com/u/91020200?v=4)<center/>
## <center>OPQBOT</center>

本项目是基于OPQ进行封装的一个框架，通过简单的注解即可实现消息的收发，在使用该项目时，说明你已经运行起了你的机器人，并且掌握了一定的编程知识以及会使用springboot
【jdk 17+】， 接下来导入下面的依赖即可

```xml
  <dependencys>
<!--   opq已经上传至中央仓库 仓库更新可能慢些，可在release先下载jar使用 -->
    <dependency>
        <groupId>io.github.yangwanjun1</groupId>
        <artifactId>OPQBot</artifactId>
        <version>1.0.3</version>
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
  enabled-task: true        #是否开启ws自动重连
  close-exception: false    #是否开启异常抛出，关闭时以日志形式打印异常信息
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

    //临时消息
    @OpqListener(type = TemporarilyMessageEvent.class)
    public void temporarily(TemporarilyMessageEvent e){
        System.out.println("收到临时消息:"+e.getContent());
    }
    //退群事件
    @OpqListener(type = ExitGroupEvent.class)
    public void exit(ExitGroupEvent e){
        String mark = e.getUserInfo().getMark();
        e.sendGroupMsg(String.format("%s 离开了我们", mark));
    }
    //群通知事件，可根据事件类型eventType判断处理（此处会推送bot未处理的群消息通知)
    @OpqListener(type = GroupNoticeEvent.class)
    public void notice(GroupNoticeEvent e){
        System.out.println("收到群通知事件:"+e.getGroupName());
    }
}
```

项目目前还在开发中（后续事件在逐渐完善，如出现问题，欢迎进行反馈，一个人的能力是有限的，需要大家一起努力维护）
