package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

public class FluxAndMonoGeneratorServiceTest {


    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux).expectNext("alex", "ben", "chloe")
                .verifyComplete();

        StepVerifier.create(namesFlux).expectNextCount(3).verifyComplete();


    }

    @Test
    void namesFlux_map(){
        var upperCaseFlux = fluxAndMonoGeneratorService.nameFlux_map();

        StepVerifier.create(upperCaseFlux).expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();


    }

    @Test
    void namesMoreThan3() {

        var lengthCaractherTest = fluxAndMonoGeneratorService.namesFlux_flatmap(3);

        StepVerifier.create(lengthCaractherTest).expectNext("ALEX", "CHLOE");

    }

    @Test
    void splitNames(){
        var splitNames = fluxAndMonoGeneratorService.namesFlux_flatmap(3);

        StepVerifier.create(splitNames).expectNext("A,L,E,X,C,H,L,O,E");
    }
}