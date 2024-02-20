package ywj.gz.cn.core;

import java.util.List;

/**
 * 多实例的时候，将bot的地址在此处配置
 */
public interface BotPluginList {

    List<Bot> getBotList();
}
