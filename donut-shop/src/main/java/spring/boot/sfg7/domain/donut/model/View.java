//: spring.boot.sfg7.domain.donut.model.Views.java

package spring.boot.sfg7.domain.donut.model;


public interface View {

    /**
     * Summary view: Minimal information for quick listings
     * Includes: type, price
     */
    public interface Summary {}

    /**
     * Public view: Information suitable for public API consumers
     * Includes: Summary + glaze, toppings, isVegan
     */
    public interface Public extends Summary {}

    /**
     * Internal view: Additional details for internal use
     * Includes: Public + calories, bakedAt
     */
    public interface Internal extends Public {}

    /**
     * Admin view: Complete information for administrative purposes
     * Includes: All fields (no restrictions)
     */
    public interface Admin extends Internal {}

}
