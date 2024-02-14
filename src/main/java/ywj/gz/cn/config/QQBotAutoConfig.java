package ywj.gz.cn.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnClass(QQBotAutoConfig.class)
@ComponentScan(basePackages = "ywj.gz.cn")
public class QQBotAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public QQBotAutoConfig autoConfig(){
        return new QQBotAutoConfig();
    }

}
