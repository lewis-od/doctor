package uk.co.lewis_od.doctor;

import java.util.HashMap;
import java.util.Map;

class Bindings {

    private final Map<Class<?>, Provider<?>> providerMap = new HashMap<>();
    private final Map<Class<?>, Class<?>> interfaceMap = new HashMap<>();

    <T> void bindProvider(final Class<? super T> clazz, final Provider<T> provider) {
        providerMap.put(clazz, provider);
    }

    @SuppressWarnings("unchecked")
    <T> Provider<T> getProvider(final Class<T> clazz) {
       Provider<?> provider = providerMap.get(clazz);
       if (provider == null) {
           return null;
       }

       return (Provider<T>) provider;
    }

    <T> void bindInterface(final Class<? super T> intface, final Class<T> implementation) {
        if (!intface.isInterface()) {
            throw new BindingException("Expected " + intface.getName() + " to be an interface.");
        }
        interfaceMap.put(intface, implementation);
    }

    public Class<?> getImplementingClass(final Class<?> intface) {
        return interfaceMap.get(intface);
    }

    public boolean isBoundToProvider(final Class<?> clazz) {
        return providerMap.containsKey(clazz);
    }

    public boolean isBoundToImplementation(final Class<?> clazz) {
        return interfaceMap.containsKey(clazz);
    }
}
