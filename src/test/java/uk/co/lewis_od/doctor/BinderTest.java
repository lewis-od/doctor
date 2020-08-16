package uk.co.lewis_od.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BinderTest {

    private static final GreeterImpl greeterInstance = new GreeterImpl();

    private Bindings bindings;
    private Binder binder;

    @BeforeEach
    void beforeEach() {
        bindings = new Bindings();
        binder = new Binder(bindings);
    }

    @Test
    void bind_toProvider() {
        binder.bind(Greeter.class).toProvider(() -> greeterInstance);

        assertThat(bindings.contains(Greeter.class)).isTrue();
        Provider<Greeter> provider = bindings.get(Greeter.class);
        assertThat(provider.get()).isEqualTo(greeterInstance);
    }

    @Test
    void bind_toInstance() {
        binder.bind(Greeter.class).toInstance(greeterInstance);

        assertThat(bindings.contains(Greeter.class)).isTrue();
        Provider<Greeter> provider = bindings.get(Greeter.class);
        assertThat(provider.get()).isEqualTo(greeterInstance);
    }

    @Test
    void bind_toNonInterface() {
        assertThatThrownBy(() -> binder.bind(String.class))
                .isInstanceOf(BindingException.class)
                .hasMessage("Expected " + String.class.getName() + " to be an interface.");
    }
}
