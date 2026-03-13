package srh_project.srh_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SrhProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(SrhProjectApplication.class, args);
        System.out.println("✅ SRH Project iniciado!");
        System.out.println("📌 Acesse: http://localhost:8080");
    }

}
