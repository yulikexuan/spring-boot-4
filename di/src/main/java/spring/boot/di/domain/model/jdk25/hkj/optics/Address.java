//: spring.boot.di.domain.model.jdk25.hkj.optics.Address.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


public record Address(String street, String city, String postcode) {

    public static Address of(String street, String city, String postcode) {
        return new Address(street, city, postcode);
    }

    public static Address applyNewStreet(String newStreet, Address address) {
        return Address.of(newStreet, address.city, address.postcode);
    }

    public static final class Lenses {

        public static Lens<Address, String> street() {
            return Lens.of(Address::street, Address::applyNewStreet);
        }

    }
}
