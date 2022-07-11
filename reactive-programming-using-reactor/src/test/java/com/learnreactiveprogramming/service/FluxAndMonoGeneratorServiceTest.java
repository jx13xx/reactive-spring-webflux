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
}