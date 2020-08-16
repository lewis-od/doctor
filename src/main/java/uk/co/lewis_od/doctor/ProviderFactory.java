package uk.co.lewis_od.doctor;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ProviderFactory {

    private ProviderFactory() {}

    @SuppressWarnings("unchecked")
    static <T> Provider<T> createProvider(final Class<T> clazz, final Bindings bindings) {
        Class<?> implementation = clazz.isInterface() ? bindings.getImplementingClass(clazz) : clazz;

        Constructor<?>[] constructors = implementation.getConstructors();
        if (constructors.length != 1) {
            throw new ProviderCreationException("Class " + implementation.getName() + " should have only 1 constructor.");
        }

        Constructor<T> constructor = (Constructor<T>) constructors[0];
        if (constructor.getParameters().length == 0) {
            return fromNoArgsConstructor(constructor);
        }

        boolean annotatedWithInject = List.of(constructor.getAnnotations()).stream()
                .anyMatch(annotation -> annotation.annotationType() == Inject.class);
        if (!annotatedWithInject) {
            throw new ProviderCreationException("Class " + implementation.getName() + " should have a constructor annotated with @Inject");
        }

        return fromArgsConstructor(constructor, bindings);
    }

    private static <T> Provider<T> fromNoArgsConstructor(final Constructor<T> constructor) {
        return () -> {
            try {
                return constructor.newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static <T> Provider<T> fromArgsConstructor(final Constructor<T> constructor,
                                               final Bindings bindings) {
        List<Provider<?>> dependencyProviders = Stream.of(constructor.getParameters())
                .map(Parameter::getType)
                .map(bindings::getProvider)
                .collect(Collectors.toUnmodifiableList());
        return new ConstructorProvider<>(constructor, dependencyProviders);
    }
}
