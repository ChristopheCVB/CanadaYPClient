/*
Copyright © 2011, Yellow Pages Group Co.  All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1)	Redistributions of source code must retain a complete copy of this notice, including the copyright notice, this list of conditions and the following disclaimer; and
2)	Neither the name of the Yellow Pages Group Co., nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT OWNER AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ccvb.android.yellowapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ccvb.android.yellowapi.model.Address;
import com.squareup.picasso.Picasso;
import com.yellow.api.YellowAPI;
import com.yellow.api.YellowAPIImpl;

public class DetailActivity extends SherlockActivity
{
	private static final String DETAILS = "details";
	private JSONObject details;
	
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setLogo(R.drawable.yellowapi);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle("Detail");
		
		this.setContentView(R.layout.detail_view);
		
		Bundle extras = this.getIntent().getExtras();
		
		if (extras == null)
		{
			return;
		}
		
		// Get the merchant Id from the result listing
		String id = extras.getString("id");
		String prov = extras.getString("prov");
		String city = extras.getString("city");
		String busName = extras.getString("busName");
		
		boolean saved = false;
		if ((savedState != null) && savedState.containsKey(DetailActivity.DETAILS))
		{
			try
			{
				this.details = new JSONObject(savedState.getString(DetailActivity.DETAILS));
				saved = true;
			}
			catch (JSONException exception)
			{
				exception.printStackTrace();
			}
		}
		if (!saved && (id != null))
		{
			// Fetch merchant details async
			new DetailTask(id, prov, city, busName).execute();
		}
		else
		{
			this.fillView();
		}
	}
	
	// Background worker to fetch merchant details
	private class DetailTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog progressDialog;
		private String id;
		private String prov;
		private String city;
		private String busName;
		
		public DetailTask(String id, String prov, String city, String busName)
		{
			this.id = id;
			this.prov = prov;
			this.city = city;
			this.busName = busName;
		}
		
		@Override
		protected void onPreExecute()
		{
			// Show the loading popup
			this.progressDialog = ProgressDialog.show(DetailActivity.this, "", "Loading", true);
		}
		
		@Override
		protected Void doInBackground(Void... arg0)
		{
			try
			{
				YellowAPI api = new YellowAPIImpl("en", Constants.YELLOW_API_KEY, Constants.YELLOW_API_UID, true);
				DetailActivity.this.details = api.getBusinessDetails(this.prov, this.city, this.busName, this.id);
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
			DetailActivity.this.fillView();
			
			// Close the loading popup
			this.progressDialog.dismiss();
		}
	}
	
	private void fillView()
	{
		ImageView logo = (ImageView) DetailActivity.this.findViewById(R.id.businessLogo);
		
		final TextView name = (TextView) DetailActivity.this.findViewById(R.id.businessName);
		TextView address = (TextView) DetailActivity.this.findViewById(R.id.businessAddress);
		
		TextView phone = (TextView) DetailActivity.this.findViewById(R.id.busPhone);
		TextView website = (TextView) DetailActivity.this.findViewById(R.id.busWebsite);
		TextView position = (TextView) DetailActivity.this.findViewById(R.id.busPos);
		
		ImageView ad = (ImageView) DetailActivity.this.findViewById(R.id.busAd);
		
		LinearLayout categoriesHolder = (LinearLayout) DetailActivity.this.findViewById(R.id.busCategories);
		
		try
		{
			// The logo replace the default image if available
			if (!DetailActivity.this.details.isNull("logos"))
			{
				JSONObject logos = DetailActivity.this.details.getJSONObject("logos");
				String url = "";
				
				if (!logos.isNull("EN"))
				{
					url = logos.getString("EN");
				}
				else if (!logos.isNull("FR"))
				{
					url = logos.getString("FR");
				}
				
				if (url.length() > 0)
				{
					Picasso.with(DetailActivity.this).load(url).into(logo);
				}
			}
			
			name.setText(DetailActivity.this.details.getString("name"));
			address.setText(new Address(DetailActivity.this.details.getJSONObject("address")).toString());
			
			// Phone number can be clicked as result of using the autoLink
			// property
			if (!DetailActivity.this.details.isNull("phones"))
			{
				JSONArray phones = DetailActivity.this.details.getJSONArray("phones");
				
				if (phones.length() > 0)
				{
					phone.setText(phones.getJSONObject(0).getString("dispNum"));
				}
			}
			
			if (!DetailActivity.this.details.isNull("categories"))
			{
				JSONArray categories = DetailActivity.this.details.getJSONArray("categories");
				TextView cat;
				
				// We may have many categories so we create the text view as
				// needed
				for (int i = 0; i < categories.length(); ++i)
				{
					cat = new TextView(DetailActivity.this);
					cat.setText(categories.getJSONObject(i).getString("name"));
					cat.setPadding(20, 0, 0, 0);
					cat.setTextSize(12.0f);
					categoriesHolder.addView(cat);
				}
			}
			
			if (!DetailActivity.this.details.isNull("geoCode"))
			{
				JSONObject geoCode = DetailActivity.this.details.getJSONObject("geoCode");
				final String latitude;
				final String longitude;
				
				if (!geoCode.isNull("latitude") && !geoCode.isNull("longitude"))
				{
					latitude = geoCode.getString("latitude");
					longitude = geoCode.getString("longitude");
					
					position.setMovementMethod(LinkMovementMethod.getInstance());
					
					// Start GoogleMap when clicking on the text view
					Spannable spans = (Spannable) position.getText();
					ClickableSpan clickSpan = new ClickableSpan()
					{
						
						@Override
						public void onClick(View widget)
						{
							String uri = "geo:" + latitude + "," + longitude + "?z=15&q=" + latitude + ", " + longitude + " (" + name.getText() + ")";
							DetailActivity.this.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
						}
						
					};
					
					spans.setSpan(clickSpan, 0, spans.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			
			if (!DetailActivity.this.details.isNull("products"))
			{
				JSONObject products = DetailActivity.this.details.getJSONObject("products");
				
				// Web site URL can be clicked as result of using the
				// autoLink property
				if (!products.isNull("webUrl"))
				{
					JSONArray urls = products.getJSONArray("webUrl");
					
					if (urls.length() > 0)
					{
						website.setText(urls.getString(0));
					}
				}
				
				if (!products.isNull("dispAd"))
				{
					JSONArray urls = products.getJSONArray("dispAd");
					
					if (urls.length() > 0)
					{
						String url = urls.getJSONObject(0).getString("url");
						
						if (url.length() > 0)
						{
							// The image is fetched by the BitmapFactory
							Picasso.with(DetailActivity.this).load(url).into(ad);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			Log.e("MerchantDetailError", "Error in the merchant detail json", e);
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
		outState.putString(DetailActivity.DETAILS, this.details.toString());
		
		super.onSaveInstanceState(outState);
	}
}