package org.ludus.ft7bot.constant;

public interface Message {
    /**
     * User-management-related messages
     */
    String SUCCESSFUL_REGISTRATION = "%s has successfully been registered!";
    String SUCCESSFUL_USERNAME_CHANGE = "Successfully changed username from %s to %s";
    String MY_CURRENT_ELO = "Your current ELO rating is: %f";

    /**
     * Ft7-related messages
     */
    String FT7_CHALLENGE_SENT = "Challenge successfully sent to %s!";
    String FT7_CHALLENGE_RECEIVED = "You have been challenged to a ft7 by %s, would you like to accept?";
    String FT7_ACCEPTED_BY_OPPONENT = "%s has accepted your challenge!";
    String FT7_REJECTED_BY_OPPONENT = "%s has rejected your challenge.";
    String FT7_ACCEPTED_BY_YOURSELF = "You have accepted the challenge!";
    String FT7_REJECTED_BY_YOURSELF = "You have rejected the challenge.";
    String FT7_RESULT_REPORTED = "The result has been reported successfully. Awaiting confirmation from %s.";
    String FT7_RESULT_CONFIRMED = "Successfully confirmed the result! Your new rating is: %f";
    String FT7_CANCELLED_SUCCESSFULLY = "Your duel with %s has successfully been cancelled.";

    /**
     * Error messages
     */
    String DUEL_FINISHED_OR_CANCELLED = "The duel is either already finished or cancelled";
    String INVALID_WINNER_SPECIFIED = "The specified winner is not part of the duel";
    String FAILED_TO_CONFIRM_RESULT = "Failed to confirm the result! The winners reported by you and %s don't match.";
    String DUEL_ALREADY_IN_PROGRESS = "There is either a pending ft7 or ft7 request between you and the requested opponent.";
    String REQUESTED_DUEL_NOT_FOUND = "The requested ft7 duel does not exist";
    String REGISTRATION_UNEXPECTED_ERROR = "Unexpected error during registration, please contact the server administrator.";
    String CHALLENGER_NOT_REGISTERED = "Please register in the ft7 system using the '/register <username>' command.";
    String OPPONENT_NOT_REGISTERED = "The player you have challenged has not registered in the ft7 system.";
    String DISCORD_ID_ALREADY_REGISTERED = "A player with this discord id has already been registered.";
    String USERNAME_ALREADY_REGISTERED = "A player with this username has already been registered.";
    String SELF_CHALLENGE_IMPOSSIBLE = "No, you can't challenge yourself to a ft7 :).";
    String USER_RETRIEVAL_FAILED = "Failed to retrieve the requested user: ";
}
