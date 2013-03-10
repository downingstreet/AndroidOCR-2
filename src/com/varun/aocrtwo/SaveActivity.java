package com.varun.aocrtwo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SaveActivity extends Activity{
	public static final String PACKAGE_NAME = "com.varun.aocrtwo";
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/AOCR/";
	protected static final String TAG = "SaveActivity.java";
	protected TextView location;
	protected Button saveButton;
	protected EditText saveName;
	public String saveRecText;
	
	
	
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     saveRecText = extras.getString("recText1");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save);
		location = (TextView)findViewById(R.id.location);
		saveButton = (Button)findViewById(R.id.saveButton);
		saveName = (EditText)findViewById(R.id.saveName);
		location.setText(DATA_PATH);
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Log.v(TAG, "saving");
					try {
						saveFileText();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
					}
					
				
			}
		});
		
	}
	
	private void saveFileText() {
		Log.v(TAG, saveRecText);
		
		// TODO Auto-generated method stub
		String fileName = saveName.getText().toString();
		if(fileName.length()!=0){
			File file = new File(DATA_PATH + fileName + ".txt");
			
			try {
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter writer = new OutputStreamWriter(fos);
				
				try {
					writer.append(saveRecText);
					writer.close();
					fos.close();
					Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "Enter a file name.", Toast.LENGTH_SHORT).show();
			
		}
	}
	

}
