package com.example.a7_kabale.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
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


public class ScannerActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {
    private static final String TAG = "MainActivity";
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;

    private CameraBridgeViewBase mOpenCvCameraView;
//    private ArrayList<String>    classes = new ArrayList<String>();
    List<String> classes = Arrays.asList("a person", "a bicycle", "a motorbike", "an airplane", "a bus", "a train", "a truck", "a boat", "a traffic light", "a fire hydrant", "a stop sign", "a parking meter", "a car", "a bench", "a bird", "a cat", "a dog", "a horse", "a sheep", "a cow", "an elephant", "a bear", "a zebra", "a giraffe", "a backpack", "an umbrella", "a handbag", "a tie", "a suitcase", "a frisbee", "skis", "a snowboard", "a sports ball", "a kite", "a baseball bat", "a baseball glove", "a skateboard", "a surfboard", "a tennis racket", "a bottle", "a wine glass", "a cup", "a fork", "a knife", "a spoon", "a bowl", "a banana", "an apple", "a sandwich", "an orange", "broccoli", "a carrot", "a hot dog", "a pizza", "a doughnut", "a cake", "a chair", "a sofa", "a potted plant", "a bed", "a dining table", "a toilet", "a TV monitor", "a laptop", "a computer mouse", "a remote control", "a keyboard", "a cell phone", "a microwave", "an oven", "a toaster", "a sink", "a refrigerator", "a book", "a clock", "a vase", "a pair of scissors", "a teddy bear", "a hair drier", "a toothbrush");


    String classesFile = "coco.names";
//    String modelConfiguration = "/yolov3.cfg";
//    String modelWeights = "/yolov3.weights";
    float confThreshold = 0.5f;
    float nmsThreshold = 0.4f;
    int inpWidth = 608;
    int inpHeight = 608;
    Mat frame;
    Net net;

