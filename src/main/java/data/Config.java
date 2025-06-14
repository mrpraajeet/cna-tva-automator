package data;

import util.AutomationException;
import util.Point;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class Config {
    private Config() {}
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream stream = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new AutomationException(ErrorCode.CONFIG_FILE);
            } else {
                properties.load(stream);
            }
        } catch (IOException e) {
            throw new AutomationException(ErrorCode.CONFIG_FILE, e);
        }
    }

    private static String getString(String key) {
        return properties.getProperty(key);
    }

    private static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    private static double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    private static Point getPoint(String key) {
        String[] coordinates = properties.getProperty(key).split(",");
        return new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
    }

    // Frequently tweaked
    public static final double FLIP_RISK = getDouble("FLIP_RISK");
    public static final double END_GUESSING = getDouble("END_GUESSING");
    public static final int MISSIONS_TO_PLAY = getInt("MISSIONS_TO_PLAY");
    public static final double SECURE_USER_SPYMASTER = getDouble("SECURE_USER_SPYMASTER");

    // Device level
    public static final String APP_LAUNCHER = new StringBuilder(getString("APP_LAUNCHER")).reverse().toString();
    public static final String APP_PACKAGE = new StringBuilder(getString("APP_PACKAGE")).reverse().toString();
    public static final int SECURE_USER = getInt("SECURE_USER");
    public static final int UNSECURE_USER = getInt("UNSECURE_USER");
    public static int CURRENT_USER = getInt("CURRENT_USER");
    public static final String DEVICE_PASSWORD = System.getenv("DEVICE_PASSWORD");
    public static final Point SWIPE_START = getPoint("SWIPE_START");
    public static final Point SWIPE_END = getPoint("SWIPE_END");
    public static final String SWIPE_DURATION = getString("SWIPE_DURATION");

    // AI Studio
    public static final String GEMINI_MODEL = getString("GEMINI_MODEL");
    public static final String GEMINI_MIME = getString("GEMINI_MIME");
    public static final String GEMINI_PROMPT = getString("GEMINI_PROMPT");

    // Tesseract OCR
    public static final String TESSERACT_PATH = getString("TESSERACT_PATH");
    public static final String TESSERACT_LANGUAGE = getString("TESSERACT_LANGUAGE");
    public static final int TESSERACT_MODE = getInt("TESSERACT_MODE");
    public static final String TESSERACT_WHITELIST = getString("TESSERACT_WHITELIST");

    // Resource Paths
    //public static final String BASE_WORDS_PATH = getString("BASE_WORDS_PATH");
    public static final String LOCAL_SS_PATH = getString("LOCAL_SS_PATH");
    public static final String DEVICE_SS_PATH = getString("DEVICE_SS_PATH");

    // Device Delays
    public static final int SWITCH_DELAY = getInt("SWITCH_DELAY");
    public static final int PASSWORD_DELAY = getInt("PASSWORD_DELAY");
    public static final int START_DELAY = getInt("START_DELAY");
    public static final int STOP_DELAY = getInt("STOP_DELAY");

    // Game Delays
    public static final double TAP_DELAY = getDouble("TAP_DELAY");
    public static final double TYPE_DELAY = getDouble("TYPE_DELAY");
    public static final int REWARD_DELAY = getInt("REWARD_DELAY");
    public static final int RETRY_DELAY = getInt("RETRY_DELAY");
    public static final int MAX_RETRIES = getInt("MAX_RETRIES");
    public static final int WATCH_DELAY = getInt("WATCH_DELAY");

    // Board
    public static final int BOARD_ROWS = getInt("BOARD_ROWS");
    public static final int BOARD_COLUMNS = getInt("BOARD_COLUMNS");
    public static final int TEAM_CARDS = getInt("TEAM_CARDS");

    // Card
    public static final List<Point> CARD_POINTERS;
    public static final int CROP_WIDTH = getInt("CROP_WIDTH");
    public static final int CROP_HEIGHT = getInt("CROP_HEIGHT");
    public static final Point COLOR_OFFSET = getPoint("COLOR_OFFSET");
    static {
        Point CARD_POINTERS_START = getPoint("CARD_POINTERS_START");
        Point CARD_POINTERS_OFFSET = getPoint("CARD_POINTERS_OFFSET");
        List<Point> pointers = new ArrayList<>(BOARD_ROWS * BOARD_COLUMNS);

        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                pointers.add(new Point(
                    CARD_POINTERS_START.x + j * CARD_POINTERS_OFFSET.x,
                    CARD_POINTERS_START.y + i * CARD_POINTERS_OFFSET.y
                ));
            }
        }

        CARD_POINTERS = Collections.unmodifiableList(pointers);
    }

    // Bottom UI
    public static final Point CLUE_FIELD = getPoint("CLUE_FIELD");
    public static final Point END_TURN = getPoint("END_TURN");
    public static final Point CLUE_NUMBER = getPoint("CLUE_NUMBER");
    public static final Point ZERO_CLUE = getPoint("ZERO_CLUE");
    public static final Point INFINITY_CLUE = getPoint("INFINITY_CLUE");

    // Game UI
    public static final Point EXIT_MISSION = getPoint("EXIT_MISSION");
    public static final Point JOIN_MISSION = getPoint("JOIN_MISSION");
    public static final Point CONTINUE_TO_MENU = getPoint("CONTINUE_TO_MENU");
    public static final Point TEAM_V_ASSASSIN = getPoint("TEAM_V_ASSASSIN");
    public static final Point LEFTMOST_MISSION = CARD_POINTERS.get(getInt("LEFTMOST_MISSION_INDEX"));
    public static final Point GOT_IT = getPoint("GOT_IT");
    public static final Point OPEN_LOGS = getPoint("OPEN_LOGS");

    // Color Thresholds
    public static final int RED_DOMINANCE = getInt("RED_DOMINANCE");
    public static final int BLUE_DOMINANCE = getInt("BLUE_DOMINANCE");
    public static final int WHITE_MIN = getInt("WHITE_MIN");
    public static final int BEIGE_MIN = getInt("BEIGE_MIN");
    public static final int BLACK_MAX = getInt("BLACK_MAX");
}
