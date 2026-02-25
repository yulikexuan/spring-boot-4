//: spring.boot.di.domain.model.jdk25.hkj.optics.CompanyTest.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@DisplayName("Test spring.boot.di.domain.model.jdk25.hkj.optics.Company Traversals - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CompanyTest {

    private Address hqAddress;

    private Employee alice;
    private Employee bob;
    private Employee carol;
    private Employee dave;

    private Department engineering;
    private Department marketing;

    private Company company;

    @BeforeEach
    void setUp() {
        hqAddress = Address.of("1 Main St", "Springfield", "12345");

        Address aliceAddr = Address.of("10 Elm St", "Springfield", "12345");
        Address bobAddr   = Address.of("20 Oak Ave", "Springfield", "12345");
        Address carolAddr = Address.of("30 Pine Rd", "Springfield", "12345");
        Address daveAddr  = Address.of("40 Cedar Ln", "Springfield", "12345");

        alice = Employee.of(1L, "Alice", aliceAddr);
        bob   = Employee.of(2L, "Bob",   bobAddr);
        carol = Employee.of(3L, "Carol", carolAddr);
        dave  = Employee.of(4L, "Dave",  daveAddr);

        engineering = Department.of("Engineering", alice, List.of(alice, bob));
        marketing   = Department.of("Marketing",   carol, List.of(carol, dave));

        company = Company.of("Acme Corp", hqAddress, List.of(engineering, marketing));
    }

    @Test
    void getAll_returns_all_departments() {
        Traversal<Company, Department> deptTraversal = Company.Traversals.departments();
        List<Department> departments = deptTraversal.getAll(company);
        assertThat(departments).containsExactly(engineering, marketing);
    }

    @Test
    void modify_transforms_all_departments() {
        Traversal<Company, Department> deptTraversal = Company.Traversals.departments();

        Company updated = deptTraversal.modify(
            dept -> Department.of(dept.name().toUpperCase(), dept.manager(), dept.staff()),
            company
        );

        List<String> names = updated.departments().stream().map(Department::name).toList();
        assertThat(names).containsExactly("ENGINEERING", "MARKETING");
    }

    @Test
    void set_replaces_all_departments_with_new_value() {
        Traversal<Company, Department> deptTraversal = Company.Traversals.departments();
        Department placeholder = Department.of("Placeholder", alice, List.of());

        Company updated = deptTraversal.set(placeholder, company);

        assertThat(updated.departments()).containsExactly(placeholder, placeholder);
    }

    @Test
    void andThen_composition_gets_all_employees_across_departments() {
        Traversal<Company, Department> deptTraversal  = Company.Traversals.departments();
        Traversal<Department, Employee> staffTraversal = Department.Traversals.staff();

        Traversal<Company, Employee> allStaff = deptTraversal.andThen(staffTraversal);

        List<Employee> employees = allStaff.getAll(company);
        assertThat(employees).containsExactly(alice, bob, carol, dave);
    }

    @Test
    void andThen_composition_modifies_all_employees_across_departments() {
        Traversal<Company, Department> deptTraversal  = Company.Traversals.departments();
        Traversal<Department, Employee> staffTraversal = Department.Traversals.staff();

        Traversal<Company, Employee> allStaff = deptTraversal.andThen(staffTraversal);

        Address newAddr = Address.of("99 New St", "Shelbyville", "99999");
        Company updated = allStaff.modify(
            emp -> Employee.of(emp.id(), emp.name().toUpperCase(), newAddr),
            company
        );

        List<String> names = updated.departments().stream()
            .flatMap(d -> d.staff().stream())
            .map(Employee::name)
            .toList();
        assertThat(names).containsExactly("ALICE", "BOB", "CAROL", "DAVE");
    }
}
