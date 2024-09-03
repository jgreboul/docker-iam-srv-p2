package jgr.iam;

// External Objects
import org.springframework.boot.SpringApplication; // https://docs.spring.io/spring-boot/api/java/org/springframework/boot/SpringApplication.html
import org.springframework.boot.autoconfigure.SpringBootApplication; // https://docs.spring.io/spring-boot/api/java/org/springframework/boot/autoconfigure/SpringBootApplication.html

// IAM Main Application
@SpringBootApplication
public class iamMain {

	// Main
	public static void main(String[] args) {
		SpringApplication.run(iamMain.class, args);
	}
}
