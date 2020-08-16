package uk.co.lewis_od.doctor;

import uk.co.lewis_od.doctor.graph.DependencyGraph;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Injector {

    private final Bindings bindings = new Bindings();
    private final DependencyGraph graph = new DependencyGraph();

    public <T> void registerBinding(final Class<? super T> clazz, Provider<T> provider) {
        bindings.put(clazz, provider);
    }

    public <T> T getInstance(final Class<T> clazz) {
        if (clazz.isInterface() && !bindings.contains(clazz)) {
            throw new RuntimeException("No binding for interface " + clazz.getName());
        }

        if (!clazz.isInterface() && !graph.containsClass(clazz)) {
            addDependenciesToGraph(clazz);
            registerBindingsForDependencies(clazz);
        }

        Provider<T> provider = bindings.get(clazz);
        return provider.get();
    }

    void addDependenciesToGraph(final Class<?> root) {
        graph.addClass(root);

        List<Constructor<?>> publicConstructors = List.of(root.getConstructors());

        Optional<Constructor<?>> noArgsConstructor = publicConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny();

        List<Constructor<?>> annotatedConstructors = publicConstructors.stream()
                .filter(Injector::hasInjectAnnotation)
                .collect(Collectors.toList());

        if (noArgsConstructor.isPresent() || root.isInterface()) {
            return;
        }

        if (annotatedConstructors.size() != 1) {
            throw new ProviderCreationException(
                    "Class " + root.getName() + " should have a public constructor either with no parameters, or annotated @Inject");
        }

        Constructor<?> annotatedConstructor = annotatedConstructors.get(0);
        for (Parameter parameter : annotatedConstructor.getParameters()) {
            addDependenciesToGraph(parameter.getType());
            graph.addDependency(root, parameter.getType());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void registerBindingsForDependencies(final Class<?> root) {
        Set<Class<?>> visited = new LinkedHashSet<>();
        Stack<Class<?>> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Class<?> vertex = stack.pop();
            Set<Class<?>> dependencies = graph.getDependencies(vertex);

            if (!visited.contains(vertex)) {
                visited.add(vertex);

                if (dependencies.isEmpty()) {
                    if (!bindings.contains(vertex)) {
                        Class<T> clazz = (Class<T>) vertex;
                        bindings.put(clazz, createDefaultProvider(clazz));
                    }
                } else {
                    stack.push(vertex); // Come back to vertex once dependencies bound
                    dependencies.forEach(stack::push);
                }
            } else {
                // Revisiting a node now that it's dependencies should be bound
                Set<Class<?>> unboundDependencies = dependencies.stream()
                        .filter(dep -> !bindings.contains(dep))
                        .collect(Collectors.toSet());
                if (!unboundDependencies.isEmpty()) {
                    String errorMessage = "Missing providers for classes: "
                            + unboundDependencies.stream().map(Class::getName).collect(Collectors.joining(", "));
                    throw new IllegalStateException(errorMessage);
                }

                Class<T> clazz = (Class<T>) vertex;
                Constructor<T> constructor = (Constructor<T>) Stream.of(clazz.getConstructors()).filter(Injector::hasInjectAnnotation).findAny().orElseThrow();
                List<Provider<?>> dependencyProviders = Stream.of(constructor.getParameters())
                        .map(Parameter::getType)
                        .map(bindings::get)
                        .collect(Collectors.toUnmodifiableList());
                bindings.put(clazz, new ConstructorProvider<>(constructor, dependencyProviders));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Provider<T> createDefaultProvider(final Class<T> clazz) {
        List<Constructor<?>> publicConstructors = List.of(clazz.getConstructors());

        Optional<Constructor<?>> noArgsConstructor = publicConstructors.stream()
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny();

        if (noArgsConstructor.isEmpty()) {
            throw new ProviderCreationException(
                    "Expected " + clazz.getName() + " to have no dependencies and a no-args constructor.");
        }

        try {
            T instance = (T) noArgsConstructor.get().newInstance();
            return () -> instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ProviderCreationException(
                    "Unable to create instance of " + clazz.getName() + " from no-args constructor.", e);
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
