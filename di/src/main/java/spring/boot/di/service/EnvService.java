//: spring.boot.sfg7.service.EnvService.java

package spring.boot.di.service;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


public interface EnvService {

    String DEV_ENV = "Dev Environment";
    String QA_ENV = "QA Environment";

    String environment();

}


@Service
@Profile({ "dev", "default" })
class DevEnvService implements EnvService {

    @Override
    public String environment() {
        return DEV_ENV;
    }

}


@Service
@Profile("qa")
class QaEnvService implements EnvService {

    @Override
    public String environment() {
        return QA_ENV;
    }
}