//: spring.boot.di.domain.model.jdk25.sealed.ChildOfSealedTypes.java

package spring.boot.di.domain.model.jdk25.sealed;


class ChildOfSealedTypes {

    sealed interface Voices {}

    sealed interface Mobile extends Voices {
        non-sealed interface MagicPhone extends Mobile {}
    }

    static void main() {

    }

} /// :~