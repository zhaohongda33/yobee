package com.ccby.yobee;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhaohongda
 *
 */
public class Execution {

	private static final Logger LOG = Logger.getLogger(Execution.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		LOG.info("启动启动目录： " + System.getProperty("user.dir"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/spring-context.xml");
		context.start();

		LOG.info("================================金斗云自动报价系统启动成功=================================");

		synchronized (Execution.class) {
			while (true) {
				try {
					Execution.class.wait();
				} catch (InterruptedException e) {
					LOG.error("== synchronized error:", e);
				}
			}
		}
	}
}
