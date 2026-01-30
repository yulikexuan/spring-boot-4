//: spring.boot.di.domain.model.jdk25.Records.java

package spring.boot.di.domain.model.jdk25;


interface Records {

    record RecordClass(String title) {
        public RecordClass(String title) {
            this.title = title;
            System.out.println(title);
        }
    }

} /// :~