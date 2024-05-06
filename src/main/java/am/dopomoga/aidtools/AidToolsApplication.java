package am.dopomoga.aidtools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@SpringBootApplication
@EnableJpaRepositories(basePackages = "am.dopomoga.aidtools.repository.postgres")
public class AidToolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AidToolsApplication.class, args);
	}

}
