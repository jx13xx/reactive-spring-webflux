package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

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

    @Test
    void namesFlux_contactmap() {
        var contactNmes = fluxAndMonoGeneratorService.namesFlux_contactmap(3);

        StepVerifier.create(contactNmes).expectNext("A,L,E,X,C,H,L,O,E");
    }

//    @Test
//    void namesMono_flatMap() {
//        var splitMono = fluxAndMonoGeneratorService.namesMono_flatMap(3);
//
//        StepVerifier.create(splitMono).expectNext(List.of("A, L, E, X")).verifyComplete();
//    }

    @Test
    void namesFlux_transform() {

        var nameTransform = fluxAndMonoGeneratorService.namesFlux_transform(9);

        StepVerifier.create(nameTransform).expectNext("default").verifyComplete();
    }
//
//    @Test
//    void namesFlux_transform_switchifEmpty() {
//        var conditionalFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchifEmpty(9);
//
//        StepVerifier.create(conditionalFlux).expectNext("D","E","F", "A","U", "L", "T").verifyComplete();
//
//    }

    @Test
    void contactFlux() {
        var contactedFlux = fluxAndMonoGeneratorService.explore_concat();


        StepVerifier.create(contactedFlux)
                .expectNext("A", "B", "C", "D","E", "F")
                .verifyComplete();


    }

    @Test
    void explore_mergeSequential() {
        var contactedFlux = fluxAndMonoGeneratorService.explore_mergeSequential();


        StepVerifier.create(contactedFlux)
                .expectNext("A", "B", "C", "D","E", "F")
                .verifyComplete();


    }

    @Test
    void explore_zip1(){
        var value = fluxAndMonoGeneratorService.explore_zip_1();

        StepVerifier.create(value).expectNext("AD14", "BE25", "CF36").verifyComplete();
    }

    @Test
    void exploreZipWith(){
        var value = fluxAndMonoGeneratorService.explore_zipWith();

        StepVerifier.create(value).expectNext("AD", "BE", "CF").verifyComplete();
    }
}