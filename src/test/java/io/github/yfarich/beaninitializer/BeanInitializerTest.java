package io.github.yfarich.beaninitializer;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(france.getCapitaCity()).isNotNull();
        assertThat(france.getPopulation()).isNotNull();
        assertThat(france.getCapitaCity().getCityName()).isNotNull();
        assertThat(france.getCapitaCity().getPhonePrefix()).isNotNull();
        assertThat(france.getCapitaCity().getCityZipCodes()).isNotNull();
    }

    @Test
    public void withOnlySubPropertiesWithClassNameTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesWithClassName(Arrays.asList("City", "Coutry"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapitaCity()).isNotNull();
        assertThat(france.getCapitaCity().getCityName()).isNull();
        assertThat(france.getCapitaCity().getCountry()).isNull();
    }

    @Test
    public void withOnlySubPropertiesInPackagesTest() {
        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesInPackages(Arrays.asList("io.github.yfarich"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapitaCity()).isNotNull();
        assertThat(france.getCapitaCity().getCityName()).isNull();
    }

    @Test
    public void withOnlySubPropertiesAccordingToPredicatesTest() {
        Predicate<Field> isPrivate = field -> !Modifier.isPrivate(field.getModifiers());

        Country france = BeanInitializer.createNew(Country.class)
                .withOnlySubPropertiesAccordingToPredicates(Arrays.asList(isPrivate));

        // Public Field
        assertThat(france.getName()).isNotNull();
        // Private Field
        assertThat(france.getCapitaCity()).isNull();
        ;
    }

    @Test
    public void withAllSubPropertiesExceptInPackages() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptInPackages(Arrays.asList("java.lang"));

        assertThat(france.getName()).isNull();
        assertThat(france.getCapitaCity()).isNotNull();
        assertThat(france.getCapitaCity().getCityName()).isNull();
    };

    @Test
    public void withAllSubPropertiesExceptWithClassName() {
        Country france = BeanInitializer.createNew(Country.class)
                .withAllSubPropertiesExceptWithClassName(Arrays.asList("City"));

        assertThat(france.getName()).isNotNull();
        assertThat(france.getCapitaCity()).isNull();
    };

    @Test
    public void initWithTest() {
        Country france = new Country();
        france.setName("France");

        france = BeanInitializer.initialize(france).withAllSubProperties();

        assertThat(france.getName()).isEqualTo("France");
        assertThat(france.getCapitaCity()).isNotNull();
    };

    @Test(expected = RuntimeException.class)
    public void nonInitializabeClassTest() {
        BeanInitializer.createNew(List.class);
    };

}
