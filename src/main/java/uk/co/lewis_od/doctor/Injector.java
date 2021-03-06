package uk.co.lewis_od.doctor;

import uk.co.lewis_od.doctor.graph.DependencyGraph;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Injector {

    private final Bindings bindings = new Bindings();
    private final DependencyGraph graph = new DependencyGraph();

    public Injector() {}

    public Injector(final Consumer<Binder> bindingFunction) {
        bindingFunction.accept(new Binder(bindings));
    }

    public <T> T getInstance(final Class<T> clazz) {
        if (clazz.isInterface() && !(bindings.isBoundToProvider(clazz) || bindings.isBoundToImplementation(clazz))) {
            throw new RuntimeException("No binding for interface " + clazz.getName());
        }

        if (!graph.containsClass(clazz)) {
            addDependenciesToGraph(clazz);
            registerBindingsForDependencies(clazz);
        }

        Provider<T> provider = bindings.getProvider(clazz);
        return provider.get();
    }

    private void addDependenciesToGraph(final Class<?> type) {
        graph.addClass(type);

        Class<?> implementation = type;
        if (type.isInterface()) {
            if (bindings.isBoundToProvider(type)) {
                return;
            } else if (!bindings.isBoundToImplementation(type)) {
                throw new BindingException("No implementation/provider bound for interface " + type.getName());
            }

            implementation = bindings.getImplementingClass(type);
        }

        addDependenciesFromConstructor(type, implementation);
    }

    public void addDependenciesFromConstructor(final Class<?> type, final Class<?> implementation) {
        List<Constructor<?>> publicConstructors = List.of(implementation.getConstructors());

        Optional<Constructor<?>> noArgsConstructor = publicConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny();

        if (noArgsConstructor.isPresent()) {
            return;
        }

        List<Constructor<?>> annotatedConstructors = publicConstructors.stream()
                .filter(Injector::hasInjectAnnotation)
                .collect(Collectors.toList());

        if (annotatedConstructors.size() != 1) {
            throw new ProviderCreationException(
                    "Class " + implementation.getName() + " should have a public constructor either with no parameters, or annotated @Inject");
        }

        Constructor<?> annotatedConstructor = annotatedConstructors.get(0);
        for (Parameter parameter : annotatedConstructor.getParameters()) {
            addDependenciesToGraph(parameter.getType());
            graph.addDependency(type, parameter.getType());
        }
    }

    private void registerBindingsForDependencies(final Class<?> root) {
        Set<Class<?>> visited = new LinkedHashSet<>();
        Deque<Class<?>> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Class<?> vertex = stack.pop();
            Set<Class<?>> dependencies = graph.getDependencies(vertex);

            if (!visited.contains(vertex)) {
                visited.add(vertex);

                if (dependencies.isEmpty()) {
                    createBindingIfRequired(vertex);
                } else {
                    stack.push(vertex); // Come back to vertex once dependencies are bound
                    dependencies.forEach(stack::push);
                }
            } else {
                // Revisiting a node now that it's dependencies should be bound
                checkDependenciesAreBound(dependencies);
                createBindingIfRequired(vertex);
            }
        }
    }

    private <T> void createBindingIfRequired(final Class<T> vertex) {
        if (!bindings.isBoundToProvider(vertex)) {
            bindings.bindProvider(vertex, ProviderFactory.createProvider(vertex, bindings));
        }
    }

    private void checkDependenciesAreBound(final Set<Class<?>> dependencies) {
        Set<Class<?>> unboundDependencies = dependencies.stream()
                .filter(dep -> !bindings.isBoundToProvider(dep))
                .collect(Collectors.toSet());
        if (!unboundDependencies.isEmpty()) {
            String errorMessage = "Missing providers for classes: "
                    + unboundDependencies.stream().map(Class::getName).collect(Collectors.joining(", "));
            throw new IllegalStateException(errorMessage);
        }
    }

    public DependencyGraph getDependencyGraph() {
        return graph;
    }

    private static boolean hasInjectAnnotation(final Constructor<?> constructor) {
        return Stream.of(constructor.getAnnotationsByType(Inject.class))
                .findAny().isPresent();
    }
}
