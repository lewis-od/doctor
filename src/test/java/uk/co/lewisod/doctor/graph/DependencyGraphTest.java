package uk.co.lewisod.doctor.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyGraphTest {

    private DependencyGraph graph;

    @BeforeEach
    void beforeEach() {
        graph = new DependencyGraph();
    }

    @Test
    void getDependencies() {
        graph.addClass(String.class);
        graph.addClass(Integer.class);
        graph.addClass(Boolean.class);
        graph.addClass(Short.class);

        graph.addDependency(String.class, Boolean.class);
        graph.addDependency(String.class, Integer.class);
        graph.addDependency(Integer.class, Short.class);

        assertThat(graph.getDependencies(String.class))
                .containsExactlyInAnyOrder(Boolean.class, Integer.class);
        assertThat(graph.getDependencies(Integer.class))
                .containsExactly(Short.class);
        assertThat(graph.getDependencies(Boolean.class)).isEmpty();
        assertThat(graph.getDependencies(Short.class)).isEmpty();
    }

    @Test
    void containsClass_true() {
        graph.addClass(String.class);
        assertThat(graph.containsClass(String.class)).isTrue();
    }

    @Test
    void containsClass_false() {
        assertThat(graph.containsClass(String.class)).isFalse();
    }
}
