package com.example.accped;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.AsyncQueryHandler;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements SensorEventListener , StepListener{

    private TextView Steps;
  //  private TextView Distance;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    //private  static final String TEXT_DISTANCE = "Distance: ";
    private int numSteps;
    private Button StartBtn , StopBtn;
    FirebaseDatabase database;

    private static final String TAG = "MainActivity";

    DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        Steps = (TextView) findViewById(R.id.Steps);
        StartBtn = (Button) findViewById(R.id.StartBtn);
        StopBtn = (Button) findViewById(R.id.StopBtn);


        StartBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        StopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(MainActivity.this);

            }
        });

    }


    @Override
    public void step(long timeNs) {
        numSteps++;
        Steps.setText(TEXT_NUM_STEPS + numSteps);
     //   Distance.setText(TEXT_DISTANCE  + (numSteps * 0.67));
         ;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
       if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
       // if(sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
            Log.d(TAG, "onSensorChanged: Steps: " + numSteps);

           Log.d(TAG, "onSensorChanged: X: " + event.values[0] + ",Y: " + event.values[1] + ",Z:" + event.values[2]);
           AddData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}



        public void AddData() {

            String StepsCount=Steps.getText().toString();
            String id=ref.push().getKey();

            if (!TextUtils.isEmpty(StepsCount)) {

                SaveData data=new SaveData(StepsCount);
                ref.child(id).setValue(data);
                //Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_LONG).show();
            }
           // else
               // Toast.makeText(getApplicationContext(), "Not Loaded", Toast.LENGTH_LONG).show();

        }

}

