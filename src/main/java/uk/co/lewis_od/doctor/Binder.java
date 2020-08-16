package uk.co.lewis_od.doctor;

public class Binder {

    public static class ToBinder<T> {

        private final Class<T> from;
        private final Bindings bindings;

        private ToBinder(final Class<T> from, final Bindings bindings) {
            this.from = from;
            this.bindings = bindings;
        }

        public void toProvider(final Provider<T> provider) {
            bindings.put(from, provider);
        }

        public void toInstance(final T instance) {
            bindings.put(from, () -> instance);
        }
    }

    private final Bindings bindings;

    public Binder(final Bindings bindings) {
        this.bindings = bindings;
    }

    public <T> ToBinder<T> bind(final Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new BindingException("Expected " + clazz.getName() + " to be an interface.");
        }
        return new ToBinder<>(clazz, bindings);
    }
}
