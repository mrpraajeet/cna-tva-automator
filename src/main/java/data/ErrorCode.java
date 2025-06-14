package data;

public enum ErrorCode {
    // data.Config
    CONFIG_FILE("Unable to load the configuration file."),

    // logic.GeminiSpymaster
    GEMINI_PROMPT("Prompt text file for Gemini is null"),
    FIRST_CLUE("Failed to get the first clue from Gemini."),
    NEXT_CLUE("Failed to get the next clue from Gemini."),

    // util.ExecuteCommand
    PROCESS_EXIT("Executed process returned non-zero exit code."),
    EXECUTE_COMMAND("Failed to execute the command."),
    SCREENSHOT("Failed to read the screenshot."),

    // util.ImageProcessor
    STARTING_BOARD("Failed to prepare the board from the screenshot."),

    // util.SleepingBeauty
    SLEEP_INTERRUPTED("Sleep interrupted."),

    // logic.Mission
    NOT_SAME("Both users are not in the same mission"),

    // logic.ActionConfirmer
    MAX_RETRIES("Max retries reached before being able to confirm the action.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
