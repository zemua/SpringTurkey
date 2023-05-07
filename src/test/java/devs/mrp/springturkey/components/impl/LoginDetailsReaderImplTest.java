package devs.mrp.springturkey.components.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoginDetailsReaderImpl.class})
class LoginDetailsReaderImplTest {

	@Autowired
	private LoginDetailsReaderImpl reader;

	@Test
	@WithMockUser(username = "basic@user.me", password = "password", roles = "USER")
	void testGetUsername() {
		String result = reader.getUsername();
		assertEquals("basic@user.me", result);
	}

}
