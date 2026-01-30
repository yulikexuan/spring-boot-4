//: spring.boot.sfg7.service.Presentation.java

package spring.boot.di.service;


public interface Presentation {

    String FULLY_PRESENTING = ">>> Fully presenting ... ";
    String BRIEFLY_PRESENTING = ">>> Briefly presenting ... ";

    String present();

    static Presentation briefPresentation() {
        return new BriefPresentation();
    }

    static Presentation fullPresentation() {
        return new FullPresentation();
    }
}

class BriefPresentation implements Presentation {

    @Override
    public String present() {
        return BRIEFLY_PRESENTING;
    }
}

class FullPresentation implements Presentation {

    @Override
    public String present() {
        return FULLY_PRESENTING;
    }
}