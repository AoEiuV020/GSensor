package njupt.g_sensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 负责通过蓝牙接收数据，
 * Created by AoEiuV020 on 17-5-30.
 */
class BTReceiver {
    private static final String TAG = "BTReceiver";
    private DataInputStream inputStream;
    private BluetoothDevice device;

    BTReceiver(BluetoothDevice device) {
        this.device = device;
    }

    float[] receive() {
        float[] ret = null;
        try {
            if (inputStream == null) {
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothUtil.BT_UUID);
                socket.connect();
                inputStream = new DataInputStream(socket.getInputStream());
            }
            ret = new float[3];
            for (int i = 0; i < 3; i++) {
                ret[i] = inputStream.readFloat();
            }
        } catch (IOException e) {
            Log.e(TAG, "receive: ", e);
        }
        return ret;
    }
}
