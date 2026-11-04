package com.mx.cbtis.gestor;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mx.cbtis.gestor.controller.UsuarioController;

@SpringBootTest
class Ejercicio5MyApiGestorApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		// El contexto completo arranca con H2 y registra controladores y el cifrador BCrypt
		assertNotNull(context.getBean(UsuarioController.class));
		assertInstanceOf(BCryptPasswordEncoder.class, context.getBean(PasswordEncoder.class));
	}

}
