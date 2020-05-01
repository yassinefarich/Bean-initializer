package io.github.yfarich.beaninitializer;

import io.github.yfarich.beaninitializer.beans.City;
import io.github.yfarich.beaninitializer.beans.Country;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanInitializerTest {

    @Test
    public void createNewTest() {
        City grenoble = BeanInitializer.createNew(City.class).withAllSubProperties();
        assertThat(grenoble).isNotNull();
        assertThat(grenoble.getCityName()).isNotNull();
        assertThat(grenoble.getPhonePrefix()).isNotNull();
    }

    @Test
    public void withAllSubPropertiesTest() {

        Country france = BeanInitializer.createNew(Country.class).withTypeSupplier(List.class, () -> new ArrayList<>()) // 'List'
                .withTypeSupplier(Double.class, () -> Double.valueOf(0)) // 'Double has no no argument constructor'
                .withAllSubProperties();

        assertThat(france.getName()).isNotNull();
        assertThat(france.getCapital()).isNotNull();
        assertThat(france.getPopulation()).isNotNull();
        assertThat(france.getCapital().getCityName()).isNotNull();
        assertThat(france.getCapital().getPhonePrefix()).isNotNull();
        assertThat(france.getCapital().getCityZipCodes()).isNotNull();
    }

    @Test
    public void withOnlySubPropertiesWithClassNameTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesWithClassName(Arrays.asList("City", "Coutry"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapital()).isNotNull();
        assertThat(france.getCapital().getCityName()).isNull();
        assertThat(france.getCapital().getCountry()).isNull();
    }

    @Test
    public void withOnlySubPropertiesInPackagesTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesInPackages(Arrays.asList("io.github.yfarich"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapital()).isNotNull();
        assertThat(france.getCapital().getCityName()).isNull();
    }

    @Test
    public void withOnlySubPropertiesAccordingToPredicatesTest() {
        Predicate<Field> isPrivate = field -> !Modifier.isPrivate(field.getModifiers());

        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesAccordingToPredicates(Arrays.asList(isPrivate));

        // Public Field
        assertThat(france.getName()).isNotNull();
        // Private Field
        assertThat(france.getCapital()).isNull();
        ;
    }

    @Test
    public void withAllSubPropertiesExceptInPackages() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptInPackages(Arrays.asList("java.lang"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapital()).isNotNull();
        assertThat(france.getCapital().getCityName()).isNull();
    };

    @Test
    public void withAllSubPropertiesExceptWithClassName() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptWithClassName(Arrays.asList("City"));

        assertThat(france.getName()).isNotNull();
        assertThat(france.getCapital()).isNull();
    };

    @Test
    public void initWithTest() {
        Country france = new Country();
        france.setName("France");

        france = BeanInitializer.initialize(france).withAllSubProperties();

        assertThat(france.getName()).isEqualTo("France");
        assertThat(france.getCapital()).isNotNull();
    };

    @Test(expected = RuntimeException.class)
    public void nonInitializabeClassTest() {
        BeanInitializer.createNew(List.class);
    };

}
