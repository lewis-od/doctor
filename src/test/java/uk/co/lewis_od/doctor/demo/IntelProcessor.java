package uk.co.lewis_od.doctor.demo;

import javax.inject.Inject;

public class IntelProcessor implements Processor {

    private final TransistorArray transistorArray;

    @Inject
    public IntelProcessor(final TransistorArray transistorArray) {
        this.transistorArray = transistorArray;
    }

    public TransistorArray getTransistorArray() {
        return transistorArray;
    }
}
