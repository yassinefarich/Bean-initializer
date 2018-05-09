# Bean-initializer

Bean-initializer is a Java utilitiy dedicated to help java developers to initialize Objects with Complicated hierarchy .
Bean-initializer use only reflexive API , which make it easy to integrate in java projects .

**Note** : Project is under construction :construction: :construction:

## Getting Started

### Example of use

```java
  Country france = BeanInitializer.createNew(Country.class)
				.withOnlySubPropertiesWithClassName(Arrays.asList("City", "Coutry"));
		Assertions.assertThat(france.getName()).isNull();
		Assertions.assertThat(france.getCapitaCity()).isNotNull();
		Assertions.assertThat(france.getCapitaCity().getCityName()).isNull();
 ```
 
More example of uses will be available on UnitTests .

### Prerequisites
	Java 8
