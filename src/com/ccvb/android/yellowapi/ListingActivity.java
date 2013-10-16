/*
Copyright © 2011, Yellow Pages Group Co.  All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1)	Redistributions of source code must retain a complete copy of this notice, including the copyright notice, this list of conditions and the following disclaimer; and
2)	Neither the name of the Yellow Pages Group Co., nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT OWNER AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ccvb.android.yellowapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ccvb.android.yellowapi.model.Listing;
import com.yellow.api.YellowAPI;
import com.yellow.api.YellowAPIImpl;

public class ListingActivity extends SherlockListActivity
{
	private static final String LISTINGS = "listings";
	
	private ArrayList<Listing> merchants = new ArrayList<Listing>();
	private TextView noListing;
	
	public BaseAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setLogo(R.drawable.yellowapi);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle("Listing");
		
		this.setContentView(R.layout.listing_view);
		
		this.adapter = new MerchantListAdapter(ListingActivity.this, R.layout.listing_item, ListingActivity.this.merchants);
		// Update the list with fetched results
		this.setListAdapter(this.adapter);
		
		Bundle extras = this.getIntent().getExtras();
		this.noListing = (TextView) this.findViewById(R.id.noListing);
		this.noListing.setVisibility(View.GONE);
		
		boolean saved = false;
		if ((savedState != null) && savedState.containsKey(ListingActivity.LISTINGS))
		{
			this.merchants = savedState.getParcelableArrayList(ListingActivity.LISTINGS);
			saved = true;
		}
		
		if (saved)
		{
			this.adapter.notifyDataSetChanged();
		}
		else
		{
			if (extras == null)
			{
				return;
			}
			
			// Get the field value from the search activity
			String what = extras.getString("what");
			String where = extras.getString("where");
			
			if ((what != null) && (where != null))
			{
				// Fetch search results async
				new ListingTask(what, where).execute();
			}
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		
		Listing item = this.merchants.get(position);
		
		Intent myIntent = new Intent(ListingActivity.this, DetailActivity.class);
		
		// Pass the id of the clicked merchant
		myIntent.putExtra("id", item.getId());
		myIntent.putExtra("prov", item.getAddress().getProvince());
		myIntent.putExtra("city", item.getAddress().getCity());
		myIntent.putExtra("busName", item.getName());
		
		this.startActivity(myIntent);
	}
	
	// Background worker to fetch results listing
	private class ListingTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog progressDialog;
		private String what;
		private String where;
		
		public ListingTask(String what, String where)
		{
			this.what = what;
			this.where = where;
		}
		
		@Override
		protected void onPreExecute()
		{
			// Show the loading popup
			this.progressDialog = ProgressDialog.show(ListingActivity.this, "", "Loading", true);
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				YellowAPI api = new YellowAPIImpl("en", Constants.YELLOW_API_KEY, Constants.YELLOW_API_UID, true);
				JSONObject response = api.findBusiness(this.what, this.where, 1, 40, 0);
				
				// Make sure the response contains results
				if ((response != null) && !response.isNull("listings"))
				{
					JSONArray listings = response.getJSONArray("listings");
					
					for (int i = 0; i < listings.length(); ++i)
					{
						ListingActivity.this.merchants.add(new Listing(listings.getJSONObject(i)));
					}
				}
			}
			catch (Exception e)
			{
				Log.e("MerchantFeedError", "Error loading JSON", e);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			// Close the loading popup
			this.progressDialog.dismiss();
			
			if (ListingActivity.this.merchants.size() == 0)
			{
				ListingActivity.this.noListing.setVisibility(View.VISIBLE);
			}
			ListingActivity.this.adapter.notifyDataSetChanged();
		}
	}
	
	// Adapter responsible of showing each row of the listing
	private class MerchantListAdapter extends BaseAdapter
	{
		public MerchantListAdapter(Context context, int textViewResourceId, List<Listing> items)
		{
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// Make sure we have a row model to fill
			if (convertView == null)
			{
				LayoutInflater vi = (LayoutInflater) ListingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.listing_item, null);
			}
			
			Listing item = this.getItem(position);
			TextView name = (TextView) convertView.findViewById(R.id.businessName);
			TextView address = (TextView) convertView.findViewById(R.id.businessAddress);
			TextView distance = (TextView) convertView.findViewById(R.id.businessDistance);
			
			// Fill the row model with merchant informations
			name.setText(item.getName());
			address.setText(item.getAddress().toString());
			distance.setText(item.getDistance() + "m");
			
			return convertView;
		}
		
		@Override
		public int getCount()
		{
			return ListingActivity.this.merchants.size();
		}
		
		@Override
		public Listing getItem(int position)
		{
			return ListingActivity.this.merchants.get(position);
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList(ListingActivity.LISTINGS, this.merchants);
		super.onSaveInstanceState(outState);
	}
}