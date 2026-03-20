//: spring.boot.sfg7.rest.mvc.web.controller.ExceptionHandler.java

package spring.boot.sfg7.rest.mvc.web.controller;


import org.springframework.http.ResponseEntity;


/*
 * As `NotFoundException` has been annotated with `@ResponseStatus`,
 * it is no longer necessary to handle `NotFoundException` explicitly
 * in this `ExceptionHandler`.
 */
// @ControllerAdvice
class ExceptionHandler {

    // @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleBeerNotFoundException() {
        return ResponseEntity.notFound().build();
    }

} /// :~