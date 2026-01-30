//: spring.boot.sfg7.service.GreetingServiceImpl.java

package spring.boot.di.service;


class BaseGreetingService implements GreetingService {

    @Override
    public String greeting() {
        return BASE_GREETING;
    }

} /// :~