package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewsRestClient {

    private WebClient webClient;


    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    public ReviewsRestClient(WebClient webClient){
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId){

        var url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return  Mono.empty();
                    }

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new ReviewsClientException
                                            (responseMessage)));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new ReviewsClientException(
                                "Server Exception in ReviewInfoService" + responseMessage
                        ))))
                .bodyToFlux(Review.class);
    }
}
