package uk.co.lewisod.doctor.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VertexTest {

    @Test
    void constructor_nullValue() {
        assertThatThrownBy(() -> Vertex.forClass(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_equal() {
        Vertex v1 = Vertex.forClass(String.class);
        Vertex v2 = Vertex.forClass(String.class);

        assertThat(v1).isEqualTo(v2);
        assertThat(v2).isEqualTo(v1);
    }

    @Test
    void equals_notEqual() {
        Vertex v1 = Vertex.forClass(String.class);
        Vertex v2 = Vertex.forClass(Integer.class);

        assertThat(v1).isNotEqualTo(v2);
        assertThat(v2).isNotEqualTo(v1);
    }
}
