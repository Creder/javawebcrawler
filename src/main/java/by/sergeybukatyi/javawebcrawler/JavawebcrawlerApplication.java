package by.sergeybukatyi.javawebcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class JavawebcrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavawebcrawlerApplication.class, args);
	}

}
