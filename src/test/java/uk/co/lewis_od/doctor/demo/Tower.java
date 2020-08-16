package uk.co.lewis_od.doctor.demo;

import javax.inject.Inject;

public class Tower {

    private final Processor processor;
    private final Memory memory;
    private final Motherboard motherboard;

    @Inject
    public Tower(final Processor processor, final Memory memory, final Motherboard motherboard) {
        this.processor = processor;
        this.memory = memory;
        this.motherboard = motherboard;
    }

    public Processor getProcessor() {
        return processor;
    }

    public Memory getMemory() {
        return memory;
    }

    public Motherboard getMotherboard() {
        return motherboard;
    }
}
