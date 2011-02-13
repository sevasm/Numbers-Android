package lt.ltech.numbers.android.persistence.mapping;

import java.util.UUID;

import lt.ltech.numbers.player.Player;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class PlayerMapper implements EntityMapper<Player> {
    @Override
    public ContentValues toContentValues(Player entity) {
        ContentValues cv = new ContentValues();
        cv.put("guid", entity.getGuid().toString());
        cv.put("name", entity.getName());
        return cv;
    }

    @Override
    public Player mapRow(Cursor cursor) throws SQLException {
        int i = 0;
        Long id = cursor.getLong(i++);
        UUID guid = UUID.fromString(cursor.getString(i++));
        String name = cursor.getString(i++);
        Player player = new Player(guid, name);
        player.setId(id);
        return player;
    }
}
