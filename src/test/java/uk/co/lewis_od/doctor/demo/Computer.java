package uk.co.lewis_od.doctor.demo;

import javax.inject.Inject;

public class Computer {

    private final Display display;
    private final Mouse mouse;
    private final Tower tower;

    @Inject
    public Computer(final Display display, final Mouse mouse, final Tower tower) {
        this.display = display;
        this.mouse = mouse;
        this.tower = tower;
    }

    public Display getDisplay() {
        return display;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Tower getTower() {
        return tower;
    }
}
