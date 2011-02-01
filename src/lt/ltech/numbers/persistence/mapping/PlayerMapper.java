package lt.ltech.numbers.persistence.mapping;

import lt.ltech.numbers.player.Player;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class PlayerMapper implements EntityMapper<Player> {
    @Override
    public ContentValues toContentValues(Player entity) {
        ContentValues cv = new ContentValues();
        cv.put("name", entity.getName());
        return cv;
    }

    @Override
    public Player mapRow(Cursor cursor) throws SQLException {
        int i = 0;
        Long id = cursor.getLong(i++);
        String name = cursor.getString(i++);
        Player player = new Player(name);
        player.setId(id);
        return player;
    }
}
