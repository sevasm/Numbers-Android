package lt.ltech.numbers.android.persistence;

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
        return stats;
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
