package com.varun.aocrtwo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SearchActivity extends Activity {
	public String searchRecText;
	public String selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     searchRecText = extras.getString("recText1");
		}
		else{
			searchRecText = "hello";
		}
		
		
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		selected = SP.getString("searchType", "1");
		
		super.onCreate(savedInstanceState);
			if(selected.equals("2")){
			String url = "http://dictionary.reference.com/browse/" + searchRecText + "?s=t";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			}
			else{
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, searchRecText);
			startActivity(intent);
			}
	}
	
	

}
