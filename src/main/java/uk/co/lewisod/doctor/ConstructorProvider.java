package uk.co.lewisod.doctor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ConstructorProvider<T> implements Provider<T> {

    private final List<Provider<?>> dependencyProviders;
    private final Constructor<T> constructor;

    public ConstructorProvider(final Constructor<T> constructor,
                               final List<Provider<?>> dependencyProviders) {
        this.constructor = constructor;
        this.dependencyProviders = dependencyProviders;
    }

    @Override
    public T get() {
        Object[] parameterValues = dependencyProviders.stream().map(Provider::get).toArray();
        try {
            return constructor.newInstance(parameterValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
