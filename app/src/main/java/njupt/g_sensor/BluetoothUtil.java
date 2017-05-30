package njupt.g_sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 封装蓝牙相关操作，
 * Created by AoEiuV020 on 17-5-29.
 */

class BluetoothUtil {
    private static final String TAG = "BluetoothUtil";
    static final String BT_NAME = BluetoothUtil.class.getName();
    static final UUID BT_UUID = UUID.nameUUIDFromBytes(BluetoothUtil.class.getName().getBytes());
    private MainActivity mainActivity;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private BluetoothAdapter bluetoothAdapter;
    private BTSender sender;
    private BTReceiver receiver;

    BluetoothUtil(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    synchronized boolean bluetoothList() {
        if (!bluetoothAdapter.isEnabled()) {
            mainActivity.setStat("蓝牙没启动");
            return false;
        }
        bluetoothDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        List<String> namedDevices = new ArrayList<>(bluetoothDevices.size());
        for (BluetoothDevice device :
                bluetoothDevices) {
            namedDevices.add(device.getName() + ", " + device.getAddress());
        }
        mainActivity.setDevices(namedDevices);
        if (bluetoothDevices.size() == 0) {
            mainActivity.setStat("蓝牙没配对");
            return false;
        }
        return true;
    }

    void senderMode() {
        if (sender == null) {
            sender = new BTSender(bluetoothAdapter);
        }
    }

    void receiverMode(int position) {
        BluetoothDevice device = bluetoothDevices.get(position);
        if (receiver == null) {
            receiver = new BTReceiver(device);
        }
    }

    void send(GModel gModel) {
        Log.d(TAG, "send: " + gModel);
        if (sender != null) {
            sender.send(gModel);
        }
    }

    float[] receive() {
        Log.d(TAG, "receive: ");
        float[] ret = null;
        if (receiver != null) {
            ret = receiver.receive();
        }
        return ret;
    }

    void stop() {
    }
}
