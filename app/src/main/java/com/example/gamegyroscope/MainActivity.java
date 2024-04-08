package com.example.gamegyroscope;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView textViewDirection;
    private Button buttonStart;

    private String[] directions = {"۹۰ درجه راست", "۹۰ درجه چپ", "۹۰ درجه بالا", "۹۰ درجه پایین", "چرخش کامل دور خودتون (حول محور z)"};
    private Random random = new Random();
    private String currentDirection;
    private boolean gameRunning = false;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        handler = new Handler();
        textViewDirection = findViewById(R.id.textViewDirection);
        buttonStart = findViewById(R.id.buttonStart);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        buttonStart.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        gameRunning = true;
        generateRandomDirection();
    }

    int index;

    private void generateRandomDirection() {

        int index = random.nextInt(directions.length);
        if (this.index == index) {
            generateRandomDirection();
            return;
        }
        this.index = index;
        currentDirection = directions[index];
        textViewDirection.setText(currentDirection);
        gameRunning = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gameRunning = true;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        }, 500);

    }

    Runnable runnable = this::GmeOver;

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private float THRESHOLD = 2.0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameRunning) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];


            if (y > THRESHOLD) {
                checkDirection("۹۰ درجه راست");
            } else if (y < -THRESHOLD) {
                checkDirection("۹۰ درجه چپ");
            } else if (x > THRESHOLD) {
                checkDirection("۹۰ درجه بالا");
            } else if (x < -THRESHOLD) {
                checkDirection("۹۰ درجه پایین");
            } else if (z > (THRESHOLD * 1.5f) || z < -(THRESHOLD * 1.5f)) {
                checkDirection("چرخش کامل دور خودتون (حول محور z)");
            }


        }
    }

    private void GmeOver() {
        textViewDirection.setText("شروع کنید.");
        gameRunning = false;
        Toast.makeText(this, "باختید", Toast.LENGTH_SHORT).show();

    }

    private void checkDirection(String direction) {
        if (direction.equals(currentDirection)) {
            generateRandomDirection();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

