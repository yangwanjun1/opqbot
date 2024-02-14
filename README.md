## <center>OPQ_BOT</center>
本项目是和OPQ对接的一个消息处理框架，类似go-cq，基于SpringBoot3进行封装，开箱即用，对于用惯注解开发的友友们是更好的选择
什么是[OPQ](https://github.com/opq-osc/OPQ)?
第一步：下载安装OPQ并启动
第二步：添加本项目依赖坐标
第三步：配置yaml文件（注意正向ws和反向ws，本项目默认使用反向ws去连接opq）
```yaml
bot:
  ws: ws://127.0.0.1:9000/ws    #ws连接地址【启动opq时填写此处】
  self-id: 1234567              #bot qq 仅在本程序作为客户端socket时可用
  check-time: 5               #断线时重连时间间隔（单位秒）
  enabled-reverse-ws: false   #启用反向ws时上面的配置失效
  reverse-ws: /ws             #path路径[opq启动时填写ws地址时填写path默认 /ws]
  reverse-port: 9000          #opq的端口[用于发送消息]
  filter-bot: true            #是否过滤bot发送的信息
  thread-poll:                #线程池配置，默认配置
    core: 2
    max-size: 4
    keep-alive-time: 30
    block-size: 50
```
第四步：编写消息处理类@QQBot

@QQBot 继承了@Component注解，在需要处理消息的类上加上此注解，就会收到qq发过来的消息

@QQListener 事件监听器，作用于@QQBot 注解类的方法上，监听不同消息事件

MessageIntercept.class 消息拦截器，消息进来之前会到达该拦截器，可自行扩展

CompressImage.class 图片压缩器，实现该接口时，上传的图片会进行压缩

CacheImage.class 文件上传缓存器，实现该接口时，上传成功的文件会通过该类，在需要的时候注入此类即可获取缓存的图片


简单使用
```java
@QQBot
public class QQ {
    /**
     * 监听聊天事件
     */
    @QQListener(type = GroupEvent.class)
    public void groupChat(GroupEvent event){
        System.out.println("group=>"+event.getContent());
    }
    /**
     * 监听正则消息
     */
    @QQListener(type = GroupEvent.class,matcher = "^(你好)\\b")
    public void groupChat(GroupEvent event,Matcher matcher){
        System.out.println("group=>"+matcher.group());
    }

    /**
     * 监听红包事件
     * 当前版本仅能收到群手气红包和口令红包
     * 好友红包可以收到口令红包，但是无法抢红包
     * 好友转账时会收到转账数据
     */
    @QQListener(type = RedBagEvent.class)
    public void redBag(RedBagEvent event){
        if (event.getType() == SourceType.GROUP) {
            System.out.println("群红包");
        }else{
            System.out.println("好友红包");
        }
    }
    /**
     * 监听at消息
     */
    @QQListener(type = GroupEvent.class,action = Action.AT)
    public void groupAt(GroupEvent event){
        System.out.println("group=>"+event.getContent());
    }

    /**
     * 监听好友事件
     */
    @QQListener(type = FriendEvent.class)
    public void friend(FriendEvent event){
        System.out.println(event.getContent());
    }


    /**
     * 监听进群事件
     */

    @QQListener(type = InviteEvent.class)
    public void group(InviteEvent event){
        User info = event.getInviteeInfo();
        event.sendContent("欢迎《"+info.getNick()+"("+info.getUin()+")》进群，来了就别想跑了，亮出你的美照吧");
    }

    /**
     * 监听退群事件
     */
    @QQListener(type = ExitGroupEvent.class)
    public void exit(ExitGroupEvent event){
        User info = event.getUserInfo();
        event.sendContent("用户《"+info.getNick()+"("+info.getUin()+")》退出了群聊");
    }

    /**
     * 处理好友请求
     */
    @QQListener(type = RequestEvent.class)
    public void request(RequestEvent event){
        System.out.println(MsgUtils.toJsonString(event));
    }


}

```
目前功能会随着opq的完善而完善，在使用前，该框架可能会存在不少的bug，如建议请勿使用