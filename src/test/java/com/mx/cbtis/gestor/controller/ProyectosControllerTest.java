package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@ExtendWith(MockitoExtension.class)
class ProyectosControllerTest {

    @Mock
    private ProtectosRepository proyectosRepository;

    @InjectMocks
    private ProyectosController proyectosController;

    private Proyectos proyectoPrueba;

    @BeforeEach
    void setUp() {
        proyectoPrueba = new Proyectos(1, "Proyecto Alfa", "Descripción de prueba", "2026-01-01", "2026-12-31", "EN_PROCESO", "bg.jpg");
    }

    @Test
    void testGetProyectos() {
        when(proyectosRepository.findAll()).thenReturn(Arrays.asList(proyectoPrueba));

        ResponseEntity<List<Proyectos>> resultado = proyectosController.getProyectos();

        assertNotNull(resultado);
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(1, resultado.getBody().size());
        assertEquals("Proyecto Alfa", resultado.getBody().get(0).getNombre());
        verify(proyectosRepository, times(1)).findAll();
    }

    @Test
    void testGetProyectoPorId() {
        when(proyectosRepository.findById(1)).thenReturn(Optional.of(proyectoPrueba));

        ResponseEntity<?> resultado = proyectosController.getProyecto(1, null);

        assertNotNull(resultado);
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        Proyectos body = (Proyectos) resultado.getBody();
        assertEquals("Proyecto Alfa", body.getNombre());
        verify(proyectosRepository, times(1)).findById(1);
    }

    @Test
    void testGetProyectoPorIdNoEncontrado() {
        when(proyectosRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<?> resultado = proyectosController.getProyecto(999, null);

        assertNotNull(resultado);
        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
        verify(proyectosRepository, times(1)).findById(999);
    }

    @Test
    void testAddProyecto() {
        when(proyectosRepository.save(any(Proyectos.class))).thenReturn(proyectoPrueba);

        ResponseEntity<Integer> status = proyectosController.addProyecto(proyectoPrueba);

        assertEquals(HttpStatus.CREATED, status.getStatusCode());
        assertEquals(1, status.getBody());
        verify(proyectosRepository, times(1)).save(proyectoPrueba);
    }
}
