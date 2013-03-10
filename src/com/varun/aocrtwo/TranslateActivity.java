package com.varun.aocrtwo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;


public class TranslateActivity extends Activity {
	public String translateRecText;
	public String selected;
	public String lang;
	/*protected TextView original;
	protected TextView translated;*/

	protected void onCreate(android.os.Bundle savedInstanceState) {
	
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     translateRecText = extras.getString("recText1");
		}
		else{
			translateRecText = "hello";
		}
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		selected = SP.getString("translateType", "1");
		
		super.onCreate(savedInstanceState);
		
		if(selected.equals("1")){
			lang = "de";
		}
		if(selected.equals("2")){
			lang = "fr";
		}
		if(selected.equals("3")){
			lang = "hi";
		}
		if(selected.equals("4")){
			lang = "it";
		}
		
		String url = "http://translate.reference.com/translate?query=" + translateRecText + "&src=en&dst=" + lang; 
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
		
		
		
		/*setContentView(R.layout.translate);*/
		
		
		/*original = (TextView)findViewById(R.id.translate);
		translated = (TextView)findViewById(R.id.translated);
		Locale curLocale = this.getResources().getConfiguration().locale;
	I18nTranslator i18nTranslator = new I18nTranslator("SPANISH");
	String text = i18nTranslator.translateString(translateRecText);
	translated.setText(text);*/
	};
}