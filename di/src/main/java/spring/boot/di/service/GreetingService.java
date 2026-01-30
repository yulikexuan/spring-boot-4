//: spring.boot.sfg7.service.GreetingService.java

package spring.boot.di.service;


public interface GreetingService {

    String BASE_GREETING = ">>> Hello everyone from base service!";
    String PRIMARY_GREETING = ">>> Hello everyone from primary service!";

    String ENGLISH_GREETING = ">>> Hello Java World!";
    String FRENCH_GREETING = ">>> Salut tout le monde Java !";

    String greeting();

    static GreetingService baseGreetingService() {
        return new BaseGreetingService();
    }

    static GreetingService primaryGreetingService() {
        return new PrimaryGreetingService();
    }

    static GreetingService englishGreetingService() {
        return new EnglishGreetingService();
    }

    static GreetingService frenchGreetingService() {
        return new FrenchGreetingService();
    }

}
