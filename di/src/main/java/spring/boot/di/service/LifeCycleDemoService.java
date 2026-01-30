//: spring.boot.sfg7.service.LifecycleDemoService.java

package spring.boot.di.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import spring.boot.di.controller.DiController;


public interface LifeCycleDemoService extends
        BeanNameAware,
        BeanFactoryAware,
        ApplicationContextAware,
        InitializingBean,
        BeanPostProcessor,
        DisposableBean {

}


@Service
class SpringBoot4LifeCycleDemoService implements LifeCycleDemoService {

    private String javaVer;

    public SpringBoot4LifeCycleDemoService() {
        System.out.println("## I'm in the SpringBoot4LifeCycleDemoService Bean's Constructor ##");
    }

    @Value("${java.specification.version}")
    public void setJavaVer(String javaVer) {
        this.javaVer = javaVer;
        System.out.println("## 1 Properties Set. Java Ver: " + this.javaVer );
    }

    @Override
    // BeanNameAware
    // Set the name of the bean in the bean factory that created this bean.
    // Invoked after the population of normal bean properties but before an init
    // callback like InitializingBean's afterPropertiesSet or a custom init-method.'
    public void setBeanName(String name) {
        System.out.println("## 2 BeanNameAware My Bean Name is: " + name);
    }

    @Override
    // BeanFactoryAware::setBeanFactory
    // BeanFactory is the root interface for accessing a Spring bean container.
    //   it is also the basic client view of a bean container;
    //   further interfaces such as ListableBeanFactory and ConfigurableBeanFactory
    //   are available for specific purposes.
    //   This interface is implemented by objects that hold a number of bean definitions,
    //   each uniquely identified by a String name
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("## 3 BeanFactoryAware - Bean Factory has been set");
    }

    @Override
    // ApplicationContextAware::setApplicationContext
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        System.out.println("## 4 ApplicationContextAware - Application context has been set");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("## 5 postConstruct The Post Construct annotated method has been called");
    }

    @Override // InitializingBean
    public void afterPropertiesSet() throws Exception {
        System.out.println("## 6 `InitializingBean::afterPropertiesSet` Populate Properties The SpringBoot4LifeCycleDemoService Bean has its properties set!");
    }

    /*************************************************************************/

    // BeanPostProcessor: for every bean in the application context
    @Override
    // Called before any bean-specific initialization callbacks
    //   e.g., @PostConstruct, InitializingBean.afterPropertiesSet(),
    //   or a custom init-method) are executed.
    // The returned bean instance may be a wrapper around the original one.
    public @Nullable Object postProcessBeforeInitialization(
            Object bean, String beanName) throws BeansException {

        System.out.println("    ## BeanPostProcessor::postProcessBeforeInitialization: " + beanName);

        if (bean instanceof DiController dic) {
            System.out.println("    >>> Calling before initialization of DiController ...");
            dic.beforeInit();
        }

        return LifeCycleDemoService.super.postProcessBeforeInitialization(bean, beanName);
    }

    // BeanPostProcessor: for every bean in the application context
    @Override
    // Called after all bean-specific initialization callbacks have completed.
    // This is a common place to wrap the bean with a proxy for AOP purposes.
    public @Nullable Object postProcessAfterInitialization(
            Object bean, String beanName) throws BeansException {

        System.out.println("    ## BeanPostProcessor::postProcessAfterInitialization: " + beanName);

        if (bean instanceof DiController dic) {
            System.out.println("    >>> Calling after initialization of DiController ...");
            dic.afterInit();
        }

        return LifeCycleDemoService.super.postProcessAfterInitialization(bean, beanName);
    }

    /*************************************************************************/

    @PreDestroy
    public void preDestroy() {
        System.out.println("## 7 The @PreDestroy annotated method has been called");
    }

    @Override
    // DisposableBean::destroy
    // Invoked by the containing BeanFactory on destruction of a bean.
    public void destroy() throws Exception {
        System.out.println("## 8 `DisposableBean::destroy` The Lifecycle bean has been terminated");
    }

}

/// :~