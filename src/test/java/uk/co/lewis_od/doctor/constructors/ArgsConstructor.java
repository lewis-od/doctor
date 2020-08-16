package uk.co.lewis_od.doctor.constructors;

import javax.inject.Inject;

public class ArgsConstructor {

    private final NoArgsConstructor instance;

    @Inject
    public ArgsConstructor(final NoArgsConstructor instance) {
        this.instance = instance;
    }

    public NoArgsConstructor getInstance() {
        return instance;
    }
}
