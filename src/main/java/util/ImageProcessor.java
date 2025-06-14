package util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import data.Card;
import data.ErrorCode;
import data.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static data.Config.*;
import static util.ExecuteCommand.screenShot;

public class ImageProcessor {
    private static final Logger logger = LogManager.getLogger(ImageProcessor.class);
    private static final Tesseract tesseract;
    private static BufferedImage image;

    private ImageProcessor() {}

    static {
        logger.info("Initializing Tesseract");
        tesseract = new Tesseract();
        tesseract.setDatapath(TESSERACT_PATH);
        tesseract.setLanguage(TESSERACT_LANGUAGE);
        tesseract.setPageSegMode(TESSERACT_MODE);
        tesseract.setVariable("tessedit_char_whitelist", TESSERACT_WHITELIST);
        logger.info("Tesseract initialized");
    }

    public static void replaceImage() {
        logger.info("Replacing current screenshot with a new one.");
        screenShot();
        try {
            image = ImageIO.read(new File(LOCAL_SS_PATH));
        } catch (IOException e) {
            throw new AutomationException(ErrorCode.SCREENSHOT, e);
        }
    }

    public static List<Card> startingBoard() {
        try {
            logger.info("Preparing the game board.");
            List<Card> board = new ArrayList<>();

            for (int id = 0; id < CARD_POINTERS.size(); id++) {
                Point p = CARD_POINTERS.get(id);
                BufferedImage crop = image.getSubimage(p.x, p.y, CROP_WIDTH, CROP_HEIGHT);
                String word = tesseract.doOCR(crop).toUpperCase().trim();
                board.add(new Card(id, word, cardTeam(p)));
            }

            logger.info("Game board has been prepared.");
            logger.debug("Board: {}", board);
            return board;
        } catch (TesseractException | RasterFormatException e) {
            throw new AutomationException(ErrorCode.STARTING_BOARD, e);
        }
    }

    public static boolean isBoxWhite(int id) {
        int[] rgb = getRGB(CARD_POINTERS.get(id));
        return rgb[0] + rgb[1] + rgb[2] > WHITE_MIN;
    }

    public static Team cardTeam(Point point) {
        int[] rgb = getRGB(point.add(COLOR_OFFSET));
        if (rgb[0] - rgb[2] > RED_DOMINANCE) return Team.RED;
        if (rgb[2] - rgb[0] > BLUE_DOMINANCE) return Team.BLUE;
        if (rgb[0] + rgb[1] + rgb[2] > BEIGE_MIN) return Team.BYSTANDER;
        return Team.ASSASSIN;
    }

    public static int[] getRGB(Point point) {
        int rgb = image.getRGB(point.x, point.y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        logger.debug("R: {}, G: {}, B: {}", r, g, b);
        return new int[]{r, g, b};
    }
}