    public void Yolo(View button){
        startYolo = !startYolo;
        if (!firstTimeYolo) {
//            String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg" ;
//            String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";
//            String tinyYoloCfg = getAssetsFile("yolov3.cfg", this);
//            String tinyYoloWeights = getAssetsFile("yolov3.weights", this);
            String tinyYoloCfg = getAssetsFile("yolov3-tiny-obj-test.cfg", this);
            String tinyYoloWeights = getAssetsFile("yolov3-tiny-obj_best.weights", this);
            net = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
            net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
            net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.cameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.enableFpsMeter();


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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (startYolo) {
            frame = inputFrame.rgba();
            Mat dst = new Mat();
            Imgproc.cvtColor(frame, dst, Imgproc.COLOR_BGRA2BGR);
            Mat blob = Dnn.blobFromImage(dst, 1/255.0, new Size(inpWidth, inpHeight), new Scalar(0,0,0), true, false);
            net.setInput(blob);
            List<Mat> outs = new ArrayList<Mat>();
            net.forward(outs, getOutputsNames(net));
            postprocess(frame, outs);
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

    List<String> getOutputsNames(Net net)
    {
        ArrayList<String> names = new ArrayList<String>();
        if (names.size() == 0)
        {
            //Get the indices of the output layers, i.e. the layers with unconnected outputs
            List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
            //get the names of all the layers in the network
            List<String> layersNames = net.getLayerNames();

            // Get the names of the output layers in names
            for (int i = 0; i < outLayers.size(); ++i) {
                String layer = layersNames.get(outLayers.get(i).intValue()-1);
                names.add(layer);
            }
        }
        return names;
    }

    private void drawPred(int classId, float conf, int left, int top, int right, int bottom, Mat frame)
    {
        //Draw a rectangle displaying the bounding box
        rectangle(frame, new Point(left, top), new Point(right, bottom), new Scalar(255, 178, 50), 3);

        //Get the label for the class name and its confidence
        String label = String.format("%.2f", conf);
        if (classes.size() > 0)
        {
            label = classes.get(classId) + ":" + label;
            System.out.println(label);
        }

        //Display the label at the top of the bounding box
        int[] baseLine = new int[1];
        Size labelSize = Imgproc.getTextSize(label, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
        top = java.lang.Math.max(top, (int)labelSize.height);
        rectangle(frame, new Point(left, top - round(1.5*labelSize.height)),
                new Point(left + round(1.5*labelSize.width), top + baseLine[0]), new Scalar(255, 255, 255), FILLED);
        putText(frame, label, new Point(left, top), Imgproc.FONT_HERSHEY_SIMPLEX, 0.75, new Scalar(0,0,0),1);
    }

    private float get_iou(Rect bb1, Rect bb2){
        int x_left = java.lang.Math.max(bb1.x, bb2.x);
        int y_top = java.lang.Math.max(bb1.y, bb2.y);
        int x_right = java.lang.Math.min(bb1.x + bb1.height, bb2.x + bb2.height);
        int y_bottom = java.lang.Math.min(bb1.y + bb1.width, bb2.y + bb2.width);
        if( x_right < x_left || y_bottom < y_top){
            return 0.0f;
        }
        int intersection_area = (x_right - x_left) * (y_bottom - y_top);
        int bb1_area = bb1.width * bb1.height;
        int bb2_area = bb2.width * bb2.height;

        float iou = intersection_area / (float)(bb1_area + bb2_area - intersection_area);

        return iou;
    }

    void postprocess(Mat frame, List<Mat> outs)
    {
        List<Integer> classIds = new ArrayList<Integer>();
        List<Float> confidences = new ArrayList<Float>();
        List<Rect2d> boxes = new ArrayList<Rect2d>();
        //List<Integer> idxs = new ArrayList<Integer>();
        List<Float> objconf = new ArrayList<Float>();

        for (int i = 0; i < outs.size(); ++i)
        {
            int cols = 0;
            for (int j = 0; j < outs.get(i).rows(); ++j)
            {
                Mat scores = outs.get(i).row(j).colRange(5, outs.get(i).row(j).cols());
                Point classIdPoint;
                double confidence;
                Core.MinMaxLocResult r = Core.minMaxLoc(scores);
                if (r.maxVal > confThreshold)
                {
                    Mat bb = outs.get(i).row(j).colRange(0, 5);
                    float[] data = new float[1];
                    bb.get(0, 0, data);

                    int centerX = (int)(data[0] * frame.cols());

                    bb.get(0, 1, data);

                    int centerY = (int)(data[0] * frame.rows());

                    bb.get(0, 2, data);

                    int width = (int)(data[0] * frame.cols());

                    bb.get(0, 3, data);

                    int height = (int)(data[0] * frame.rows());

                    int left = centerX - width / 2;
                    int top = centerY - height / 2;

                    bb.get(0, 4, data);
                    objconf.add(data[0]);

                    confidences.add((float)r.maxVal);
                    classIds.add((int)r.maxLoc.x);
                    boxes.add(new Rect2d(left, top, width, height));
                }
            }
        }
/*        int classesIdsSize = classIds.size();
        for (int i = 0; i < classesIdsSize; i++){
            int idx = classIds.get(0);
            if(idx != -1) {
                for (int j = i; j < classesIdsSize; j++) {
                    if (idx == classIds.get(j)) {
                        if(get_iou(boxes.get(i), boxes.get(j)) >= nmsThreshold){
                            if(objconf.get(i) > objconf.get(j)){
                                classIds.set(j, -1);
                            } else {
                                classIds.set(i, -1);
                                idx = classIds.get(j);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < classesIdsSize; ++i)
        {
            int idx = classIds.get(i);
            if(idx != -1) {
                Rect box = boxes.get(i);
                drawPred(classIds.get(i), confidences.get(i), box.x, box.y,
                        box.x + box.width, box.y + box.height, frame);
            }
        }*/

        MatOfRect2d boxs =  new MatOfRect2d();
        boxs.fromList(boxes);
        MatOfFloat confis = new MatOfFloat();
        confis.fromList(objconf);
        MatOfInt idxs = new MatOfInt();
        Dnn.NMSBoxes(boxs, confis, confThreshold, nmsThreshold, idxs);
        if(idxs.total() > 0) {
            int[] indices = idxs.toArray();
            for (int i = 0; i < indices.length; ++i) {
                int idx = indices[i];
                Rect2d box = boxes.get(idx);
                drawPred(classIds.get(idx), confidences.get(idx), (int)box.x, (int)box.y,
                        (int)(box.x + box.width), (int)(box.y + box.height), frame);
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}