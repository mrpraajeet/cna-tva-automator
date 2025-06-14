import data.Config;
import logic.Mission;
import logic.Turn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static util.ExecuteCommand.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting session...");
            boolean firstMission = true;

            for (int i = 1; i <= Config.MISSIONS_TO_PLAY; i++) {
                logger.info("Starting mission {}...", i);
                Mission mission = new Mission(firstMission);

                while (!mission.isOver()) {
                    Turn.team(mission);
                }

                firstMission = false;
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            endSession();
        }
    }
}
