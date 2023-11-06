### 基于springboot封装的OpqBot机器人
本项目是基于OPQ进行封装的一个框架，通过简单的注解即可实现消息的收发，在使用该项目时，说明你已经运行起了你的机器人，并且掌握了一定的编程知识以及会使用springboot， 【项目依赖于springboot-web，请导入web模块】,
首先下载[opqbot.jar](https://github.com/yangwanjun1/opqbot/releases)包，放到本地并引入项目中【jdk 17+】，并导入下面的依赖

```xml
  <dependencys>
    <dependency>
        <groupId>net.coobird</groupId>
        <artifactId>thumbnailator</artifactId>
        <version>0.4.20</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.1.5</version>
    </dependency>
    <dependency>
        <groupId>org.java-websocket</groupId>
        <artifactId>Java-WebSocket</artifactId>
        <version>1.5.4</version>
    </dependency>
  </dependencys>

```
配置yaml
```yaml
opq:
  ws: ws://127.0.0.1:9000/ws  #ws连接地址
```

通过下面的例子，实现消息的收发
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
}
```

项目目前还在开发中（后续事件在逐渐完善）
