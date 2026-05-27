//: guru.modulith.domain.product.service.ProductService.java

package guru.modulith.product.domain.service;


import java.time.Instant;

import guru.modulith.notification.domain.dto.NotificationDto;
import guru.modulith.product.domain.dto.ProductDto;
import guru.modulith.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


public interface ProductService {

    void create(Product product);

    void create(ProductDto productDto);
}


@Service
@NullMarked
@RequiredArgsConstructor
class ProductServiceImpl implements ProductService {

    private final ApplicationEventPublisher eventPublisher;
    private final ProductMapper productMapper;

    @Override
    public void create(Product product) {

        final NotificationDto notificationDto = new NotificationDto(
                Instant.now(),"SMS", product.name());

        eventPublisher.publishEvent(notificationDto);
    }

    public void create(ProductDto productDto) {

        final NotificationDto notificationDto = new NotificationDto(
                Instant.now(), "SMS", productDto.name());

        eventPublisher.publishEvent(notificationDto);
    }

}
