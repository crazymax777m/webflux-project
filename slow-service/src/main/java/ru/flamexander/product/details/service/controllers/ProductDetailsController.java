package ru.flamexander.product.details.service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.flamexander.product.details.service.dtos.ProductDetailsDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/v1/details")
public class ProductDetailsController {

    private final Map<Long, ProductDetailsDto> data = Map.of(
            1L, new ProductDetailsDto(1L, "Description #1"),
            2L, new ProductDetailsDto(2L, "Description #2")
    );

    @GetMapping("/{ids}")
    public ResponseEntity<?> getProductDetailsByIds(@PathVariable List<Long> ids) throws InterruptedException {
        Thread.sleep(2500 + (int)(Math.random() * 2500));
        if (ids.size() == 1) {
            return getProductDetailsById(ids.get(0));
        }

        List<ProductDetailsDto> result = new ArrayList<>();

        for (Long id: ids) {
            result.add(data.get(id));
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/")
    public Collection<ProductDetailsDto> getAllProductsDetails() throws InterruptedException {
        Thread.sleep(2500 + (int)(Math.random() * 2500));
        return data.values();
    }

    private ResponseEntity<ProductDetailsDto> getProductDetailsById(@PathVariable Long id) throws InterruptedException {
        ProductDetailsDto result = data.get(id);
        if (isNull(result)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

}
