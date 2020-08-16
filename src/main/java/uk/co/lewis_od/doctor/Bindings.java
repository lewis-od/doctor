package uk.co.lewis_od.doctor;

import java.util.HashMap;
import java.util.Map;

public class Bindings {

    private final Map<Class<?>, Provider<?>> providerMap = new HashMap<>();

    public <T> void put(final Class<? super T> clazz, final Provider<T> provider) {
        providerMap.put(clazz, provider);
    }

    @SuppressWarnings("unchecked")
    public <T> Provider<T> get(final Class<T> clazz) {
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
