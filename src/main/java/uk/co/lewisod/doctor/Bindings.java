package uk.co.lewisod.doctor;

import java.util.HashMap;
import java.util.Map;

public class Bindings {

    private final Map<Class<?>, Provider<?>> providerMap = new HashMap<>();

    public <T> void put(Class<? super T> clazz, Provider<T> provider) {
        providerMap.put(clazz, provider);
    }

    @SuppressWarnings("unchecked")
    public <T> Provider<T> get(Class<T> clazz) {
       Provider<?> provider = providerMap.get(clazz);
       if (provider == null) {
           return null;
       }

       return (Provider<T>) provider;
    }

    public boolean contains(final Class<?> clazz) {
        return providerMap.containsKey(clazz);
    }
}
