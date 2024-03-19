package client;

import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class PrintBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private final String teamColor;
    private final GameData game;
    private final String[] blackHeaders = { "h", "g", "f", "e", "d", "c", "b", "a" };
    private final String[] blackSideHeaders = { "1", "2", "3", "4", "5", "6", "7", "8" };
    private final String[] whiteHeaders = { "a", "b", "c", "d", "e", "f", "g", "h" };
    private final String[] whiteSideHeaders = { "8", "7", "6", "5", "4", "3", "2", "1" };

    public PrintBoard(String teamColor, GameData game) {
        this.teamColor = teamColor;
        this.game = game;
    }

    public void print() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (this.teamColor.equals("WHITE")) {
            printBlack(out);
            out.println();
            printWhite(out);
        } else if (this.teamColor.equals("BLACK")) {
            printWhite(out);
            out.println();
            printBlack(out);
        } else {
            printBlack(out);
            out.println();
            printWhite(out);
        }

        out.println();
    }

    private void printWhite(PrintStream out) {
        out.print(ERASE_SCREEN);

        drawHeaders(out, whiteHeaders);
        drawBoard(out, whiteSideHeaders);
        drawHeaders(out, whiteHeaders);
    }

    private void printBlack(PrintStream out) {
        out.print(ERASE_SCREEN);

        drawHeaders(out, blackHeaders);
        drawBoard(out, blackSideHeaders);
        drawHeaders(out, blackHeaders);
    }

    private void drawHeaders(PrintStream out, String[] headers) {
        setGray(out);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0) {
                out.print(SPACE.repeat(SQUARE_SIZE_IN_CHARS));
            }

            drawHeader(out, headers[boardCol]);
        }
        out.print(SPACE.repeat(SQUARE_SIZE_IN_CHARS));
        resetColor(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(SPACE);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(headerText);
        out.print(SPACE);
    }

    private static void drawBoard(PrintStream out, String[] sideHeaders) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++) {
            setGray(out);

            drawHeader(out, sideHeaders[boardRow]);
            drawSquares(out, boardRow);

            setGray(out);
            drawHeader(out, sideHeaders[boardRow]);

            resetColor(out);
            out.println();
        }
    }

    private static void drawSquares(PrintStream out, int rowNum) {
        var colorFirst = SET_BG_COLOR_WHITE;
        var colorSecond = SET_BG_COLOR_BLACK;
        if (rowNum % 2 != 0) {
            colorFirst = SET_BG_COLOR_BLACK;
            colorSecond = SET_BG_COLOR_WHITE;
        }

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            if (boardCol % 2 == 0) {
                out.print(colorFirst);

            } else {
                out.print(colorSecond);
            }
            out.print(SPACE.repeat(SQUARE_SIZE_IN_CHARS));
        }
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void resetColor(PrintStream out) {
        out.print(RESET_ALL);
    }
}