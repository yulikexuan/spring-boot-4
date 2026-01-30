open module spring.boot.di {
    requires java.base;
    requires jakarta.annotation;
    requires static lombok;
    requires org.jspecify;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
}