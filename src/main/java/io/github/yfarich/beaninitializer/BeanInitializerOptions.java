package io.github.yfarich.beaninitializer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 
 * This interface define the list of options to define how the
 * <b>subProperties</b> will be initialized.
 * 
 * @author farich
 * @param <T>
 * 
 */
public interface BeanInitializerOptions<T> {

    T withOnlySubPropertiesInPackages(List<String> packages);

    T withOnlySubPropertiesWithClassName(List<String> classNames);

    T withOnlySubPropertiesAccordingToPredicates(List<Predicate<Field>> classNames);

    <B> BeanInitializerOptions<T> withTypeSupplier(Class<B> clazz, Supplier<B> beanProvider);

    T withAllSubPropertiesExceptInPackages(List<String> packages);

    T withAllSubPropertiesExceptWithClassName(List<String> classNames);

    T withAllSubProperties();
}