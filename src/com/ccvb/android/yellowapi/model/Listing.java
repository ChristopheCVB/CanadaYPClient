package com.ccvb.android.yellowapi.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Listing implements Parcelable
{
	private String id;
	private String parentId;
	private boolean parent;
	
	private String name;
	
	public Location location = null;
	private double distance = -1.0;
	private Address address;
	
	private JSONObject jsonO;
	
	public Listing(JSONObject json) throws JSONException
	{
		this.parse(json);
	}
	
	/**
	 * @param json
	 * @throws JSONException
	 */
	private void parse(JSONObject json) throws JSONException
	{
		this.jsonO = json;
		this.id = json.getString("id");
		this.parentId = json.getString("parentId");
		this.parent = json.getBoolean("isParent");
		
		this.name = json.getString("name");
		
		if (!json.isNull("geoCode"))
		{
			JSONObject geoCode = json.getJSONObject("geoCode");
			
			this.location = new Location("YAPI");
			this.location.setLatitude(geoCode.getDouble("latitude") * 1000000);
			this.location.setLongitude(geoCode.getDouble("longitude") * 1000000);
		}
		
		if (json.getString("distance").length() > 0)
		{
			this.distance = json.getDouble("distance");
		}
		
		if (!json.isNull("address"))
		{
			this.address = new Address(json.getJSONObject("address"));
		}
	}
	
	public Listing(Parcel in)
	{
		try
		{
			this.parse(new JSONObject(in.readString()));
		}
		catch (Exception exception)
		{}
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public String getParentId()
	{
		return this.parentId;
	}
	
	public boolean isParent()
	{
		return this.parent;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Location getLocation()
	{
		return this.location;
	}
	
	public double getDistance()
	{
		return this.distance;
	}
	
	public Address getAddress()
	{
		return this.address;
	}
	
	@Override
	public String toString()
	{
		return this.jsonO.toString();
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(this.jsonO.toString());
	}
	
	public static final Parcelable.Creator<Listing> CREATOR = new Parcelable.Creator<Listing>()
			{
		@Override
		public Listing createFromParcel(Parcel in)
		{
			return new Listing(in);
		}
		
		@Override
		public Listing[] newArray(int size)
		{
			return new Listing[size];
		}
			};
}
