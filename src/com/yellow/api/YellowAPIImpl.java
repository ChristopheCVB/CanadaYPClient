/*
Copyright © 2011, Yellow Pages Group Co.  All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1)	Redistributions of source code must retain a complete copy of this notice, including the copyright notice, this list of conditions and the following disclaimer; and
2)	Neither the name of the Yellow Pages Group Co., nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT OWNER AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.yellow.api;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Yellow API implementation
 * 
 * @see com.yellow.api.YellowAPI
 */
public class YellowAPIImpl implements YellowAPI
{
	private static String baseURL = "http://api.yellowapi.com/";
	private static String sandboxURL = "http://api.sandbox.yellowapi.com/";
	
	private String language;
	private String apiKey;
	private String uid;
	private boolean sandbox;
	
	/**
	 * Initialize the Yellow API with needed information
	 * 
	 * @param language : language code for the results (need to be "en" or "fr")
	 * @param apiKey : API key provided by http://yellowapi.com
	 * @param uid : unique identifier of your application
	 * @param useSandbox : whenever the sand box should be used of not, API key must be used consequently
	 */
	public YellowAPIImpl(String language, String apiKey, String uid, boolean useSandbox)
	{
		this.language = language;
		this.apiKey = apiKey;
		this.uid = uid;
		
		this.sandbox = useSandbox;
	}
	
	/**
	 * @see com.yellow.api.YellowAPI
	 */
	@Override
	public JSONObject findBusiness(String what, String where, int page, int pageLength, long filter) throws YellowException
	{
		String flags = this.transformFilters(filter);;
		
		StringBuffer query = new StringBuffer("FindBusiness/?");
		query.append(String.format("pg=%d&what=%s&where=%s&pgLen=%d&lang=%s&fmt=json&sflag=%s&apikey=%s&UID=%s", page, URLEncoder.encode(what), URLEncoder.encode(where), pageLength,
				flags, this.language, this.apiKey, this.uid));
		
		try
		{
			return (JSONObject) this.request((this.sandbox ? YellowAPIImpl.sandboxURL : YellowAPIImpl.baseURL) + query.toString());
		}
		catch (IOException e)
		{
			throw new YellowException(e);
		}
		catch (JSONException e)
		{
			throw new YellowException(e);
		}
	}
	
	/**
	 * @see com.yellow.api.YellowAPI
	 */
	@Override
	public JSONObject findBusinessByPhone(String phone) throws YellowException
	{
		return this.findBusiness(phone, null, 1, 40, 0);
	}
	
	/**
	 * @see com.yellow.api.YellowAPI
	 */
	@Override
	public JSONObject findBusinessNear(String what, double latitude, double longitude, int page, int pageLength, long filter) throws YellowException
	{
		return this.findBusiness(what, "cZ" + longitude + ", " + latitude, page, pageLength, filter);
	}
	
	/**
	 * @see com.yellow.api.YellowAPI
	 */
	@Override
	public JSONObject getBusinessDetails(String prov, String city, String busName, String listingId) throws YellowException
	{
		String pattern = "[^a-zA-Z0-9]";
		StringBuffer query = new StringBuffer("GetBusinessDetails/?");
		query.append(String.format("prov=%s&&city=%s&bus-name=%s&listingId=%s&lang=%s&fmt=json&apikey=%s&UID=%s", prov.replaceAll(pattern, "-"),
				city.replaceAll(pattern, "-"), busName.replaceAll(pattern, "-"), listingId, this.language, this.apiKey, this.uid));
		
		try
		{
			return (JSONObject) this.request((this.sandbox ? YellowAPIImpl.sandboxURL : YellowAPIImpl.baseURL) + query.toString());
		}
		catch (IOException e)
		{
			throw new YellowException(e);
		}
		catch (JSONException e)
		{
			throw new YellowException(e);
		}
	}
	
	/**
	 * @see com.yellow.api.YellowAPI
	 */
	@Override
	public JSONObject findDealers(String parentId, int page, int pageLength) throws YellowException
	{
		StringBuffer query = new StringBuffer("FindDealer/?");
		query.append(String.format("pg=%d&pid=%s&pgLen=%d&lang=%s&fmt=json&apikey=%s&UID=%s", page, parentId, pageLength, this.language, this.apiKey,
				this.uid));
		
		try
		{
			return (JSONObject) this.request((this.sandbox ? YellowAPIImpl.sandboxURL : YellowAPIImpl.baseURL) + query.toString());
		}
		catch (IOException e)
		{
			throw new YellowException(e);
		}
		catch (JSONException e)
		{
			throw new YellowException(e);
		}
	}
	
	/**
	 * Change the language of the results returned by the API
	 * 
	 * @param language
	 *            : language code for the results (need to be "en" or "fr")
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}
	
	/**
	 * Change the API key used by the API
	 * 
	 * @param apiKey
	 *            : API key provided by http://yellowapi.com
	 */
	public void setAPIKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	/**
	 * Change the udid used by the API
	 * 
	 * @param uid
	 *            : unique identifier of your application
	 */
	public void setUID(String uid)
	{
		this.uid = uid;
	}
	
	/**
	 * Set the usage of the sand box
	 * 
	 * @param sandbox
	 *            : whenever the sand box should be used of not, API key must be
	 *            used consequently
	 */
	public void useSandbox(boolean sandbox)
	{
		this.sandbox = sandbox;
	}
	
	private String transformFilters(long filter)
	{
		String result = "";
		
		if ((filter & YellowAPI.FILTER_BUS_NAME) == YellowAPI.FILTER_BUS_NAME)
		{
			result += "bn-";
		}
		
		if ((filter & YellowAPI.FILTER_PHOTO) == YellowAPI.FILTER_PHOTO)
		{
			result += "fto-";
		}
		
		if ((filter & YellowAPI.FILTER_VIDEO) == YellowAPI.FILTER_VIDEO)
		{
			result += "vdo-";
		}
		
		return result.length() > 0 ? result.substring(0, result.length() - 1) : result;
	}
	
	private Object request(String query) throws ClientProtocolException, IOException, JSONException
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(query);
		HttpResponse response = client.execute(get);
		
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			String result = EntityUtils.toString(response.getEntity());
			
			if (result.substring(0, 1).equals("["))
			{
				return new JSONArray(result);
			}
			else if (result.substring(0, 1).equals("{"))
			{
				return new JSONObject(result);
			}
		}
		
		return null;
	}
}
