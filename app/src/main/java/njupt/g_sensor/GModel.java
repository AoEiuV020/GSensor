package njupt.g_sensor;

/**
 * Created by AoEiuV020 on 17-5-30.
 */

@SuppressWarnings("WeakerAccess")
public class GModel {
    public float x;
    public float y;
    public float z;

    public void set(float[] values) {
        if (values.length >= 3) {
            x = values[0];
            y = values[1];
            z = values[2];
        }
    }

    public float[] get() {
        return new float[]{x, y, z};
    }

    @Override
    public String toString() {
        return "x = [" + x + "], y = [" + y + "], z = [" + z + "]";
    }
}
