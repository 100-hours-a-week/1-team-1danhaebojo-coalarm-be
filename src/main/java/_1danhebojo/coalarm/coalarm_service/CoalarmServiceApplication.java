package _1danhebojo.coalarm.coalarm_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "_1danhebojo.coalarm.coalarm_service")
@EnableJpaRepositories(basePackages = "_1danhebojo.coalarm.coalarm_service.domain.alert.repository.jpa")
@EnableScheduling
@EntityScan(basePackages = "_1danhebojo.coalarm.coalarm_service.domain.alert.repository.entity") // ✅ 패키지 확인
public class CoalarmServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoalarmServiceApplication.class, args);
	}

}
