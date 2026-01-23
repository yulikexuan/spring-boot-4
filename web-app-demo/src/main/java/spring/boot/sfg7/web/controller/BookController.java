//: spring.boot.sfg7.web.controller.BookController.java

package spring.boot.sfg7.web.controller;


import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.boot.sfg7.domain.service.BookService;


@NullMarked
@Controller
@RequiredArgsConstructor
class BookController {

    private final BookService bookService;

    @RequestMapping("/sfg7/books")
    String findAllBooks(final Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "books";
    }

} /// :~