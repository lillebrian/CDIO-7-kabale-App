package com.example.a7_kabale.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View.OnTouchListener;

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
import java.util.LinkedList;
import java.util.List;

/**@Author Mikkel Johansen, s175194 */
public class ScannerActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    Context whoAmI = this;
    //defines list to contains scannedCards, their confidentiality levels and a list over all the
    // card names ordered in the same way as the class id's
    ArrayList<String> scannedCards = new ArrayList<>();
    ArrayList<Float> scannedCardsConf = new ArrayList<>();
    List<ImageView> cardViews = new ArrayList<>();
    List<String> cardNames = Arrays.asList("1h","13h","12h","11h","10h","9h","8h","7h","6h","5h","4h",
            "3h","2h","1d","13d","12d","11d","10d","9d","8d","7d","6d","5d","4d","3d","2d",
            "1c","13c","12c","11c","10c","9c","8c","7c","6c","5c","4c","3c","2c","1s","13s",
            "12s","11s","10s","9s","8s","7s","6s","5s","4s","3s","2s");


    //Items in the activity
    Button expandCV;

    //linked list to act as queue for which cards that still needs scanning
    LinkedList<Integer> queue = new LinkedList<>();

    //int value to define how many cards there should be scanned
    int cardsToScan = 7;
    Boolean isExpanded = false;

    //classes used for OpenCV
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    //Boolean values used for running object detection is to be replaced
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;


    public void Yolo(View button){
        startYolo = !startYolo;
        if (!firstTimeYolo) {
//            String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg" ;
//            String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";
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

        LinearLayout cardHolder = findViewById(R.id.cardViewer);
        ImageView expandCV = findViewById(R.id.expandCV);
        Button yoloBtn = findViewById(R.id.Edge_Detection);
        ConstraintLayout constraintLayout = findViewById(R.id.scannerConstraint);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        int cardsToScan = getIntent().getIntExtra("amount", 0);
//        RelativeLayout cardHolder = findViewById(R.id.cardViewer);

        //request for camera permission
        if(ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            //directly ask for the permissions
            ActivityCompat.requestPermissions(ScannerActivity.this,new  String[]{
                    Manifest.permission.CAMERA
            },100);
        }



        for (int i = 0; i < cardsToScan; i++) {
            ImageView image = new ImageView(getApplicationContext());
            image.setContentDescription(String.valueOf(i));
            if (i == 0)
                image.setImageDrawable(getDrawable(R.drawable.cards_back_red));
            else
                image.setImageDrawable(getDrawable(R.drawable.cards_back));
            image.setAdjustViewBounds(true);
            image.setClickable(true);
            image.setMaxHeight(500);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(String.valueOf(v.getContentDescription()));
                    if (queue.contains(index))
                        return;
                    ((ImageView) v).setImageResource(R.drawable.cards_back_red);
                    cardViews.get(queue.peekFirst()).setImageResource(R.drawable.cards_back);
                    scannedCards.set(index, "");
                    queue.addFirst(index);
                }
            });
