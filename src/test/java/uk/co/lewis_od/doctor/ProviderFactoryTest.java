package uk.co.lewis_od.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.lewis_od.doctor.constructors.ArgsConstructor;
import uk.co.lewis_od.doctor.constructors.NoAnnotation;
import uk.co.lewis_od.doctor.constructors.NoArgsConstructor;
import uk.co.lewis_od.doctor.constructors.TwoConstructors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProviderFactoryTest {

    private Bindings bindings;

    @BeforeEach
    void beforeEach() {
        bindings = new Bindings();
    }

    @Test
    void createProvider_noArgsConstructor() {
        Provider<NoArgsConstructor> provider = ProviderFactory.createProvider(NoArgsConstructor.class, bindings);
        assertThat(provider.get()).isInstanceOf(NoArgsConstructor.class);
    }

    @Test
    void createProvider_argsConstructor() {
        bindings.bindProvider(NoArgsConstructor.class, NoArgsConstructor::new);

        Provider<ArgsConstructor> provider = ProviderFactory.createProvider(ArgsConstructor.class, bindings);

        assertThat(provider).isInstanceOf(ConstructorProvider.class);
        assertThat(provider.get()).isInstanceOf(ArgsConstructor.class);
    }

    @Test
    void createProvider_boundInterface() {
        bindings.bindInterface(Greeter.class, GreeterImpl.class);

        Provider<Greeter> provider = ProviderFactory.createProvider(Greeter.class, bindings);

        assertThat(provider.get()).isInstanceOf(GreeterImpl.class);
    }

    @Test
    void createProvider_twoConstructors() {
        assertThatThrownBy(() -> ProviderFactory.createProvider(TwoConstructors.class, bindings))
                .isInstanceOf(ProviderCreationException.class)
                .hasMessage("Class " + TwoConstructors.class.getName() + " should have only 1 constructor.");
    }

    @Test
    void createProvider_noInjectAnnotation() {
        assertThatThrownBy(() -> ProviderFactory.createProvider(NoAnnotation.class, bindings))
                .isInstanceOf(ProviderCreationException.class)
                .hasMessage("Class " + NoAnnotation.class.getName() + " should have a constructor annotated with @Inject");
    }
}
