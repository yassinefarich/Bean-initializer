package io.github.yfarich;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BeanInitializer<T> implements BeanInitializerOptions<T> {

	private static List<Predicate<Field>> ALWAYS_TRUE_PREDICATES = Arrays.asList(field -> true);

	private Map<Class<?>, Supplier<?>> beenSuppliers = new HashMap<>();

	private T objectInstance;

	private BeanInitializer(T instance) {
		objectInstance = instance;
	}

	public static <T> BeanInitializerOptions<T> initialize(T instance) {
		return new BeanInitializer<T>(instance);
	}

	public static <T> BeanInitializerOptions<T> createNew(Class<T> clazz) {
		T instance;
		try {
			instance = clazz.newInstance();
			return new BeanInitializer<T>(instance);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T withOnlySubPropertiesInPackages(List<String> packages) {
		Objects.requireNonNull(packages);

		Predicate<Field> classNamesField = field -> {
			String fieldPackageName = field.getType().getClass().getPackage().getName();
			return packages.stream().filter(packageName -> packageName.startsWith(fieldPackageName)).count() > 0;
		};
		return new RecursiveInitializer()
				.withSuppliers(beenSuppliers)
				.initializeRecursively(objectInstance, Arrays.asList(classNamesField));
	}

	@Override
	public T withOnlySubPropertiesWithClassName(List<String> classNames) {
		Objects.requireNonNull(classNames);

		Predicate<Field> classNamesField = field -> {
			String filedClassName = field.getType().getName();
			return classNames.stream().filter(className -> filedClassName.contains(className)).count() > 0;
		};

		return new RecursiveInitializer()
				.withSuppliers(beenSuppliers)
				.initializeRecursively(objectInstance, Arrays.asList(classNamesField));
	}

	@Override
	public T withOnlySubPropertiesAccordingToPredicates(List<Predicate<Field>> predicates) {
		Objects.requireNonNull(predicates);
		return new RecursiveInitializer()
				.withSuppliers(beenSuppliers)
				.initializeRecursively(objectInstance, predicates);
	}

	@Override
	public T withAllSubProperties() {
		return new RecursiveInitializer()
				.withSuppliers(beenSuppliers)
				.initializeRecursively(objectInstance, ALWAYS_TRUE_PREDICATES);
	}

	@Override
	public <TB> BeanInitializerOptions<T> withTypeSupplier(Class<TB> clazz, Supplier<TB> beanSupplier) {
		beenSuppliers.put(clazz, beanSupplier);
		return this;
	}

}
