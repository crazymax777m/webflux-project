package ru.flamexander.reactive.service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.DetailedProductDto;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.entities.Product;
import ru.flamexander.reactive.service.exceptions.ProductDetailsNotFoundException;
import ru.flamexander.reactive.service.services.ProductDetailsService;
import ru.flamexander.reactive.service.services.ProductsService;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/v1/detailed")
@RequiredArgsConstructor
public class ProductsDetailsController {
    private final ProductsService productsService;
    private final ProductDetailsService productDetailsService;

    @GetMapping("/demo")
    public Flux<ProductDetailsDto> getManySlowProducts() {
        Mono<ProductDetailsDto> p1 = productDetailsService.getProductDetailsById(1L);
        Mono<ProductDetailsDto> p2 = productDetailsService.getProductDetailsById(2L);
        Mono<ProductDetailsDto> p3 = productDetailsService.getProductDetailsById(3L);
        return p1.mergeWith(p2).mergeWith(p3);
    }

    @GetMapping("/")
    public CorePublisher<DetailedProductDto> getAllDetailedProducts() {
        return mergeProductAndDetails(
                productsService.findAll(),
                productDetailsService.getAllProductsDetails()
        );
    }

    @GetMapping("/{ids}")
    public CorePublisher<DetailedProductDto> getDetailedProductByIds(@PathVariable("ids") List<Long> ids) {
        if (ids.size() == 1) {
            return getDetailedProductById(ids.get(0));
        }

        return mergeProductAndDetails(
                productsService.findByIds(ids),
                productDetailsService.getProductDetailsByIds(ids)
        );
    }

    private Flux<DetailedProductDto> mergeProductAndDetails(Flux<Product> productDtos,
                                                            Flux<ProductDetailsDto> detailsDtos) {
        Flux<Map<Long, ProductDetailsDto>> detailsMap = detailsDtos.collectMap(ProductDetailsDto::getId)
                .cache()
                .repeat();

        return productDtos.zipWith(
                detailsMap,
                (product, detailsValue) -> {
                    ProductDetailsDto details = detailsValue.get(product.getId());
                    if (isNull(details)) {
                        return new DetailedProductDto(product.getId(), product.getName());
                    }
                    return new DetailedProductDto(product.getId(), product.getName(), details.getDescription());
                }
        );
    }

    private Mono<DetailedProductDto> getDetailedProductById(@PathVariable("id") Long id) {
        Mono<Product> productDto = productsService.findById(id);
        Mono<ProductDetailsDto> detailsDto = productDetailsService.getProductDetailsById(id);
        detailsDto = detailsDto.onErrorReturn(ProductDetailsNotFoundException.class, new ProductDetailsDto(id, ""));

        return productDto.zipWith(
                detailsDto,
                (product, details) -> new DetailedProductDto(product.getId(), product.getName(), details.getDescription())
        );
    }
}
