package util;

import data.ErrorCode;
import logic.GeminiSpymaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static data.Config.*;
import static util.SleepingBeauty.randSleep;
import static util.SleepingBeauty.sleep;

public class ExecuteCommand {
    private static final Logger logger = LogManager.getLogger(ExecuteCommand.class);

    public static void execute(String... command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            if (command[command.length - 1].equals(DEVICE_PASSWORD)) {
                logger.info("Entering password...");
            } else {
                logger.info("Executing `{}`...", String.join(" ", command));
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            if (process.waitFor() != 0) throw new AutomationException(ErrorCode.PROCESS_EXIT);
            logger.debug("Output of the command: {}", output.toString());
        } catch (IOException | InterruptedException e) {
            throw new AutomationException(ErrorCode.EXECUTE_COMMAND, e);
        }
    }

    public static void taps(Point... sequence) {
        for (Point coordinates : sequence) {
            execute("adb", "shell", "input", "tap",
                String.valueOf(coordinates.x), String.valueOf(coordinates.y));
            randSleep(TAP_DELAY);
        }
    }

    public static void screenShot() {
        execute("adb", "shell", "screencap", "-p", DEVICE_SS_PATH);
        execute("adb", "pull", DEVICE_SS_PATH, LOCAL_SS_PATH);
    }

    public static void typeClue(String clue) {
        for (char c : clue.toLowerCase().trim().toCharArray()) {
            execute("adb", "shell", "input", "keyevent", String.valueOf(c - 'a' + 29));
            randSleep(TYPE_DELAY);
        }
    }

    public static void switchPlayer() {
        logger.info("Switching players...");
        int switchUser = CURRENT_USER == SECURE_USER ? UNSECURE_USER : SECURE_USER;

        execute("adb", "shell", "am", "force-stop", APP_PACKAGE);
        logger.info("Waiting for the game to stop...");
        sleep(STOP_DELAY);

        execute("adb", "shell", "am", "switch-user", String.valueOf(switchUser));
        logger.info("Waiting for users to be switched...");
        sleep(SWITCH_DELAY);

        CURRENT_USER = switchUser;
        if (CURRENT_USER == SECURE_USER) {
            execute("adb", "shell", "input", "swipe",
                String.valueOf(SWIPE_START.x), String.valueOf(SWIPE_START.y),
                String.valueOf(SWIPE_END.x), String.valueOf(SWIPE_END.y),
                SWIPE_DURATION
            );

            logger.info("Waiting for password field to appear...");
            sleep(PASSWORD_DELAY);
            execute("adb", "shell", "input", "text", DEVICE_PASSWORD);
            execute("adb", "shell", "input", "keyevent", "KEYCODE_ENTER");
        }

        execute("adb", "shell", "am", "start", APP_LAUNCHER);
        logger.info("Waiting for the game to start...");
        sleep(START_DELAY);
    }

    public static void endSession() {
        logger.info("Ending session...");
        execute("adb", "shell", "am", "force-stop", "--user", String.valueOf(SECURE_USER), APP_PACKAGE);
        sleep(STOP_DELAY);
        execute("adb", "shell", "am", "force-stop", "--user", String.valueOf(UNSECURE_USER), APP_PACKAGE);
        sleep(STOP_DELAY);

        execute("adb", "shell", "input", "keyevent", "KEYCODE_POWER");
        GeminiSpymaster.close();
        System.exit(0);
    }
}