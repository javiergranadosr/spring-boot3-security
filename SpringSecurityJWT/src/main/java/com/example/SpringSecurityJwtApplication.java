package com.example;

import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SpringSecurityJwtApplication /*implements CommandLineRunner*/ {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}

	/*
	@Override
	public void run(String... args) throws Exception {
		System.out.println(passwordEncoder.encode("1234"));
	}*/


	// Forma utilizada sin utilizar el implements
	@Bean
	CommandLineRunner init() {
		return args -> {
			UserEntity admin = UserEntity.builder()
					.email("admin@demo.com")
					.username("admin.demo")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(Erole.valueOf(Erole.ADMIN.name()))
							.build()))
					.build();


			UserEntity user = UserEntity.builder()
					.email("user@demo.com")
					.username("user.demo")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(Erole.valueOf(Erole.USER.name()))
							.build()))
					.build();

			UserEntity guest = UserEntity.builder()
					.email("guest@demo.com")
					.username("guest.demo")
					.password(passwordEncoder.encode("1234"))
					.roles(Set.of(RoleEntity.builder()
							.name(Erole.valueOf(Erole.GUEST.name()))
							.build()))
					.build();

			userRepository.save(admin);
			userRepository.save(user);
			userRepository.save(guest);

		};
	}
}
