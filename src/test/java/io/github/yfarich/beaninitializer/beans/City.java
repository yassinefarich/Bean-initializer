package io.github.yfarich.beaninitializer.beans;

import java.util.List;

public class City {
	
	private String cityName ;
	private int phonePrefix;
	private List<Integer> cityZipCodes;
	private Country country;
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Integer getPhonePrefix() {
		return phonePrefix;
	}
	public void setPhonePrefix(Integer phonePrefix) {
		this.phonePrefix = phonePrefix;
	}
	public List<Integer> getCityZipCodes() {
		return cityZipCodes;
	}
	public void setCityZipCodes(List<Integer> cityZipCodes) {
		this.cityZipCodes = cityZipCodes;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public void setPhonePrefix(int phonePrefix) {
		this.phonePrefix = phonePrefix;
	}
	
	

}
