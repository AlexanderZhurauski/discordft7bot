package org.ludus.ft7bot.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Shamelessly "borrowed" from <a href="https://www.geeksforgeeks.org/elo-rating-algorithm/"></a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EloUtil {
    public static final int WIN = 1;
    public static final int LOSS = 0;
    private static final int K = 32; // This is a typical value but can be adjusted depending on the level of the players

    /**
     * Calculate the updated Elo rating for a single player.
     * @param currentRating Current Elo rating of the player
     * @param opponentRating Elo rating of the opponent
     * @param outcome 1 if the player wins, 0 if the player loses.
     * @return Updated Elo rating of the player.
     */
    public static double calculateNewElo(double currentRating, double opponentRating, int outcome) {
        // Calculate the probability of winning for the player against this specific opponent
        double expectedScore = Probability(currentRating, opponentRating);

        // Calculate the updated Elo rating
        return currentRating + K * (outcome - expectedScore);
    }

    /**
     * Helper method to calculate the probability of winning.
     * @param playerRating Elo rating of the player
     * @param opponentRating Elo rating of the opponent
     * @return Probability of winning for the player.
     */
    private static double Probability(double playerRating, double opponentRating) {
        return 1.0 / (1 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }
}
