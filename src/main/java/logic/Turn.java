package logic;

import data.Clue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Stack;

import static data.Config.*;
import static logic.ActionConfirmer.endTurn;
import static logic.ActionConfirmer.flipCard;
import static util.ExecuteCommand.*;
import static util.ImageProcessor.isBoxWhite;

public class Turn {
    private static final Logger logger = LogManager.getLogger(Turn.class);

    public static void spymaster(Clue clue) {
        logger.info("Spymaster is going to give the clue: {}", clue);
        clue.refers().forEach(id ->  taps(CARD_POINTERS.get(id)));

        taps(CLUE_FIELD);
        typeClue(clue.clue());
        taps(TEAM_V_ASSASSIN, CLUE_NUMBER, clue.count() == 0
                ? ZERO_CLUE : clue.count() == -1
                ? INFINITY_CLUE : CLUE_NUMBER);

        endTurn();
        logger.info("Spymaster has ended their turn.");
    }

    public static void operative(List<Clue> clues, int[] idsInOperative) {
        Stack<Integer> toGuess = new Stack<>();
        Stack<Integer> toRisk = new Stack<>();

        int count = clues.getLast().count();
        if (count < 1) { count = TEAM_CARDS; }

        for (Clue clue : clues) {
            int flips = 0;

            for (Integer id : clue.refers().reversed()) {
                if (isBoxWhite(id)) {
                    toGuess.remove(id);
                    toGuess.push(id);
                } else { flips++; }
            }

            if (flips < clue.refers().size() && clue.risk() > -1) {
                toRisk.push(clue.risk());
            }
        }

        logger.debug("Operative's guess stack: {}", toGuess);
        logger.debug("Operative's risk stack: {}", toRisk);

        for (int i = 0; i < Math.min(count + 1, toGuess.size()); i++) {
            if (Math.random() < END_GUESSING * i) {
                logger.info("Operative has stopped guessing for this turn.");
                break;
            } else if (Math.random() < FLIP_RISK * i) {
                logger.info("Operative is going to flip the risk card: {}", toRisk.peek());
                flipCard(idsInOperative[toRisk.pop()]);
                break;
            } else {
                logger.info("Operative is going to flip the team card: {}", toGuess.peek());
                flipCard(idsInOperative[toGuess.pop()]);
                if (i == count) { return; }
            }

        }

        endTurn();
        logger.info("Operative has ended their turn.");
    }

    public static void team(Mission mission) {
        GeminiSpymaster spymaster = mission.getSpymaster();

        ActionConfirmer.switchPlayer();
        ActionConfirmer.reenterMission(false);
        spymaster(spymaster.nextClue(mission.boardString()));

        ActionConfirmer.switchPlayer();
        ActionConfirmer.reenterMission(spymaster.getClues().getLast().count() == 0);
        operative(spymaster.getClues(), mission.getIdsInOperative());
    }
}
