package njupt.g_sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 负责通过蓝牙发送数据，
 * Created by AoEiuV020 on 17-5-30.
 */
class BTSender {
    private static final String TAG = "BTSender";
    private BluetoothSocket socket;
    private BluetoothServerSocket serverSocket;
    private DataOutputStream output;

    BTSender(BluetoothAdapter bluetoothAdapter) {
        try {
            this.serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(BluetoothUtil.BT_NAME, BluetoothUtil.BT_UUID);
        } catch (IOException e) {
            Log.e(TAG, "BTSender: ", e);
        }
    }

    void send(GModel gModel) {
        if (output == null) {
            try {
                socket = serverSocket.accept();
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(TAG, "BTSender: ", e);
                return;
            }
        }
        try {
            output.writeFloat(gModel.x);
            output.writeFloat(gModel.y);
            output.writeFloat(gModel.z);
        } catch (IOException e) {
            Log.e(TAG, "send: ", e);
        }
    }
}
