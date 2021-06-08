package com.example.a7_kabale.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.example.a7_kabale.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.*;
import org.opencv.utils.Converters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.round;
import static org.opencv.core.Core.FILLED;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;


public class ScannerActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;

    public void Yolo(View button){
        startYolo = !startYolo;
        if (!firstTimeYolo) {
//            String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg" ;
//            String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";

//              String tinyYoloCfg = getAssetsFile("yolov3.cfg", this);
//              String tinyYoloWeights = getAssetsFile("yolov3.weights", this);
            String tinyYoloCfg = getAssetsFile("yolov3-tiny-obj-test.cfg", this);
            String tinyYoloWeights = getAssetsFile("yolov3-tiny-obj_10000.weights", this);
            tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
            tinyYolo.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
            tinyYolo.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.cameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.setCameraPermissionGranted();
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        if (startYolo) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);
            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0), false, false);
//            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(608, 608), new Scalar(0, 0, 0), false, false);

            tinyYolo.setInput(imageBlob);

            java.util.List<Mat> result = new java.util.ArrayList<Mat>(2);
            List<String> outBlobNames = new java.util.ArrayList<>();

            outBlobNames.add(0, "yolo_16");
            outBlobNames.add(1, "yolo_23");

            tinyYolo.forward(result, outBlobNames);

            float confThreshold = 0.3f;

            List<Integer> clsIds = new ArrayList<>();
            List<Float> confs = new ArrayList<>();
            List<Rect2d> rects = new ArrayList<>();

            for (int i = 0; i < result.size(); ++i){
                Mat level = result.get(i);
                for (int j = 0; j < level.rows(); ++j){
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);

                    float confidence = (float)mm.maxVal;

                    Point classIdPoint = mm.maxLoc;

                    if (confidence > confThreshold) {
                        int centerX = (int) (row.get(0,0)[0] * frame.cols());
                        int centerY = (int) (row.get(0,1)[0] * frame.rows());
                        int width = (int) (row.get(0,2)[0] * frame.cols());
                        int height = (int) (row.get(0,3)[0] * frame.rows());

                        int left = centerX - width / 2;
                        int top = centerY - height / 2;

                        clsIds.add((int)classIdPoint.x);
                        confs.add(confidence);
                        rects.add(new Rect2d(left, top, width, height));
                    }
                }
            }
            int ArrayLength = confs.size();

            if (ArrayLength >=1 ) {
                float nmsThresh = 0.2f;

                MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
                Rect2d[] boxesArray = rects.toArray(new Rect2d[0]);
//                MatOfRect2d boxes = new MatOfRect2d();
//                boxes.fromList(boxesArray);
                MatOfRect2d boxes = new MatOfRect2d(boxesArray);
                MatOfInt indices = new MatOfInt();
                Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);

                //Draw result boxes
                int[] ind = indices.toArray();
                for (int i = 0; i < ind.length; ++i) {
                    int idx = ind[i];
                    Rect2d box = boxesArray[idx];
                    int idGuy = clsIds.get(idx);
                    float conf = confs.get(idx);


                    List<String> cardNames = Arrays.asList("Ah","Kh","Qh","Jh","10h","9h","8h","7h","6h","5h","4h",
                            "3h","2h","Ad","Kd","Qd","Jd","10d","9d","8d","7d","6d","5d","4d","3d","2d",
                            "Ac","Kc","Qc","Jc","10c","9c","8c","7c","6c","5c","4c","3c","2c","As","Ks",
                            "Qs","Js","10s","9s","8s","7s","6s","5s","4s","3s","2s");
                    List<String> cocoNames = Arrays.asList("a person", "a bicycle", "a motorbike", "an airplane", "a bus", "a train", "a truck", "a boat", "a traffic light", "a fire hydrant", "a stop sign", "a parking meter", "a car", "a bench", "a bird", "a cat", "a dog", "a horse", "a sheep", "a cow", "an elephant", "a bear", "a zebra", "a giraffe", "a backpack", "an umbrella", "a handbag", "a tie", "a suitcase", "a frisbee", "skis", "a snowboard", "a sports ball", "a kite", "a baseball bat", "a baseball glove", "a skateboard", "a surfboard", "a tennis racket", "a bottle", "a wine glass", "a cup", "a fork", "a knife", "a spoon", "a bowl", "a banana", "an apple", "a sandwich", "an orange", "broccoli", "a carrot", "a hot dog", "a pizza", "a doughnut", "a cake", "a chair", "a sofa", "a potted plant", "a bed", "a dining table", "a toilet", "a TV monitor", "a laptop", "a computer mouse", "a remote control", "a keyboard", "a cell phone", "a microwave", "an oven", "a toaster", "a sink", "a refrigerator", "a book", "a clock", "a vase", "a pair of scissors", "a teddy bear", "a hair drier", "a toothbrush");

                    int intConf = (int) (conf * 100);

//                    Imgproc.putText(frame,cocoNames.get(idGuy) + " " + intConf + "%",box.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255,255,0),2);
//                    Imgproc.putText(frame,cardNames.get(idGuy) + " " + intConf + "%",box.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255,255,0),2);
                    Imgproc.putText(frame,cardNames.get(idGuy) + " " + intConf + "%",box.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0,0,255),2);
                    Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                }
            }

        }


        return frame;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    private static String getAssetsFile(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }
}