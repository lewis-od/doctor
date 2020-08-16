package uk.co.lewis_od.doctor.constructors;

import uk.co.lewis_od.doctor.constructors.ArgsConstructor;
import uk.co.lewis_od.doctor.constructors.NoArgsConstructor;

import javax.inject.Inject;

public class TwoConstructors {

    @Inject
    public TwoConstructors(final ArgsConstructor argsConstructor) {}

    @Inject
    public TwoConstructors(final NoArgsConstructor noArgsConstructor) {}
}
