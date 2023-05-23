package com.example.test3.controllers;

import java.util.Optional;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.test3.Fire;
import com.example.test3.Fireman;
import com.example.test3.FiremanRepository;

@RestController
@RequestMapping("/fireman")
public class FiremanController {

    @Autowired
    FiremanRepository firemanRepository;

    record FiremanStatsDTO(int firemenCount, int firesCount){}

    record FiremanData(Long id, String name, int firesCount) {
        static FiremanData fromFireman(Fireman fireman) {
            return new FiremanData(fireman.getId(), fireman.getName(), fireman.getFires().size());
        }
    }

    @GetMapping("/veteran")
    public FiremanData getVeteran() throws NotFoundException {
        Optional<Fireman> veteranMaybe = firemanRepository.getVeteran();
        Fireman veteran = veteranMaybe.orElseThrow(() -> new NotFoundException());        
        return FiremanData.fromFireman(veteran);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
    }

    @GetMapping("/veteran/stats")
    public FiremanStatsDTO getStats(){
        int nbOfFireman = firemanRepository.findAll().size();
        nbOfFireman = 0;
        List<Fire> fires = new ArrayList<>();
        int nbOfFire = 0;
        List<Fireman> firemens = firemanRepository.findAll();
        for (Fireman fireman : firemens) {
            nbOfFireman++;
            for (Fire fire : fireman.getFires()) {
                if(!fires.contains(fire)){
                    fires.add(fire);
                    nbOfFire++;
                }
            }
        } 
        return new FiremanStatsDTO(nbOfFireman, nbOfFire);
    }
}
