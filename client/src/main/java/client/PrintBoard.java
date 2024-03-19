package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class PrintBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final int LINE_WIDTH_IN_CHARS = 1;

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
            //draw side header
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




//
//    private static void drawHeaders(PrintStream out) {
//
//        setBlack(out);
//
//        String[] headers = { "TIC", "TAC", "TOE" };
//        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//            drawHeader(out, headers[boardCol]);
//
//            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
//            }
//        }
//
//        out.println();
//    }
//
//    private static void drawHeader(PrintStream out, String headerText) {
//        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
//        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;
//
//        out.print(EMPTY.repeat(prefixLength));
//        printHeaderText(out, headerText);
//        out.print(EMPTY.repeat(suffixLength));
//    }
//
//    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);
//
//        out.print(player);
//
//        setBlack(out);
//    }
//
//    private static void drawTicTacToeBoard(PrintStream out) {
//
//        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//
//            drawRowOfSquares(out);
//
//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                drawVerticalLine(out);
//                setBlack(out);
//            }
//        }
//    }
//
//    private static void drawRowOfSquares(PrintStream out) {
//
//        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_CHARS; ++squareRow) {
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                setWhite(out);
//
//                if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
//                    int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
//                    int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;
//
//                    out.print(EMPTY.repeat(prefixLength));
//                    printPlayer(out, rand.nextBoolean() ? X : O);
//                    out.print(EMPTY.repeat(suffixLength));
//                }
//                else {
//                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
//                }
//
//                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                    // Draw right line
//                    setRed(out);
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
//                }
//
//                setBlack(out);
//            }
//
//            out.println();
//        }
//    }
//
//    private static void drawVerticalLine(PrintStream out) {
//
//        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
//                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;
//
//        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
//            setRed(out);
//            out.print(EMPTY.repeat(boardSizeInSpaces));
//
//            setBlack(out);
//            out.println();
//        }
//    }
//
//    private static void setWhite(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void setRed(PrintStream out) {
//        out.print(SET_BG_COLOR_RED);
//        out.print(SET_TEXT_COLOR_RED);
//    }
//
//    private static void setBlack(PrintStream out) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_BLACK);
//    }
//
//    private static void printPlayer(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);
//
//        out.print(player);
//
//        setWhite(out);
//    }
//}
