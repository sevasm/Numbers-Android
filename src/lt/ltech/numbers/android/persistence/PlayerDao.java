package lt.ltech.numbers.android.persistence;

import android.content.Context;

public class PlayerDao extends NumbersDao {
    public PlayerDao(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return "players";
    }

    @Override
    public String[] getColumns() {
        return new String[] { "id", "guid", "name" };
    }
}
