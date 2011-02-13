package lt.ltech.numbers.android.log;

import android.util.Log;

/**
 * A convenient wrapper for Android logging. It makes logging simpler by
 * enabling tag re-use and passing messages and their parameters directly to
 * String.format()
 */
public class Logger {
    private final String tag;

    public Logger(String name) {
        this.tag = name;
    }

    public Logger(Class<?> clazz) {
        this.tag = clazz.getName();
    }

    public void v(String message, Object... parameters) {
        Log.v(this.tag, String.format(message, parameters));
    }

    public void d(String message, Object... parameters) {
        Log.d(this.tag, String.format(message, parameters));
    }

    public void i(String message, Object... parameters) {
        Log.i(this.tag, String.format(message, parameters));
    }

    public void w(String message, Object... parameters) {
        Log.w(this.tag, String.format(message, parameters));
    }

    public void e(String message, Object... parameters) {
        Log.e(this.tag, String.format(message, parameters));
    }
}
