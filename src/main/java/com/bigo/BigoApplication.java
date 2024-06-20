package com.bigo;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 启动程序
 * 
 * @author bigo
 */
@Slf4j
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = {"com.bigo.project.bigo.wallet.jpaEntity","com.bigo.project.bigo.config.jpaEntity"})
@EnableJpaRepositories(basePackages = {"com.bigo.project.bigo.wallet.dao","com.bigo.project.bigo.config.dao"})
public class BigoApplication
{
    public static void main(String[] args)
    {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Johannesburg"));
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(BigoApplication.class, args);
        System.out.println(".______    __    _______   ______   \n" +
                "|   _  \\  |  |  /  _____| /  __  \\  \n" +
                "|  |_)  | |  | |  |  __  |  |  |  | \n" +
                "|   _  <  |  | |  | |_ | |  |  |  | \n" +
                "|  |_)  | |  | |  |__| | |  `--'  | \n" +
                "|______/  |__|  \\______|  \\______/  \n" +
                "                                    " );
    }

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) gracefulShutdown());
        return tomcatServletWebServerFactory;
    }

    private class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
        private volatile Connector connector;
        private final int waitTime = 10;

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
            this.connector.pause();
            Executor executor = this.connector.getProtocolHandler().getExecutor();
            try {
                if (executor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                        log.warn("Tomcat 进程在" + waitTime + " 秒内无法结束，尝试强制结束");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
