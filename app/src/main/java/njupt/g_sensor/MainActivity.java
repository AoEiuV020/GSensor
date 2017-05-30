package njupt.g_sensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Button sender;
    private Button receiver;
    private TextView textX;
    private TextView textY;
    private TextView textZ;
    private TextView stat;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private SensorManager sensorManager;
    private Sensor sensor;
    private BluetoothUtil bluetooth;
    private GModel gModel;
    private Handler handler = new Handler();
    private SensorEventListener sensorEventListener;
    private AsyncTask<Object, Object, Object> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gModel = new GModel();
        textX = (TextView) findViewById(R.id.editTextX);
        textY = (TextView) findViewById(R.id.editTextY);
        textZ = (TextView) findViewById(R.id.editTextZ);
        stat = (TextView) findViewById(R.id.stat);
        sender = (Button) findViewById(R.id.sender);
        receiver = (Button) findViewById(R.id.receiver);
        listView = (ListView) findViewById(R.id.listView);
        bluetooth = new BluetoothUtil(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> bluetooth.receiverMode(position));
        sender.setOnClickListener(v -> {
            setStat("发送");
            listView.setVisibility(View.INVISIBLE);
            boolean isBluetoothEnabled = bluetooth.bluetoothList();
            if(!isBluetoothEnabled) {
                return;
            }
            bluetooth.senderMode();
            sender.setBackgroundColor(Color.RED);
            receiver.setBackgroundColor(0);
            receiver.setClickable(false);
            if (sensorManager == null) {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                if (sensorManager == null) {
                    String msg = "设备不支持传感器";
                    textX.setText(msg);
                    textY.setText(msg);
                    textZ.setText(msg);
                    setStat(msg);
                    return;
                }
            }
            if (sensor == null) {
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (sensor == null) {
                    String msg = "设备不支持加速度传感器";
                    textX.setText(msg);
                    textY.setText(msg);
                    textZ.setText(msg);
                    setStat(msg);
                    return;
                }
            }
            if (sensorEventListener == null) {
                sensorEventListener = new SensorEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        gModel.set(event.values);
                        textX.setText("" + gModel.x);
                        textY.setText("" + gModel.y);
                        textZ.setText("" + gModel.z);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                };
            }
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            task = new SenderTask();
            task.execute(this);
        });
        receiver.setOnClickListener((View view) -> {
            setStat("接收");
            listView.setVisibility(View.VISIBLE);
            boolean isBluetoothEnabled = bluetooth.bluetoothList();
            if(!isBluetoothEnabled) {
                return;
            }
            receiver.setBackgroundColor(Color.RED);
            sender.setBackgroundColor(0);
            sender.setClickable(false);
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
            textX.setText("");
            textY.setText("");
            textZ.setText("");
            task = new ReceiverTask();
            task.execute(this);
        });
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener((v) -> {
            finish();
            System.exit(0);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.stop();
    }

    public void setStat(String str) {
        handler.post(() -> stat.setText(str));
    }

    public void setDevices(List<String> namedDevices) {
        adapter.clear();
        adapter.addAll(namedDevices);
    }

    @SuppressLint("StaticFieldLeak")
    class SenderTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            Log.d(TAG, "SenderTask: ");
            try {
                while (!isCancelled()) {
                    Thread.sleep(50);
                    bluetooth.send(gModel);
                }
            } catch (InterruptedException e) {
                setStat("中断");
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ReceiverTask extends AsyncTask<Object, Object, Object> {
        @SuppressLint("SetTextI18n")
        @Override
        protected Object doInBackground(Object... objects) {
            Log.d(TAG, "ReceiverTask: ");
            try {
                while (!isCancelled()) {
                    Thread.sleep(50);
                    float[] values = bluetooth.receive();
                    if (values == null) {
                        continue;
                    }
                    gModel.set(values);
                    handler.post(() -> {
                        textX.setText("" + gModel.x);
                        textY.setText("" + gModel.y);
                        textZ.setText("" + gModel.z);
                    });
                }
            } catch (InterruptedException e) {
                setStat("中断");
            }
            return null;
        }
    }
}
