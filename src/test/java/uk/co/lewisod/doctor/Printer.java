package uk.co.lewisod.doctor;

import javax.inject.Inject;

public class Printer {

    private final Greeter greeter;

    @Inject
    public Printer(final Greeter greeter) {
        this.greeter = greeter;
    }

    public String printHello() {
        return "PRINTING: " + greeter.sayHello();
    }
}
