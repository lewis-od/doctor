package uk.co.lewis_od.doctor.graph;

import java.util.Objects;

public final class Vertex {

    private final Class<?> value;

    private Vertex(final Class<?> value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public Class<?> getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return value.equals(vertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static Vertex forClass(final Class<?> clazz) {
        return new Vertex(clazz);
    }
}
