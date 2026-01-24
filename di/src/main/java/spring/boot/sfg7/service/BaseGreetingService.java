//: spring.boot.sfg7.service.GreetingServiceImpl.java

package spring.boot.sfg7.service;


import org.springframework.stereotype.Service;


@Service
class BaseGreetingService implements GreetingService {

    @Override
    public String greeting() {
        return BASE_GREETING;
    }

} /// :~