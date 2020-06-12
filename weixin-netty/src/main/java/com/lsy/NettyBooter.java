package com.lsy;

import com.lsy.netty.WSServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            try {
                //启动WSServer
                WSServer.getInstance().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
