package io.github.yfarich.beaninitializer.beans;

public class Country {

	private City capitaCity;
	private String name;
	private double population;
	
	public City getCapitaCity() {
		return capitaCity;
	}
	public void setCapitaCity(City capitaCity) {
		this.capitaCity = capitaCity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPopulation() {
		return population;
	}
	public void setPopulation(Double population) {
		this.population = population;
	}
	
	
}
