package logic;

import data.ErrorCode;
import data.Card;
import data.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.AutomationException;

import java.util.*;
import java.util.stream.IntStream;

import static data.Config.*;
import static data.Team.*;
import static logic.ActionConfirmer.joinMission;
import static util.ExecuteCommand.*;
import static util.ImageProcessor.*;
import static util.SleepingBeauty.sleep;

public class Mission {
    private static final Logger logger = LogManager.getLogger(Mission.class);
    private final int spymasterUser;
    private final GeminiSpymaster spymaster;
    private final boolean[] isFlipped;
    private final List<Card> board;
    private final Team team;
    private final int[] idsInOperative;

    public Mission(boolean firstMission) {
        this.spymasterUser = Math.random() < SECURE_USER_SPYMASTER ? SECURE_USER : UNSECURE_USER;
        logger.info("Spymaster for this user: {}", spymasterUser);
        this.spymaster = new GeminiSpymaster();
        this.isFlipped = new boolean[CARD_POINTERS.size()];

        if (firstMission) {
            logger.info("Buffer time to ensure the current user is inside the game.");
        } else {
            logger.info("Waiting for post mission animations to end.");
        }
        sleep(REWARD_DELAY);

        if (spymasterUser == CURRENT_USER) {
            logger.info("Current user is the spymaster, No need to switch players.");
            if (!firstMission) { taps(CONTINUE_TO_MENU, EXIT_MISSION); }
        } else { ActionConfirmer.switchPlayer(); }

        logger.info("Spymaster is joining a mission.");
        joinMission();

        this.board = startingBoard();
        this.team = board.stream()
            .filter(card -> card.team() == RED)
            .count() == TEAM_CARDS ? RED : BLUE;

        logger.info("Spymaster is going to give their first clue for this mission.");
        Turn.spymaster(spymaster.firstClue(boardString(board, isFlipped, team)));

        this.idsInOperative = operativeSetup(board);

        logger.info("Operative is going to make their first guess for this mission.");
        Turn.operative(spymaster.getClues(), idsInOperative);
    }

    public static int[] operativeSetup(List<Card> board) {
        ActionConfirmer.switchPlayer();
        joinMission();
        logger.info("Operative has joined a mission.");

        List<String> operativeWords = startingBoard()
            .stream()
            .map(Card::word)
            .toList();
        logger.debug("Word order in operative's view: {}", operativeWords);

        int[] idsInOperative = IntStream
            .range(0, CARD_POINTERS.size())
            .map(id -> operativeWords.indexOf(board.get(id).word()))
            .toArray();

        if (Arrays.stream(idsInOperative).anyMatch(id -> id == -1)) {
            throw new AutomationException(ErrorCode.NOT_SAME);
        }

        return idsInOperative;
    }

    public boolean isOver() {
        logger.info("Checking if the mission is over.");
        replaceImage();
        int remainingRed = 0, remainingBlue = 0;

        for (Card card : board) {
            isFlipped[card.id()] = !isBoxWhite(card.id());
            if (isFlipped[card.id()]) { if (card.team() == ASSASSIN) { return true; }}
            else if (card.team() == RED) { remainingRed++; }
            else if (card.team() == BLUE) { remainingBlue++; }
        }

        return remainingRed == 0 || remainingBlue == 0;
    }

    public static String boardString(List<Card> board, boolean[] isFlipped, Team team) {
        StringBuilder sb = new StringBuilder();
        sb.append("USER: \n")
            .append("Give a clue for team ")
            .append(team)
            .append("'s yet to be flipped cards")
            .append(" while trying to avoid other cards.")
            .append("\n")
            .append("Current state of the board:\n");

        for (Card card : board) {
            sb.append(card.id()).append(" ");
            sb.append(card.word()).append(" - ");
            sb.append(isFlipped[card.id()] ? "Flipped" : "Unflipped").append(" ");

            sb.append(card.team());
            if (card.team() == team) { sb.append(" <- Your Team"); }
            else if (card.team() == ASSASSIN) sb.append("!!");

            sb.append("\n");
        }

        return sb.toString();
    }

    public String boardString() {
        return boardString(board, isFlipped, team);
    }

    public int getSpymasterUser() { return spymasterUser; }
    public GeminiSpymaster getSpymaster() { return spymaster; }
    public boolean[] getIsFlipped() { return isFlipped; }
    public List<Card> getBoard() { return board; }
    public Team getTeam() { return team; }
    public int[] getIdsInOperative() { return idsInOperative; }
}
