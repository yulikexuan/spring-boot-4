//: spring.boot.sfg7.rest.mvc.web.util.WebUtils.java

package spring.boot.sfg7.rest.mvc.web.util;


import java.net.URI;
import java.util.function.Supplier;

import lombok.NonNull;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


public interface WebUtils {

    public static <T> URI buildLocation(
            @NonNull String path, @NonNull Supplier<T> idSupplier) {

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(path)
                .buildAndExpand(idSupplier.get())
                .toUri();
    }

}