//          Initializes all the different list's that need to contain data
            cardViews.add(image);
            cardHolder.addView(image);
            queue.add(i);
            scannedCards.add("");
            //Uncomment if you wanna check the confidentiality things get through with
            scannedCardsConf.add(0.0f);

        }

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
        expandCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isExpanded) {

                    cardHolder.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

                    constraintSet.connect(R.id.expandCV, ConstraintSet.BOTTOM, R.id.scannerConstraint, ConstraintSet.BOTTOM, 0);
                    constraintSet.clear(R.id.expandCV, ConstraintSet.TOP);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) expandCV.getLayoutParams();
                    params.setMargins(0,0,0,0);
                    yoloBtn.setVisibility(View.INVISIBLE);
                    yoloBtn.setVisibility(View.GONE);

                    expandCV.setBackgroundResource(R.drawable.up_arrow);

                    constraintSet.applyTo(constraintLayout);
                    isExpanded = true;
                } else {
                    final float scale = (getResources().getDisplayMetrics().density);

                    cardHolder.getLayoutParams().height = (int) (60 * scale + 0.5f);

                    constraintSet.clear(R.id.expandCV, ConstraintSet.BOTTOM);
                    constraintSet.connect(R.id.expandCV, ConstraintSet.TOP, R.id.scannerConstraint, ConstraintSet.TOP, (int)(43 * scale + 0.5f));
                    yoloBtn.setVisibility(View.VISIBLE);

                    expandCV.setBackgroundResource(R.drawable.down_arrow);

                    constraintSet.applyTo(constraintLayout);
                    isExpanded = false;
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        //#TODO replace this with getFirst on the queue to check if empty
        if (startYolo) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

            //#TODO check which one seems best (Size)
//            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0), false, false);
            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(608, 608), new Scalar(0, 0, 0), false, false);

            tinyYolo.setInput(imageBlob);

            java.util.List<Mat> result = new java.util.ArrayList<Mat>(2);
            List<String> outBlobNames = new java.util.ArrayList<>();

            outBlobNames.add(0, "yolo_16");
            outBlobNames.add(1, "yolo_23");

            tinyYolo.forward(result, outBlobNames);

            //defines what confidentiality lvl is required for the software to allow recognition
            float confThreshold = 0.9f;

            //defines 3 lists 1 for containing classid's, another to contain confenditiality levels
            //the last to contain the dimensions for the box to be drawn around the the area
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
                float nmsThresh = 0.6f;

                MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
                Rect2d[] boxesArray = rects.toArray(new Rect2d[0]);
                MatOfRect2d boxes = new MatOfRect2d(boxesArray);
                MatOfInt indices = new MatOfInt();

                //performs NMS(Non-Maximum) on the boxes
                Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);

                //Draws the result boxes
                int[] ind = indices.toArray();
                for (int i = 0; i < ind.length; ++i) {
                    int index = ind[i];
                    //gets the measurements of the recognized area
                    Rect2d box = boxesArray[index];
                    //gets the names of the recognized card from it's class id.
                    String name = cardNames.get(clsIds.get(index));
                    //gets the confidentiality lvl of the recognized class/card
                    float conf = confs.get(index);
                    //converts the float value of the confidentiality to an integer
                    int intConf = (int) (conf * 100);
                    //Draws the name of the card around the recognized area's
                    Imgproc.putText(frame,name + " " + intConf + "%",box.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0,0,255),2);
                    //Draws an rectangle around the recognized area
                    Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                    //check if the recognized card is a already recognized card
                    if (!scannedCards.contains(name)) {
                        //removes the index from the queue
                        int queueIndex = queue.poll();
                        //inserts the card name into the list of scanned cards
                        scannedCards.set(queueIndex, name);
                        //can be used for testing to see how high fails get through in confidentiality
                        scannedCardsConf.set(queueIndex, conf);
                        if (queue.peekFirst() == null) {
//                            finishUp();
                            Intent returnIntent = new Intent();
                            returnIntent.putStringArrayListExtra("result", new ArrayList<>(scannedCards));
                            setResult(this.RESULT_OK, returnIntent);
                            finish();
                        }
                        //updates the card on the ui
                        else
                            updateCard(name, queueIndex);
                    }
                    //for testing purposes
                    System.out.println("queue: " + queue.toString());
                    System.out.println("cards: " + scannedCards.toString());
                    System.out.println("Conf: " + scannedCardsConf.toString());
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

    //Method that updates the picture associated with the top cards
    private void updateCard(String name, int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardViews.get(index).setImageResource(getResources().getIdentifier("cards_"+name.toLowerCase(), "drawable", getPackageName()));
                cardViews.get(queue.peekFirst()).setImageResource(R.drawable.cards_back_red);
            }
        });

    }

    //method that can load in assets files.
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

    private void finishUp() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", new ArrayList<>(scannedCards));
        setResult(this.RESULT_OK, returnIntent);
        finish();
    }

}