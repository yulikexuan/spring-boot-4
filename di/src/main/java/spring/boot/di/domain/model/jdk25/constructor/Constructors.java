//: spring.boot.di.domain.model.jdk25.constructor.Constructors.java

package spring.boot.di.domain.model.jdk25.constructor;


class Constructors {

    private final int cpuCount;
    private final boolean reboot;

    /*
     * ERROR 1:
     * When a constructor delegates to another via this(...),
     * the delegated constructor (Constructors(int)) runs first.
     * Java's definite assignment rules consider a final field as "already assigned"
     * after the this(...) call returns.
     * You cannot assign a final field again after this(...)
     *   — it's treated as a duplicate assignment, which is forbidden for final fields.
     *
     * ERROR 2:
     * The delegated constructor Constructors(int) only sets cpuCount.
     * Since reboot is final and Constructors(int) never assigns it,
     * Java reports that reboot might not be initialized.
     * The no-arg constructor's attempt to assign it after this(3) is rejected
     * (per Error 1),  leaving reboot unassigned in Constructors(int).
     *
     * The fundamental rule:
     *   Once you delegate with this(...), the called constructor must fully
     *   initialize all final fields.
     *   The calling constructor cannot "finish the job" afterward.
     */
    public Constructors() {
        this(3);
        // reboot = false;
        // LINE Z
    }

    public Constructors(int numberBags) {
        this.cpuCount = numberBags;
        reboot = false;
    }

    public static void main(String[] args) {
        var seed = new Constructors();
        System.out.print(seed.cpuCount);
    }

} /// :~