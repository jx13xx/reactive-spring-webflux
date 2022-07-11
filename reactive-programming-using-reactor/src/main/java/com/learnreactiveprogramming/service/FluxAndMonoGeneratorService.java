package com.learnreactiveprogramming.service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"));
    }


    public Mono<String> nameMono(){
        return Mono.just("Alex");
    }


    public Flux<String> nameFlux_map(){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase);

    }

    public Flux<String> nameFlux_map2() {
//        return Flux.fromIterable(List.of("alex", "ben", "chlore"))
//                .filter(name -> name.length() >= 4);


            return  Flux.fromIterable(List.of("alex", "ben", "chlore"))
                    .filter(name -> name.contains("l"));
    }

    public Flux<String> namesMoreThan3(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chlore"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .log();
    }


    public Flux<String> namesFlux_flatmap(int stringlength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringlength)
                .flatMap(s -> splitString(s));

    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }


    public static void main(String[] args) {


        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

//        fluxAndMonoGeneratorService.namesFlux().subscribe(name -> { System.out.println(name);
//        });

//
//        fluxAndMonoGeneratorService.nameMono().subscribe(name -> {
//            System.out.println("Mono " +name);
//        });

        fluxAndMonoGeneratorService.nameFlux_map2().subscribe(names -> {
            System.out.println("Filters " + names);
        });


    }
}
