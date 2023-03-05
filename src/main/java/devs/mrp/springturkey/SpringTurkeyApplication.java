package devs.mrp.springturkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.groocraft.couchdb.slacker.annotation.EnableCouchDbRepositories;

@SpringBootApplication
@EnableCouchDbRepositories
public class SpringTurkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTurkeyApplication.class, args);
	}

}
