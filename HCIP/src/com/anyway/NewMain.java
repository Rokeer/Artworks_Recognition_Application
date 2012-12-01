package com.anyway;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class NewMain extends Activity implements SurfaceHolder.Callback {

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	ProgressDialog m_pDialog;
	ImageView img;
	int m_count = 0;
	Dialog dialog;
	boolean isCancel = false;

	// Ä£ÄâÓÃ
	boolean isRec = true;
	final Handler mPHandler = new Handler();
	final Runnable showNotice = new Runnable() {
		public void run() {
			updateNotice();
		}
	};

	private void updateNotice() {
		// update detail
		dialog.show();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.newmain);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.ctrl, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);
		img = (ImageView) findViewById(R.id.imageView1);

		img.setOnTouchListener(new OnTouchListener() {
			private int mx, my;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					mx = (int) (event.getRawX());
					my = (int) (event.getRawY() - 50);

					v.layout(mx - img.getWidth() / 2, my - img.getHeight() / 2,
							mx + img.getWidth() / 2, my + img.getHeight() / 2);
					break;
				}
				return true;
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
				&& event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.isTracking()
				&& !event.isCanceled()) {
			// *** DO ACTION HERE ***
			camera.takePicture(myShutterCallback, myPictureCallback_RAW,
					myPictureCallback_JPG);

			m_count = 0;
			isCancel = false;
			m_pDialog = new ProgressDialog(NewMain.this);
			m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_pDialog.setTitle("Recognizing");
			m_pDialog
					.setMessage("Recognizing this Atrwork. Wait a second, please...");
			// m_pDialog.setIcon();
			m_pDialog.setIndeterminate(false);
			m_pDialog.setCancelable(true);
			m_pDialog.setButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							isCancel = true;

						}
					});

			m_pDialog.show();

			dialog = new AlertDialog.Builder(NewMain.this)
					.setTitle("")
					.setMessage("Oops, Cannot find it. Do you like Submit it?")
					.setPositiveButton("Submit",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.setClass(NewMain.this, MSubmit.class);
									NewMain.this.startActivity(intent);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							}).create();

			new Thread() {
				public void run() {
					try {
						while (m_count <= 100) {
							m_count++;
							Thread.sleep(30);
						}
						m_pDialog.cancel();
						if (!isCancel) {
							if (isRec) {
								Intent intent = new Intent();
								intent.setClass(NewMain.this, Information.class);
								NewMain.this.startActivity(intent);
								isRec = false;
							} else {
								mPHandler.post(showNotice);

								isRec = true;
							}
						}
					} catch (InterruptedException e) {
						m_pDialog.cancel();
					}
				}
			}.start();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	ShutterCallback myShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub

		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub

		}
	};

	PictureCallback myPictureCallback_JPG = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
			Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0,
					arg0.length);
			Uri uriTarget = getContentResolver().insert(
					Media.EXTERNAL_CONTENT_URI, new ContentValues());

			OutputStream imageFileOS;
			try {
				imageFileOS = getContentResolver().openOutputStream(uriTarget);
				imageFileOS.write(arg0);
				imageFileOS.flush();
				imageFileOS.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			camera.startPreview();
		}
	};

	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// float location[] = new float[2];
	// location[0] = ev.getY();
	// location[1] = ev.getX();
	// LayoutInflater controlInflaterforsquare =
	// LayoutInflater.from(getBaseContext());
	// View viewControl = controlInflaterforsquare.inflate(R.layout.ctrl, null);
	// TextView tv = (TextView) findViewById(R.id.textView1);
	// LayoutParams lp = tv.get
	// tv.setLayoutParams(x);
	// LayoutParams layoutParamsControlforSquare
	// = new LayoutParams(LayoutParams.FILL_PARENT,
	// LayoutParams.FILL_PARENT);
	// return true;

	// }
	// @Override
	protected void onDraw(Canvas canvas) {
		Paint rectanglePaint = new Paint();
		rectanglePaint.setARGB(255, 200, 0, 0);
		rectanglePaint.setStyle(Paint.Style.FILL);
		rectanglePaint.setStrokeWidth(2);
		canvas.drawRect(new Rect(10, 10, 200, 200), rectanglePaint);
		// Log.w(this.getClass().getName(), "On Draw Called");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}
}