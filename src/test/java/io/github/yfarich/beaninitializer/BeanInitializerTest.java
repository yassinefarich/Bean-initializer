package io.github.yfarich.beaninitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.github.yfarich.beaninitializer.beans.City;
import io.github.yfarich.beaninitializer.beans.Country;

public class BeanInitializerTest {

    @Test
    public void createNewTest() {
        City grenoble = BeanInitializer.createNew(City.class).withAllSubProperties();
        Assertions.assertThat(grenoble).isNotNull();
        Assertions.assertThat(grenoble.getCityName()).isNotNull();
        Assertions.assertThat(grenoble.getPhonePrefix()).isNotNull();
    }

    @Test
    public void withAllSubPropertiesTest() {

        Country france = BeanInitializer.createNew(Country.class).withTypeSupplier(List.class, () -> new ArrayList<>()) // 'List'
                .withTypeSupplier(Double.class, () -> Double.valueOf(0)) // 'Double has no no argument constructor'
                .withAllSubProperties();

        Assertions.assertThat(france.getName()).isNotNull();
        Assertions.assertThat(france.getCapitaCity()).isNotNull();
        Assertions.assertThat(france.getPopulation()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getCityName()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getPhonePrefix()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getCityZipCodes()).isNotNull();
    }

    @Test
    public void withOnlySubPropertiesWithClassNameTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesWithClassName(Arrays.asList("City", "Coutry"));

        Assertions.assertThat(france.getName()).isNull();
        Assertions.assertThat(france.getCapitaCity()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getCityName()).isNull();
        Assertions.assertThat(france.getCapitaCity().getCountry()).isNull();
    }

    @Test
    public void withOnlySubPropertiesInPackagesTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesInPackages(Arrays.asList("io.github.yfarich"));

        Assertions.assertThat(france.getName()).isNull();
        Assertions.assertThat(france.getCapitaCity()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getCityName()).isNull();
    }

    @Test
    public void withOnlySubPropertiesAccordingToPredicatesTest() {
        Predicate<Field> isPrivate = field -> !Modifier.isPrivate(field.getModifiers());

        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesAccordingToPredicates(Arrays.asList(isPrivate));

        // Public Field
        Assertions.assertThat(france.getName()).isNotNull();
        // Private Field
        Assertions.assertThat(france.getCapitaCity()).isNull();
        ;
    }

    @Test
    public void withAllSubPropertiesExceptInPackages() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptInPackages(Arrays.asList("java.lang"));

        Assertions.assertThat(france.getName()).isNull();
        Assertions.assertThat(france.getCapitaCity()).isNotNull();
        Assertions.assertThat(france.getCapitaCity().getCityName()).isNull();
    };

    @Test
    public void withAllSubPropertiesExceptWithClassName() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptWithClassName(Arrays.asList("City"));

        Assertions.assertThat(france.getName()).isNotNull();
        Assertions.assertThat(france.getCapitaCity()).isNull();
    };

    @Test
    public void initWithTest() {
        Country france = new Country();
        france.setName("France");

        france = BeanInitializer.initialize(france).withAllSubProperties();

        Assertions.assertThat(france.getName()).isEqualTo("France");
        Assertions.assertThat(france.getCapitaCity()).isNotNull();
    };

    @Test(expected = RuntimeException.class)
    public void nonInitializabeClassTest() {
        BeanInitializer.createNew(List.class);
    };

}
