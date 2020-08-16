package uk.co.lewisod.doctor.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyGraph {

    private final Map<Vertex, List<Vertex>> adjacentVertices = new HashMap<>();

    public void addClass(final Class<?> clazz) {
        adjacentVertices.putIfAbsent(Vertex.forClass(clazz), new ArrayList<>());
    }

    public boolean containsClass(final Class<?> clazz) {
        return adjacentVertices.containsKey(Vertex.forClass(clazz));
    }

    public void addDependency(final Class<?> from, final Class<?> to) {
        Vertex fromVertex = Vertex.forClass(from);
        Vertex toVertex = Vertex.forClass(to);
        adjacentVertices.get(fromVertex).add(toVertex);
    }

    public Set<Class<?>> getDependencies(final Class<?> clazz) {
        return adjacentVertices.get(Vertex.forClass(clazz)).stream()
                .map(Vertex::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }
}
