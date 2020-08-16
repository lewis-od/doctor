# Doctor 👩‍⚕️
A very basic dependency injection system for Java.

Built purely for my own eduction - don't use it for anything serious!

Still very much work in progress 🛠

## Usage
To be instantiable by Doctor, classes must have a public constructor with
either no parameters, or annotated with the `javax.inject.Inject` 
annotation.

Doctor will create a dependency graph based on these constructors. If the
dependency graph isn't a directed tree, things will break.

To bind interfaces to implementations:
```java
Injector injector = new Injector(binder -> {
    binder.bind(Example.class).toClass(ExampleImpl.class);
});
Example instance = injector.getInstance(Example.class);
```
