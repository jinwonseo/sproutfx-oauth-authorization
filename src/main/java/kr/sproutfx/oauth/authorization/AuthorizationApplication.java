package kr.sproutfx.oauth.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
public class AuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApplication.class, args);
	}

}
