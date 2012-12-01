package com.anyway;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements SurfaceHolder.Callback {

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	private Button mButton01;
	ProgressDialog m_pDialog;
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
		setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mButton01 = (Button) findViewById(R.id.button1);
		mButton01.setOnClickListener(new Button01Listener());
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	class Button01Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			camera.takePicture(myShutterCallback, myPictureCallback_RAW,
					myPictureCallback_JPG);

			m_count = 0;
			isCancel = false;
			m_pDialog = new ProgressDialog(Main.this);
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

			dialog = new AlertDialog.Builder(Main.this)
					.setTitle("")
					.setMessage("Oops, Cannot find it. Do you like Submit it?")
					.setPositiveButton("Submit",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.setClass(Main.this, MSubmit.class);
									Main.this.startActivity(intent);
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
								intent.setClass(Main.this, Information.class);
								Main.this.startActivity(intent);
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

		}
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
		camera.setDisplayOrientation(90);
		Camera.Parameters p = camera.getParameters();
		p.set("jpeg-quality", 100);
		p.setRotation(90);
		p.setPictureFormat(PixelFormat.JPEG);
		p.setPreviewSize(640, 480);
		p.setFocusMode("auto");
		camera.setParameters(p);

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