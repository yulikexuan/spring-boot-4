//: spring.boot.sfg7.service.PrimaryGreetingService.java

package spring.boot.di.service;


class PrimaryGreetingService implements GreetingService {

    @Override
    public String greeting() {
        return PRIMARY_GREETING;
    }

} /// :~