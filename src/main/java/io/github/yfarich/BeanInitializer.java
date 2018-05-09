package io.github.yfarich;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BeanInitializer<T> implements BeanInitializerOptions<T> {

	private T objectInstance;
	private static List<Predicate<Field>> ALWAYS_TRUE_PREDICATES = Arrays.asList(field -> true);

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

		return initializeRecursively(objectInstance, Arrays.asList(classNamesField));

	}

	@Override
	public T withOnlySubPropertiesWithClassName(List<String> classNames) {
		Objects.requireNonNull(classNames);

		Predicate<Field> classNamesField = field -> {
			String filedClassName = field.getType().getName();
			return classNames.stream().filter(className -> filedClassName.contains(className)).count() > 0;
		};

		return initializeRecursively(objectInstance, Arrays.asList(classNamesField));
	}

	@Override
	public T withOnlySubPropertiesAccordingToPredicates(List<Predicate<Field>> predicates) {
		Objects.requireNonNull(predicates);
		return initializeRecursively(objectInstance, predicates);
	}

	@Override
	public T withAllSubProperties() {
		return initializeRecursively(objectInstance, ALWAYS_TRUE_PREDICATES);
	}

	private static <T> T initializeRecursively(T object, List<Predicate<Field>> predicates) {

		Field[] fields = object.getClass().getDeclaredFields();

		Arrays.stream(fields).filter(field -> !field.getType().isPrimitive()) // Remove primitive types
				.filter(field -> predicates.stream().anyMatch(stringPredicate -> stringPredicate.test(field)))
				.map(instantiateFieldsOn(object)).filter(Optional::isPresent).map(Optional::get)
				.forEach(fieldObject -> initializeRecursively(fieldObject, predicates));

		return object;
	}

	private static Function<Field, Optional<Object>> instantiateFieldsOn(Object object) {

		return field -> {
			try {
				Class<?> fieldClass = field.getType();

				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);

				Object fieldValue = field.get(object);
				if (fieldValue == null) {
					field.set(object, fieldClass.newInstance());
				}

				fieldValue = field.get(object);
				field.setAccessible(isAccessible);
				return Optional.ofNullable(fieldValue);

			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				System.err.println("* Error : " + e.getMessage());
			}

			return Optional.empty();
		};
	}

}
