//: spring.boot.sfg7.domain.model.BookDto.java

package spring.boot.sfg7.domain.model;


import java.util.List;

import org.jspecify.annotations.NullMarked;


@NullMarked
public record BookDto(
        String title,
        String isbn,
        String publisher,
        List<String> authors) {

} /// :~