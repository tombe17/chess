package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class PrintBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;


    public PrintBoard() {

    }

    public static void print() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawBoard(out);
        drawHeaders(out);

        out.println();
    }

    private static void drawHeaders(PrintStream out) {
        setGray(out);
        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h" };
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

    private static void drawBoard(PrintStream out) {
        String[] sideHeaders = { "8", "7", "6", "5", "4", "3", "2", "1"};
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