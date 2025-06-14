package logic;

import data.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.AutomationException;
import util.ExecuteCommand;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

import static data.Config.*;
import static util.ExecuteCommand.taps;
import static util.ImageProcessor.*;
import static util.SleepingBeauty.sleep;

public class ActionConfirmer {
    private ActionConfirmer() {}
    private static final Logger logger = LogManager.getLogger(ActionConfirmer.class);

    public static void joinMission() {
        taps(JOIN_MISSION, TEAM_V_ASSASSIN);
        retry(
            () -> isBoxWhite(0) && isBoxWhite(CARD_POINTERS.size() - 1),
            "Player hasn't joined the mission yet."
        );
        logger.info("Watching the consequences of other player's (and maybe Assassin's) turn.");
        sleep(WATCH_DELAY);
    }

    public static void reenterMission(boolean zeroClue) {
        taps(LEFTMOST_MISSION);
        retry(
            () -> Arrays.stream(getRGB(OPEN_LOGS)).sum() > WHITE_MIN,
            "Player hasn't reentered their existing mission to make a turn yet."
        );
        if (zeroClue) { taps(GOT_IT); }
        sleep(WATCH_DELAY);
    }

    public static void flipCard(int id) {
        taps(CARD_POINTERS.get(id), CARD_POINTERS.get(id));
        retry(
            () -> !isBoxWhite(id),
            "Operative hasn't flipped their card yet."
        );
    }

    public static void endTurn() {
        taps(END_TURN);
        retry(
            () -> Arrays.stream(getRGB(END_TURN)).sum() < WHITE_MIN,
            "Player hasn't ended their turn yet."
        );
    }

    public static void switchPlayer() {
        ExecuteCommand.switchPlayer();
        retry(
            () -> Arrays.stream(getRGB(CONTINUE_TO_MENU)).sum() > BLACK_MAX,
            "Players haven't been switched yet."
        );
    }

    public static void retry(BooleanSupplier condition, String message) {
        for (int i = 1; i <= MAX_RETRIES; i++) {
            replaceImage();

            if (condition.getAsBoolean()) { break; }
            if (i == MAX_RETRIES) { throw new AutomationException(ErrorCode.MAX_RETRIES); }

            logger.warn("{} Rechecking after {} seconds", message, RETRY_DELAY);
            sleep(RETRY_DELAY);
        }
    }
}