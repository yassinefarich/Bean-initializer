package io.github.yfarich.beaninitializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecursiveInitializer {

	private static final Logger LOGGER = Logger.getLogger(BeanInitializer.class.getName());
	private static final boolean noErrorIsAccepted = false;
	private Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

	<T> T initializeRecursively(T object, List<Predicate<Field>> predicates) {
		return initializeRecursively(object, predicates, new ArrayList<>());
	}

	private <T> T initializeRecursively(T object, List<Predicate<Field>> predicates, List<Class> classStack) {

		try {
			classStack.add(object.getClass());
			// To prevent stack overflow when instantiating classes with composer pattern
			if (classHasBeenInitializedBefore(object, classStack)) {
				throw new RuntimeException("Composition detected : The class << " + object.getClass().getName()
						+ " >> Has been instanciated before in this branch");
			}

			if (!beanComeFromSuppliers(object.getClass())) {
				Field[] fields = object.getClass().getDeclaredFields();
				Arrays.stream(fields).filter(field -> !field.getType().isPrimitive()) // Remove primitive types
						.filter(field -> predicates.stream().anyMatch(stringPredicate -> stringPredicate.test(field)))
						.map(instantiateFieldsOn(object)).filter(Optional::isPresent).map(Optional::get)
						.forEach(fieldObject -> initializeRecursively(fieldObject, predicates,
								new ArrayList<>(classStack)));
			}

		} catch (RuntimeException e) {
			LOGGER.log(Level.WARNING, e.getMessage());

			if (noErrorIsAccepted)
				throw new RuntimeException(e.getMessage());
		}
		return object;
	}

	private <T> boolean classHasBeenInitializedBefore(T object, @SuppressWarnings("rawtypes") List<Class> classStack) {
		return classStack.stream().filter(calzz -> Objects.equals(calzz, object.getClass())).count() > 2;
	}

	private Function<Field, Optional<Object>> instantiateFieldsOn(Object object) {

		return field -> {
			try {
				Class<?> fieldClass = field.getType();

				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);

				Object fieldValue = field.get(object);
				if (fieldValue == null) {

					Optional<Supplier<?>> classSupplier = findSupplier(fieldClass);
					if (classSupplier.isPresent()) {
						field.set(object, classSupplier.get().get());
					} else {
						field.set(object, fieldClass.newInstance());
					}
				}

				fieldValue = field.get(object);
				field.setAccessible(isAccessible);
				return Optional.ofNullable(fieldValue);

			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				LOGGER.log(Level.WARNING, e.getMessage());

				if (noErrorIsAccepted)
					throw new RuntimeException(e.getMessage());
			}

			return Optional.empty();
		};
	}

	private <T> Optional<Supplier<?>> findSupplier(Class<T> clazz) {
		return Optional.ofNullable(suppliers.get(clazz));
	}

	private boolean beanComeFromSuppliers(Class<? extends Object> class1) {
		return suppliers.containsKey(class1);
	}

	public RecursiveInitializer withSuppliers(Map<Class<?>, Supplier<?>> beanSuppliers) {
		suppliers = beanSuppliers;
		return this;
	}
}
