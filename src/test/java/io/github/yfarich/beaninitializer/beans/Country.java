package io.github.yfarich.beaninitializer.beans;

public class Country {

	private City capital;
	public String name;
	private Double population;
	
	public City getCapital() {
		return capital;
	}
	public void setCapital(City capital) {
		this.capital = capital;
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
