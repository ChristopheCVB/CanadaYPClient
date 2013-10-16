/*
Copyright © 2011, Yellow Pages Group Co.  All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1)	Redistributions of source code must retain a complete copy of this notice, including the copyright notice, this list of conditions and the following disclaimer; and
2)	Neither the name of the Yellow Pages Group Co., nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT OWNER AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.yellow.api;

import org.json.JSONObject;

/**
 * Yellow API
 * 
 * This API is responsible for calling the web services provided by Yellow
 * Pages. Note that this API does not create threads to call the web service. It
 * is recommended to call the API on a separate thread to avoid blocking the
 * user interface.
 */
public interface YellowAPI
{
	/**
	 * Show all business
	 */
	public static final long FILTER_ALL = 0x000L;
	
	/**
	 * Show only business which name contain the What search term
	 */
	public static final long FILTER_BUS_NAME = 0x00FL;
	
	/**
	 * Show only business that has photo
	 */
	public static final long FILTER_PHOTO = 0x0F0L;
	
	/**
	 * Show only business that has video
	 */
	public static final long FILTER_VIDEO = 0xF00L;
	
	public static final int CONDITION_NEW = 100;
	public static final int CONDITION_USED = 200;
	
	public static final int TYPE_OFFER = 100;
	public static final int TYPE_SEARCH = 200;
	
	public static final int ORDER_DISTANCE = 1;
	public static final int ORDER_DATE = 2;
	public static final int ORDER_PRICE_ASC = 3;
	public static final int ORDER_PRICE_DESC = 4;
	
	/**
	 * Do a what where search using provided information
	 * 
	 * @param what
	 *            : what is searched
	 * @param where
	 *            : where the results should be from
	 * @param page
	 *            : page number
	 * @param pageLength
	 *            : number of results per page
	 * @param filter
	 *            : filter type
	 * 
	 * @return a JSONObject containing the response from the web service
	 * 
	 * @throws a
	 *             YellowException whenever an error occur (mainly network or
	 *             JSON related)
	 */
	JSONObject findBusiness(String what, String where, int page, int pageLength, long filter) throws YellowException;
	
	/**
	 * Do reverse search
	 * 
	 * @param phone
	 *            : phone number
	 * 
	 * @return a JSONObject containing the response from the web service
	 * 
	 * @throws a
	 *             YellowException whenever an error occur (mainly network or
	 *             JSON related)
	 */
	JSONObject findBusinessByPhone(String phone) throws YellowException;
	
	/**
	 * Do a what where search for a specific location using provided information
	 * 
	 * @param what
	 *            : what is searched
	 * @param latitude
	 *            : latitude from where the results should be from
	 * @param longitude
	 *            : longitude from where the results should be from
	 * @param page
	 *            : page number
	 * @param pageLength
	 *            : number of results per page
	 * @param filter
	 *            : filter type
	 * 
	 * @return a JSONObject containing the response from the web service
	 * 
	 * @throws a
	 *             YellowException whenever an error occur (mainly network or
	 *             JSON related)
	 */
	JSONObject findBusinessNear(String what, double latitude, double longitude, int page, int pageLength, long filter) throws YellowException;
	
	/**
	 * Get detailed information about a business
	 * 
	 * @param prov
	 *            : the province code of the merchant
	 * @param city
	 *            : the city name of the merchant
	 * @param busName
	 *            : the name of the merchant
	 * @param listingId
	 *            : the merchant id got from a search result
	 * 
	 * 
	 * @return a JSONObject containing the response from the web service
	 * 
	 * @throws a
	 *             YellowException whenever an error occur (mainly network or
	 *             JSON related)
	 */
	JSONObject getBusinessDetails(String prov, String city, String busName, String listingId) throws YellowException;
	
	/**
	 * Get merchant related to the parent specified (e.g. Dominos pizza's
	 * branches)
	 * 
	 * @param parentId
	 *            : id of the parent merchant
	 * @param page
	 *            : page number
	 * @param pageLength
	 *            : number of result per page
	 * 
	 * @return a JSONObject containing the response from the web service
	 * 
	 * @throws a
	 *             YellowException whenever an error occur (mainly network or
	 *             JSON related)
	 */
	JSONObject findDealers(String parentId, int page, int pageLength) throws YellowException;
}
