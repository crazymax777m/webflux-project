package ru.flamexander.reactive.service.integrations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.exceptions.AppException;
import ru.flamexander.reactive.service.exceptions.ProductDetailsNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class ProductDetailsServiceIntegration {
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsServiceIntegration.class.getName());

    private final WebClient productDetailsServiceWebClient;

    public Mono<ProductDetailsDto> getProductDetailsById(Long id) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-ID: {}", id);
        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/{id}", id)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new ProductDetailsNotFoundException())
                )
                .onStatus(
                        HttpStatus::isError,
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToMono(ProductDetailsDto.class);
    }

    public Flux<ProductDetailsDto> getAllProductsDetails() {
        logger.info("SEND REQUEST FOR ALL PRODUCT_DETAILS");
        return productDetailsServiceWebClient
                .get()
                .uri("/api/v1/details/")
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToFlux(ProductDetailsDto.class);
    }

    public Flux<ProductDetailsDto> getProductDetailsByIds(List<Long> ids) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-ID: {}", ids);
        StringJoiner stringJoiner = new StringJoiner(",");
        ids.stream()
                .map(Object::toString)
                .forEach(stringJoiner::add);

        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/{ids}", stringJoiner.toString())
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToFlux(ProductDetailsDto.class);
    }
}
