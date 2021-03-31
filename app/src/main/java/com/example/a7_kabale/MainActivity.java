package com.example.a7_kabale;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final int CAMERA_ACCESS_ALLOWED = 101;
    private static final String TAG = "LOG";
    private CameraBridgeViewBase mOpenCvCameraView;

    Mat imageSrc, imageTrans, dst;

//    static {
//        // OpenCV Initialization
//        if (!OpenCVLoader.initDebug())
//            Log.e("OpenCv", "Unable to load OpenCV");
//        else {
//            Log.d("OpenCv", "OpenCV loaded");
//        }
//    }

    /* Recommended Initialization of OpenCV on Android. */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully!");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // OpenCV manager initialization
        OpenCVLoader.initDebug();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG", "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = findViewById(R.id.HelloOpenCvView);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Granted");
            initializeCamera(mOpenCvCameraView);
        } else {
            Log.d(TAG, "onCreate: Permission Prompt");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_ACCESS_ALLOWED);
        }
    }

    private void initializeCamera(CameraBridgeViewBase cameraView){
        cameraView.setCameraPermissionGranted();
        cameraView.disableFpsMeter();
        cameraView.setCameraIndex(-1);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);
        cameraView.enableView();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        imageSrc = new Mat();
        dst = new Mat();
    }

    public void onCameraViewStopped() {
        dst.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // https://stackoverflow.com/questions/64488713/opencv-application-android-camera-crashes-after-10-seconds
        // Fixes orientation of the camera of openCV
        imageSrc = inputFrame.rgba();
        imageTrans = imageSrc.t();
        Core.flip(imageTrans, imageTrans, 1);
        Imgproc.resize(imageTrans, dst, imageSrc.size());
        imageSrc.release();
        imageTrans.release();
        return dst;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Ensure that this result is for the camera permission request
        if (requestCode == CAMERA_ACCESS_ALLOWED) {
            // Check if the request was granted or denied
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The request was granted -> tell the camera view
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                mOpenCvCameraView.setCameraPermissionGranted();
            } else {
                // The request was denied -> tell the user and exit the application
                Toast.makeText(this, "Camera permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}