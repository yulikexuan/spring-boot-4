//: spring.boot.sfg7.service.GreetingService.java

package spring.boot.sfg7.service;


public interface GreetingService {

    String BASE_GREETING = ">>> Hello everyone from base service!";

    String greeting();

    static GreetingService baseGreetingService() {
        return new BaseGreetingService();
    }

}
