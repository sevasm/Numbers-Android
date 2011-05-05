package lt.ltech.numbers.android.persistence;

import java.math.BigDecimal;
import java.util.List;

import lt.ltech.numbers.android.entity.Stats;
import lt.ltech.numbers.android.persistence.mapping.StatsMapper;
import lt.ltech.numbers.player.Player;
import android.content.Context;

public class StatsDao extends NumbersDao {
    public StatsDao(Context context) {
        super(context);
    }

    public Stats findByPlayer(Player player) {
        List<Stats> list = this.findBySelection(new StatsMapper(),
                "player_id = ?", player.getId().toString());
        Stats stats = null;
        if (list.size() > 0) {
            stats = list.get(0);
        }
        if (stats == null) {
            stats = new Stats();
            stats.setPlayerId(player.getId());
            stats.setGamesPlayed(0);
            stats.setGamesWon(0);
            stats.setGamesDrawn(0);
            stats.setCorrectGuesses(0);
            stats.setAverageGuesses(BigDecimal.ZERO);
        }
        return stats;
    }

    public void saveStats(Stats stats) {
        this.insertOrUpdate(stats, stats.getId(), new StatsMapper());
    }

    @Override
    public String getTableName() {
        return "stats";
    }

    @Override
    public String[] getColumns() {
        return new String[] { "id", "player_id", "games_played", "games_won",
                "games_drawn", "correct_guesses", "average_guesses" };
    }
}
