package lt.ltech.numbers.android.entity;

import java.math.BigDecimal;

import lt.ltech.numbers.android.persistence.BasicEntity;

public class Stats extends BasicEntity {
    private Long id;
    private Long playerId;
    private Integer gamesPlayed;
    private Integer gamesWon;
    private Integer gamesDrawn;
    private Integer correctGuesses;
    private BigDecimal averageGuesses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(Integer gamesWon) {
        this.gamesWon = gamesWon;
    }

    public Integer getGamesDrawn() {
        return gamesDrawn;
    }

    public void setGamesDrawn(Integer gamesDrawn) {
        this.gamesDrawn = gamesDrawn;
    }

    public Integer getCorrectGuesses() {
        return correctGuesses;
    }

    public void setCorrectGuesses(Integer correctGuesses) {
        this.correctGuesses = correctGuesses;
    }

    public BigDecimal getAverageGuesses() {
        return averageGuesses;
    }

    public void setAverageGuesses(BigDecimal averageGuesses) {
        this.averageGuesses = averageGuesses;
    }

    public String toString() {
        return String.format("%3d %3d %3d %3d %3d %3s", gamesPlayed, gamesWon,
                gamesDrawn, getGamesLost(), correctGuesses,
                averageGuesses.toString());
    }

    public Integer getGamesLost() {
        if (this.gamesPlayed != null && this.gamesWon != null
                && this.gamesDrawn != null) {
            return this.gamesPlayed - this.gamesWon - this.gamesDrawn;
        } else {
            return null;
        }
    }
}
