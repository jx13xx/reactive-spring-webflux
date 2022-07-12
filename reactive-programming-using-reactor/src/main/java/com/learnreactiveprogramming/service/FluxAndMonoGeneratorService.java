package com.learnreactiveprogramming.service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
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

    public Flux<String> namesFlux_contactmap(int stringlength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringlength)
                .concatMap(s -> splitString(s));
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> namesFlux_transform_switchifEmpty(int stringlength){
        // filter the string whose length is greaters than 3

        Function<Flux<String>, Flux<String>> filterMap = name ->
                name.map(String::toUpperCase)
                        .filter(s -> s.length() > stringlength)
                        .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap);


        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }


    public Flux<String> namesFlux_transform(int stringlength){

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringlength)
                .flatMap(this::splitString);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default");

    }


    public Flux<String> explore_concat(){

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D","E", "F");

        return Flux.concat(abcFlux, defFlux);
    }



    public Mono<List<String>> namesMono_flatMap(int stringlenght){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringlenght)
                .flatMap(this::splitStringMono);
    }

    private Mono<List<String>> splitStringMono(String s){
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> explore_mergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));


        return Flux.mergeSequential(abcFlux, defFlux);
    }

    public Flux<String> explore_zip_1(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D", "E", "F");
        var flux3 = Flux.just("1", "2", "3");
        var flux4 = Flux.just("4", "5", "6");


        return Flux.zip(abcFlux, defFlux, flux3, flux4)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
    }

    public Flux<String> explore_zipWith(){
        var abcFlux = Flux.just("A","B","C");

        var defFlux = Flux.just("D","E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second);
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
