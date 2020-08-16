package uk.co.lewis_od.doctor;

import org.junit.jupiter.api.Test;
import uk.co.lewis_od.doctor.demo.Computer;
import uk.co.lewis_od.doctor.demo.Display;
import uk.co.lewis_od.doctor.demo.IntelProcessor;
import uk.co.lewis_od.doctor.demo.Memory;
import uk.co.lewis_od.doctor.demo.Motherboard;
import uk.co.lewis_od.doctor.demo.Mouse;
import uk.co.lewis_od.doctor.demo.Processor;
import uk.co.lewis_od.doctor.demo.Tower;
import uk.co.lewis_od.doctor.graph.DependencyGraph;

import static org.assertj.core.api.Assertions.assertThat;

class InjectorTest {

    @Test
    void createGraph() {
        Injector injector = new Injector();
        injector.addDependenciesToGraph(Computer.class);

        DependencyGraph graph = injector.getDependencyGraph();

        assertThat(graph.getDependencies(Computer.class))
                .containsExactlyInAnyOrder(Display.class, Mouse.class, Tower.class);
        assertThat(graph.getDependencies(Tower.class))
                .containsExactlyInAnyOrder(Processor.class, Memory.class, Motherboard.class);
    }

    @Test
    void inject_demo() {
        Injector injector = new Injector();
        injector.registerBinding(Processor.class, IntelProcessor::new);

        Computer computer = injector.getInstance(Computer.class);

        assertThat(computer.getDisplay()).isNotNull();
        assertThat(computer.getMouse()).isNotNull();
        assertThat(computer.getTower()).isNotNull();

        Tower tower = computer.getTower();
        assertThat(tower.getMemory()).isNotNull();
        assertThat(tower.getMotherboard()).isNotNull();
        assertThat(tower.getProcessor()).isInstanceOf(IntelProcessor.class);
    }

    @Test
    void inject_interfaceBindings() {
        Injector injector = new Injector();
        injector.registerBinding(Greeter.class, GreeterImpl::new);

        Printer printer = injector.getInstance(Printer.class);
        assertThat(printer.printHello()).isEqualTo("PRINTING: Hello");
    }
}
