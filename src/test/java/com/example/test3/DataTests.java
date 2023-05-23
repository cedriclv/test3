package com.example.test3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.Query;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class DataTests {
    
    @Autowired
    FireRepository fireRepository;
    
    @Autowired
    FiremanRepository firemanRepository;


    @Test
    public void testCreateFire(){
        int severity = 8;
        Instant date = Instant.now();
    	var fire = new Fire(severity, date);

        fireRepository.saveAndFlush(fire);

        Optional<Fire> fromDB = fireRepository.findById(fire.getId());

        assertTrue(fromDB.isPresent());
        assertEquals(fire.getId(), fromDB.get().getId());
        assertEquals(date, fromDB.get().getDate());
        assertEquals(severity, fromDB.get().getSeverity());
    }

    @Test
    public void testAssignFiresToFireman(){

        //Arrange
        Fireman fireman = new Fireman("toto le pompier");

        Instant date = Instant.now();

        var fire1 = new Fire(3, date);
    	var fire2 = new Fire(4, date);
    	var fire3 = new Fire(5, date);

        fireman.addFire(fire1);
        fireman.addFire(fire2);
        fireman.addFire(fire3);
        
        fireRepository.saveAndFlush(fire1);
        fireRepository.saveAndFlush(fire2);
        fireRepository.saveAndFlush(fire3);
        firemanRepository.saveAndFlush(fireman);

        //Act
        Optional<Fireman> firemanFromDb = firemanRepository.findById(fireman.getId());
        Optional<Fire> fire1FromDb = fireRepository.findById(fire1.getId());

        //Assert
        assertTrue(firemanFromDb.isPresent());
        assertTrue(fire1FromDb.isPresent());
        assertEquals(firemanFromDb.get().getName(),fireman.getName(), "name of the fireman is not ok");
        assertTrue(firemanFromDb.get().getFires().contains(fire1), "fire1 must be in the list");
        assertTrue(firemanFromDb.get().getFires().contains(fire2), "fire2 must be in the list");
        assertTrue(firemanFromDb.get().getFires().contains(fire3), "fire3 must be in the list");
    }

    @Test
    public void testNegativeSeverity(){
        assertThrows(ConstraintViolationException.class, () -> {
            Fire fire = new Fire(-4, Instant.now());
            fireRepository.saveAndFlush(fire);
        }, "severity cannot be negative");
    }

    @Test
    public void getVeteran_ShouldReturnVeteran_When_SeveralFiremans(){
        //Arrange
        Instant date = Instant.now();
        
        Fireman fireman1 = new Fireman("toto le pompier");

        var fire1 = new Fire(3, date);
        var fire2 = new Fire(4, date);
    
        fireman1.addFire(fire1);
        fireman1.addFire(fire2);
            
        fireRepository.saveAndFlush(fire1);
        fireRepository.saveAndFlush(fire2);
        firemanRepository.saveAndFlush(fireman1);
    
        Fireman fireman2 = new Fireman("titi le pompier");
        
        var fire3 = new Fire(3, date);
        var fire4 = new Fire(4, date);
        var fire5 = new Fire(4, date);

        fireman2.addFire(fire3);
        fireman2.addFire(fire4);
        fireman2.addFire(fire5);

        fireRepository.saveAndFlush(fire3);
        fireRepository.saveAndFlush(fire4);
        fireRepository.saveAndFlush(fire5);
        firemanRepository.saveAndFlush(fireman2);

        //Act
        Optional<Fireman> veteran = firemanRepository.getVeteran();
        
        //Assert
        assertEquals(fireman2.getId(), veteran.get().getId(),"veteran not found");
    }

    @Test
    public void getVeteran_ShouldReturnEmpty_When_NoSeveralFiremans(){
        //Arrange
        Instant date = Instant.now();
        
        Fireman fireman1 = new Fireman("toto le pompier");

    
        Fireman fireman2 = new Fireman("titi le pompier");


        //Act
        Optional<Fireman> veteran = firemanRepository.getVeteran();
        
        //Assert
        assertTrue(veteran.isEmpty(),"veteran found but no veteran loaded");
    }

    @Test
    public void getVeteran_ShouldReturnFireman_When_OnlyOneFireman(){
        //Arrange
        Instant date = Instant.now();
        
        Fireman fireman1 = new Fireman("toto le pompier");
 
        var fire1 = new Fire(3, date);
        var fire2 = new Fire(4, date);
     
        fireman1.addFire(fire1);
        fireman1.addFire(fire2);
             
        fireRepository.saveAndFlush(fire1);
        fireRepository.saveAndFlush(fire2);
        firemanRepository.saveAndFlush(fireman1);
 
         //Act
         Optional<Fireman> veteran = firemanRepository.getVeteran();
         
         //Assert
         assertEquals(fireman1.getId(), veteran.get().getId(),"veteran not found");
     }
}