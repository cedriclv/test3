package com.example.test3.controllers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.test3.Fire;
import com.example.test3.Fireman;
import com.example.test3.FiremanRepository;

import lombok.var;

@WebMvcTest(FiremanController.class)
public class FiremanControllerTests {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FiremanRepository firemanRepository;

    @Test
	public void testGetVeteranSimple() throws Exception {

    var fireman = mock(Fireman.class);
    when(fireman.getId()).thenReturn(1L);
    when(fireman.getName()).thenReturn("champion");
    when(firemanRepository.getVeteran()).thenReturn(Optional.of(fireman));

    mockMvc.perform(get("/fireman/veteran"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(fireman.getId()))
    .andExpect(jsonPath("$.name").value("champion"));    
    }

    @Test
	public void testGetVeteranNoVeteran() throws Exception {

    when(firemanRepository.getVeteran()).thenReturn(Optional.empty());

    mockMvc.perform(get("/fireman/veteran"))
    .andExpect(status().isNotFound());    
    }

    // la première doit juste vérifier que le compte 
    // se fait bien avec plusieurs pompiers et feux
    // la deuxième doit spécifiquement vérifier qu'un même feu 
    // n'est pas compté deux fois même si deux firemen sont intervenus dessus

    @Test
	public void testGetstatsSeveralVeterans() throws Exception {
    //Arrange
    List<Fireman> firemen = new ArrayList<>();

    Fire fire1 = new Fire(2, Instant.now());
    Fire fire2 = new Fire(2, Instant.now());
    Fire fire3 = new Fire(2, Instant.now());
    Fire fire4 = new Fire(2, Instant.now());
    Fire fire5 = new Fire(2, Instant.now());

    Fireman fireman1 = new Fireman("Popeye");
    Fireman fireman2 = new Fireman("Olive");

    fireman1.getFires().add(fire1);
    fireman1.getFires().add(fire2);
    fireman1.getFires().add(fire3);
    fireman2.getFires().add(fire4);
    fireman1.getFires().add(fire4);
    
    firemen.add(fireman1);
    firemen.add(fireman2);
    
    when(firemanRepository.findAll()).thenReturn(firemen);

    //Act

    //Assert

    mockMvc.perform(get("/fireman/veteran/stats"))
    .andExpect(status().isOk())    
    .andExpect(jsonPath("$.firemenCount").value(2))
    .andExpect(jsonPath("$.firesCount").value(4));
    }
}
