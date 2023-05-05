package devs.mrp.springturkey.configuration;

import java.util.Collections;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class UserDetailsConfiguration {

	@Bean
	@Primary
	public UserDetailsService userDetailsService() {
		UserDetails basicUser = User.builder()
				.username("basic@user.me")
				.password("basicPass")
				.authorities(Collections.emptyList())
				.build();

		UserDetails otherUser = User.builder()
				.username("other@user.you")
				.password("otherPass")
				.authorities(Collections.emptyList())
				.build();

		return new InMemoryUserDetailsManager(basicUser, otherUser);
	}

}
