[![Build Status](https://travis-ci.org/yassinefarich/Bean-initializer.svg?branch=master)](https://travis-ci.org/yassinefarich/Bean-initializer)
# Bean-initializer

Bean-initializer is a Java utilitiy dedicated to help developers to initialize Objects with Complicated hierarchy .

Bean-initializer use only reflexive API , which make it easy to be integrated in java projects .


## Getting Started

### Example of use

 * Create new Object with all subObjects
```java
   City grenoble = BeanInitializer.createNew(City.class)
                    .withAllSubProperties();
 ```

 * Create new Object with Type suppliers
```java
   Country france = BeanInitializer.createNew(Country.class)
                    .withTypeSupplier(List.class, () -> new ArrayList<>()) // 'List' is an Interface
                    .withTypeSupplier(Double.class, () -> Double.valueOf(0)) // 'Double has no no argument constructor'
                    .withAllSubProperties();
 ```

 * Create new Object with only fileds of classe
```java
   Country france = BeanInitializer.createNew(Country.class)
                    .withOnlySubPropertiesWithClassName(Arrays.asList("City", "Coutry"));
 ```

 * Create new Object with predicates on fields
```java
   Predicate<Field> isPrivate = field -> !Modifier.isPrivate(field.getModifiers());
   Country france = BeanInitializer.createNew(Country.class)
                    .withOnlySubPropertiesAccordingToPredicates(Arrays.asList(isPrivate));
 ```


 
More examples are available on [Test class](https://github.com/yassinefarich/Bean-initializer/blob/master/src/test/java/io/github/yfarich/beaninitializer/BeanInitializerTest.java).

### Prerequisites
 * Java 8
 * no arg constructor
 * TypeSupplier for Abstract classes ,interfaces or Types with no arg constructor *(Example below)*
 ```java
     Country france = BeanInitializer.createNew(Country.class)
                       .withTypeSupplier(List.class, () -> new ArrayList<>()) // 'List' is Abstract type (Interface) 
                       .withTypeSupplier(Double.class, () -> Double.valueOf(0)) // 'Double' has no no argument constructor'
                       .withAllSubProperties();
  ```
