# <center> ![](https://avatars.githubusercontent.com/u/91020200?v=4)<center/>
## <center>OPQBOT</center>
本项目是基于OPQ进行封装的一个框架，使用spring boot项目即可轻松实现消息的收发和处理
什么是OPQ[点击传送](https://github.com/opq-osc/OPQ)?
如何使用？下载OPQ，获取token后启动OPQ

Windows
```shell
 OPQBot.exe  -port 9000 
 -token xxxxx
 -wsserver ws://127.0.0.1:9000/ws  #程序地址【不填写时默认正向，OPQ主动连接,docker容器不可用】
 -wthread 100
```
Linux
```shell
./OPQBot -port 9000
  -token xxxx
  -wsserver ws://127.0.0.1:9000/ws #程序地址【不填写时默认正向，OPQ主动连接,docker容器不可用】
  -wthread 100 #工作线程 (default 100)
```
新建springboot项目，导入opqbot依赖或者下载[opqbot.jar](https://github.com/yangwanjun1/opqbot/releases)，然后编写组件即可实现消息的收发，填写pom文件
```xml
  <dependencys>
<!--   opq已经上传至中央仓库 仓库更新可能慢些，可在release先下载jar使用 -->
    <dependency>
        <groupId>io.github.yangwanjun1</groupId>
        <artifactId>OPQBot</artifactId>
        <version>1.0.7</version>
    </dependency>
  </dependencys>

```
配置yaml
```yaml
opq:
  ws: ws://127.0.0.1:9000/ws  #ws连接地址【启动opq时没用填写ws时填写此处】
  thread-poll:                #线程池配置，默认配置
    core: 2
    max-size: 4
    keep-alive-time: 30
    block-size: 50
  enabled-task: true        #是否开启ws自动重连
  enabled-reverse-ws: false  #启用反向ws时上面的配置失效
  reverse-ws: /ws      #path路径[opq启动时填写ws地址时填写path默认 /ws]
  reverse-port: 9000  #opq的端口[用于发送消息]
  welcome: 你好，欢迎使用opqbot
  filter-bot: true   #是否过滤bot发送的信息默认true
  photo-catch: true #开启图片缓存
```

通过下面的例子，实现消息的收发（一定要在spring扫描到的包下）
```java

@Opq
@Component
@Slf4j
public class OpqEvent {
//    监听群事件
    @OpqListener(type = GroupMessageEvent.class)
    public void test(GroupMessageEvent event){
        log.info("收到群《{}》->《{}》的消息:{}", event.getGroup().getGroupName(), event.getGroup().getGroupCard(), event.getContent());
    }
    //配合正则使用
    @OpqListener(type = GroupMessageEvent.class,matcher = "^(你好)\\b")
    public void atMatcher(GroupMessageEvent event){
        log.info("收到消息:{}",event.getContent());
//        发送消息
        event.sendGroupMsg("你也好");
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
        //发送图片
        e.sendGroupImage(new File("C:\\1.jpg"),0.8);
    }
    //进群事件
    @OpqListener(type = InviteHandlerEvent.class)
    public void g(InviteHandlerEvent e){
        UserData info = e.getInviteeInfo();
        log.info("用户《{}》进入了群聊",info.getNick());
        //发送消息
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
        String nick = e.getUserInfo().getNick();
        e.sendGroupMsg(String.format("%s 离开了我们", nick));
    }
    //群通知事件，可根据事件类型eventType判断处理（此处会推送bot未处理的群消息通知)
    @OpqListener(type = GroupNoticeEvent.class)
    public void notice(GroupNoticeEvent e){
        System.out.println("收到群通知事件:"+e.getGroupName());
//        同意请求【当前仅监听未处理的状态事件】 事件类型 1 申请进群 2 被邀请进群 13退出群聊(针对管理员群主的推送事件) 15取消管理员 3设置管理员
        if (e.getEventType() == 1 || e.getEventType() == 2){ //同意进群
            e.handlerNotice(OptionType.GROUP_AGREE);
        }
    }
    //好友请求
    @OpqListener(type = FriendRequestEvent.class)
    public void request(FriendRequestEvent e){
        UserData data = e.getRequester();
        System.out.println(String.format("收到 %s 的好友请求", data.getMark()+data.getNick()));
    }
}
```
【OpqUtils是一个工具类，可根据需求使用】
项目目前还在开发中（后续事件在逐渐完善，如出现问题，欢迎进行反馈）
