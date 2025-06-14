package logic;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.google.gson.Gson;
import data.Clue;
import data.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.AutomationException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.genai.types.Type.Known.*;
import static data.Config.*;

public class GeminiSpymaster {
    private static final Logger logger = LogManager.getLogger(GeminiSpymaster.class);
    private static final Gson gson = new Gson();
    private static final Client client = new Client();
    private final Chat chat;
    private final List<Clue> clues = new ArrayList<>();

    private static final Schema Int = Schema.builder().type(INTEGER).build();
    private static final Map<String, Schema> properties = Map.of(
        "clue", Schema.builder().type(STRING).build(),
        "count", Int,
        "refers", Schema.builder().type(ARRAY).items(Int).build(),
        "risk", Int
    );
    private static final GenerateContentConfig config = GenerateContentConfig
        .builder()
        .responseMimeType(GEMINI_MIME)
        .responseSchema(Schema.builder().type(OBJECT).properties(properties).build())
        .build();

    public GeminiSpymaster() {
        this.chat = client.chats.create(GEMINI_MODEL, config);
    }

    public Clue firstClue(String boardString) {
        try(InputStream stream = GeminiSpymaster.class.getResourceAsStream(GEMINI_PROMPT)) {
            if (stream == null) {
                throw new AutomationException(ErrorCode.GEMINI_PROMPT);
            } else {
                String prompt = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                    + "\n\n" + boardString;
                return nextClue(prompt);
            }
        } catch (Exception e) {
            throw new AutomationException(ErrorCode.FIRST_CLUE, e);
        }
    }

    public Clue nextClue(String boardString) {
        logger.info("Asking clue from Gemini...");
        logger.debug("Board: \n{}", boardString);

        try {
            clues.add(gson.fromJson(
                this.chat.sendMessage(boardString).text(),
                Clue.class
            ));
            return clues.getLast();
        } catch (Exception e) {
            throw new AutomationException(ErrorCode.NEXT_CLUE, e);
        }
    }

    public static void close() {
        client.close();
    }

    public List<Clue> getClues() { return clues; }
    public Chat getChat() { return chat; }
}
