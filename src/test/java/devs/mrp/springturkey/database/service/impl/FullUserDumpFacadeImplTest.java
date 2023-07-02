package devs.mrp.springturkey.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.configuration.ServiceBeansConfig;

@DataJpaTest
@EnableJpaRepositories(basePackages = "devs.mrp.springturkey.database.repository")
@EntityScan("devs.mrp.springturkey.database.*")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ServiceBeansConfig.class})
@EnableJpaAuditing
class FullUserDumpFacadeImplTest {

	@Autowired
	private FullUserDumpFacadeImpl fullUserDumpFacade;

	@Test
	@WithMockUser("some@user.me")
	void testFullUserDump() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		Map<Object,Object> expected = mapper.readValue(expectedJson(), Map.class);

		Map<Object,Object> result = fullUserDumpFacade.fullUserDump(null).block();

		assertEquals(expected, result);
		fail("Not yet implemented");
	}

	private String expectedJson() {
		return "{\"someJson\":\"someValue\"}";
	}

}
