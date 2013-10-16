package com.ccvb.android.yellowapi.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Address
{
	public String street;
	public String city;
	public String province;
	public String provinceCode;
	
	public Address(JSONObject json) throws JSONException
	{
		this.street = json.getString("street");
		this.city = json.getString("city");
		this.province = json.getString("prov");
		this.provinceCode = json.getString("pcode");
	}
	
	public String getStreet()
	{
		return this.street;
	}
	
	public String getCity()
	{
		return this.city;
	}
	
	public String getProvince()
	{
		return this.province;
	}
	
	public String getProvinceCode()
	{
		return this.provinceCode;
	}
	
	@Override
	public String toString()
	{
		return this.street + ", " + this.city;
	}
}
