package devs.mrp.springturkey;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@SpringBootTest
class SpringTurkeyApplicationTests {

	@MockBean
	ReactiveJwtDecoder reactiveJwtDecoder;

	@Test
	void contextLoads() {
	}

}
