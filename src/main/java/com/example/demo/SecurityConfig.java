package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> requests.requestMatchers("/cashcard/**").hasRole("Card-ROLE"))
				.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable());
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
		User.UserBuilder builder = User.builder();
		UserDetails sarah = builder.username("sarah1").password(passwordEncoder.encode("abc123")).roles("Card-ROLE")
				.build();

		UserDetails hankHasNoRole = builder.username("hank-has-no-role").password(passwordEncoder.encode("sqs123"))
				.roles("No-ROLE").build();
		return new InMemoryUserDetailsManager(sarah, hankHasNoRole);
	}
}