package lt.ltech.numbers.android.persistence.mapping;

import lt.ltech.numbers.android.entity.Stats;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class StatsMapper implements EntityMapper<Stats> {
    @Override
    public ContentValues toContentValues(Stats entity) {
        ContentValues values = new ContentValues();
        values.put("player_id", entity.getPlayerId());
        values.put("games_played", entity.getGamesPlayed());
        values.put("games_won", entity.getGamesWon());
        values.put("games_drawn", entity.getGamesDrawn());
        values.put("correct_guesses", entity.getCorrectGuesses());
        values.put("average_guesses", entity.getAverageGuesses());
        return values;
    }

    @Override
    public Stats mapRow(Cursor cursor) throws SQLException {
        Stats stats = new Stats();
        int i = 0;
        stats.setId(cursor.getLong(i++));
        stats.setPlayerId(cursor.getLong(i++));
        stats.setGamesPlayed(cursor.getInt(i++));
        stats.setGamesWon(cursor.getInt(i++));
        stats.setGamesDrawn(cursor.getInt(i++));
        stats.setCorrectGuesses(cursor.getInt(i++));
        stats.setAverageGuesses(cursor.getInt(i++));
        return stats;
    }
}
