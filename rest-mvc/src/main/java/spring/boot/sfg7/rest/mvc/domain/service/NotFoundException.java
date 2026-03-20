//: spring.boot.sfg7.rest.mvc.domain.beer.service.BeerNotFoundException.java

package spring.boot.sfg7.rest.mvc.domain.service;


import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@NullMarked
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not Found!")
public class NotFoundException extends RuntimeException {

    private final UUID beerId;

    public NotFoundException(UUID beerId) {
        this.beerId = beerId;
    }

    public NotFoundException(UUID beerId, String message) {
        super(message);
        this.beerId = beerId;
    }

    public NotFoundException(UUID beerId, String message, Throwable cause) {
        super(message, cause);
        this.beerId = beerId;
    }

    public NotFoundException(UUID beerId, Throwable cause) {
        super(cause);
        this.beerId = beerId;
    }

} /// :~