package jpabook.jpashop.domain;


import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String city;
    private String zipcode;
    private String street;

    protected Address() {

    }

    public Address(String city, String zipcode, String street) {
        this.city = city;
        this.zipcode = zipcode;
        this.street = street;
    }
}
