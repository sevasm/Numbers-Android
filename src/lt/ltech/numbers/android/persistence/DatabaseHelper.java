package lt.ltech.numbers.android.persistence;

import lt.ltech.numbers.android.log.Logger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final Logger logger = new Logger(
            DatabaseHelper.class.getName());

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "numbers";

    private static final String PLAYERS_CREATE_Q = "CREATE TABLE players "
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)";
    private static final String PLAYERS_DROP_Q = "DROP TABLE IF EXISTS players";
    private static final String PLAYERS_UNQ_Q = "CREATE UNIQUE INDEX "
            + "IF NOT EXISTS players_unq ON players (name)";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLAYERS_CREATE_Q);
        db.execSQL(PLAYERS_UNQ_Q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logger.i("Upgrading database version from %d to %d", oldVersion,
                newVersion);
        db.execSQL(PLAYERS_DROP_Q);
        this.onCreate(db);
    }
}
