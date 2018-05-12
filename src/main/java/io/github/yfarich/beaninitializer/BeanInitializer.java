package io.github.yfarich.beaninitializer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BeanInitializer<T> implements BeanInitializerOptions<T> {

    private static final List<Predicate<Field>> ALWAYS_TRUE_PREDICATES = Arrays.asList(field -> true);

    private Map<Class<?>, Supplier<?>> beenSuppliers = new HashMap<>();

    private T objectInstance;

    private BeanInitializer(T instance) {
        objectInstance = instance;
    }

    public static <T> BeanInitializerOptions<T> initialize(T instance) {
        return new BeanInitializer<>(instance);
    }

    public static <T> BeanInitializerOptions<T> createNew(Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
            return new BeanInitializer<>(instance);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T withOnlySubPropertiesInPackages(List<String> packages) {
        Objects.requireNonNull(packages);
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                Arrays.asList(makePackageNamePredicate(packages)));
    }

    @Override
    public T withAllSubPropertiesExceptInPackages(List<String> packages) {
        Objects.requireNonNull(packages);
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                Arrays.asList(makePackageNamePredicate(packages).negate()));
    }

    private Predicate<Field> makePackageNamePredicate(List<String> packages) {
        return field -> {
            String fieldPackageName = field.getType().getPackage().getName();
            return packages.stream().filter(fieldPackageName::startsWith).count() > 0;
        };
    }

    @Override
    public T withOnlySubPropertiesWithClassName(List<String> classNames) {
        Objects.requireNonNull(classNames);
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                Arrays.asList(makeClassNamePredicate(classNames)));
    }

    @Override
    public T withAllSubPropertiesExceptWithClassName(List<String> classNames) {
        Objects.requireNonNull(classNames);
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                Arrays.asList(makeClassNamePredicate(classNames).negate()));
    }

    private Predicate<Field> makeClassNamePredicate(List<String> classNames) {
        return field -> {
            String filedClassName = field.getType().getName();
            return classNames.stream().filter(filedClassName::contains).count() > 0;
        };
    }

    @Override
    public T withOnlySubPropertiesAccordingToPredicates(List<Predicate<Field>> predicates) {
        Objects.requireNonNull(predicates);
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                predicates);
    }

    @Override
    public T withAllSubProperties() {
        return new RecursiveInitializer().withSuppliers(beenSuppliers).initializeRecursively(objectInstance,
                ALWAYS_TRUE_PREDICATES);
    }

    @Override
    public <B> BeanInitializerOptions<T> withTypeSupplier(Class<B> clazz, Supplier<B> beanSupplier) {
        beenSuppliers.put(clazz, beanSupplier);
        return this;
    }
}
