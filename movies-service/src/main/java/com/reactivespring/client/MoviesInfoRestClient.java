package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;


    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<MovieInfo> retrieveMovieInfo(String movieId){

        var url = moviesInfoUrl.concat("/{id}");
        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return  Mono.just(new MoviesInfoClientException("There is no Movieinfo available", clientResponse.statusCode().value()));
                    }

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new MoviesInfoClientException
                                            (responseMessage, clientResponse.statusCode().value())));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                "Server Exception in MovieInfoService" + responseMessage
                        ))))
                .bodyToMono(MovieInfo.class)
                .log();

    }
}
