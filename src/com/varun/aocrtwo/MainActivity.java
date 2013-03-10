package com.varun.aocrtwo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;


public class MainActivity extends Activity implements OnClickListener {
	public static final String PACKAGE_NAME = "com.varun.aocrtwo";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/AOCR/";
	public String recText = "hello";

	public static final String lang = "eng";

	private static final String TAG = "MainActivity.java";

	protected Button takePhoto;
	protected Button saveText;
	protected Button translate;
	protected Button search;
	protected ImageView _image;
	protected EditText _field;
	protected String _path;
	protected boolean _taken;

	protected static final String PHOTO_TAKEN = "photo_taken";

	public void setRecText(String recText) {
		this.recText = recText;
	}

	public String getRecText() {
		return recText;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/eng.traineddata");

				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/eng.traineddata");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		_image = (ImageView) findViewById(R.id.imagePreview);
		_field = (EditText) findViewById(R.id.textArea);
		takePhoto = (Button) findViewById(R.id.shutterButton);
		saveText = (Button) findViewById(R.id.save);
		translate = (Button) findViewById(R.id.translate);
		search = (Button) findViewById(R.id.search);

		takePhoto.setOnClickListener(this);
		saveText.setOnClickListener(this);
		translate.setOnClickListener(this);
		search.setOnClickListener(this);

		_path = DATA_PATH + "/ocr.jpg";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.shutterButton:
			Log.v(TAG, "Starting Camera app switch case");
			startCameraActivity();
			break;
		case R.id.save:
			Log.v(TAG, "Starting SaveActivity");
			Intent saveIntent = new Intent(getApplicationContext(),
					SaveActivity.class);
			saveIntent.putExtra("recText1", recText);
			startActivity(saveIntent);
			break;

		case R.id.translate:
			Log.v(TAG, "Starting to save");
			Intent translateIntent = new Intent(getApplicationContext(),
					TranslateActivity.class);
			translateIntent.putExtra("recText1", recText);
			startActivity(translateIntent);
			break;

		case R.id.search:
			Log.v(TAG, "Starting to search");
			Intent searchIntent = new Intent(getApplicationContext(),
					SearchActivity.class);
			searchIntent.putExtra("recText1", recText);
			startActivity(searchIntent);
			break;

		}

	}

	/*
	 * public class ButtonClickHandler implements View.OnClickListener { public
	 * void onClick(View view) { Log.v(TAG, "Starting Camera app");
	 * startCameraActivity(); } }
	 */

	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	public void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		_image.setImageBitmap(bitmap);

		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if (lang.equalsIgnoreCase("eng")) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}

		recognizedText = recognizedText.trim();

		if (recognizedText.length() != 0) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText
					: _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());

			recText = recognizedText;
		}

		Log.v(TAG, recText);
		// Cycle done.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.pref_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {

		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;

		case R.id.preferences:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

}