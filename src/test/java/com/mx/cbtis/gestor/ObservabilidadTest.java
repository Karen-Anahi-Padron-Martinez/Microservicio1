package com.mx.cbtis.gestor;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Endpoints de Actuator que usan Docker (health check) y Prometheus (métricas).
 * @AutoConfigureObservability: en pruebas Spring Boot desactiva la exportación de métricas; aquí se activa como en producción.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class ObservabilidadTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void healthResponde200ConEstadoUp() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void sondasLivenessYReadinessDisponibles() throws Exception {
		mockMvc.perform(get("/actuator/health/liveness")).andExpect(status().isOk());
		mockMvc.perform(get("/actuator/health/readiness")).andExpect(status().isOk());
	}

	@Test
	void prometheusExponeMetricasConLaEtiquetaDeLaAplicacion() throws Exception {
		// Una petición previa para que existan métricas http_server_requests
		mockMvc.perform(get("/buscarUsuarios")).andExpect(status().isOk());

		mockMvc.perform(get("/actuator/prometheus"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("jvm_memory_used_bytes")))
				.andExpect(content().string(containsString("http_server_requests_seconds_bucket")))
				.andExpect(content().string(containsString("application=\"Ejercicio5MyAPIGestor\"")));
	}

	@Test
	void endpointsSensiblesNoEstanExpuestos() throws Exception {
		mockMvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
		mockMvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
	}
}
