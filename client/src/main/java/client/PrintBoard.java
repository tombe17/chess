package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class PrintBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private final ChessGame.TeamColor teamColor;
    private static ChessGame game;
    private final String[] blackHeaders = { "h", "g", "f", "e", "d", "c", "b", "a" };
    private final String[] blackSideHeaders = { "1", "2", "3", "4", "5", "6", "7", "8" };
    private final String[] whiteHeaders = { "a", "b", "c", "d", "e", "f", "g", "h" };
    private final String[] whiteSideHeaders = { "8", "7", "6", "5", "4", "3", "2", "1" };

    public PrintBoard(ChessGame.TeamColor teamColor, ChessGame game) {
        this.teamColor = teamColor;
        PrintBoard.game = game;
    }

    public void print() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (teamColor != null ) {
            if (this.teamColor.equals(ChessGame.TeamColor.BLACK)) {
                printBlack(out);
            } else {
                printWhite(out);
            }
        } else {
            printWhite(out);
        }
    }

    private void printWhite(PrintStream out) {
        out.print(ERASE_SCREEN);
        drawHeaders(out, whiteHeaders);
        drawBoard(out, whiteSideHeaders, true);
        drawHeaders(out, whiteHeaders);
    }

    private void printBlack(PrintStream out) {
        out.print(ERASE_SCREEN);

        drawHeaders(out, blackHeaders);
        drawBoard(out, blackSideHeaders, false);
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

    private static void drawBoard(PrintStream out, String[] sideHeaders, boolean forWhite) {
        if (forWhite) {
            int headerRow = 0;
            for (int boardRow = BOARD_SIZE_IN_SQUARES; boardRow > 0; boardRow--) {
                setGray(out);

                drawHeader(out, sideHeaders[headerRow]);
                drawSquares(out, boardRow, true);

                setGray(out);
                drawHeader(out, sideHeaders[headerRow]);

                headerRow++;
                resetColor(out);
                out.println();
            }
        } else {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++) {
                setGray(out);

                drawHeader(out, sideHeaders[boardRow]);
                drawSquares(out, boardRow, false);

                setGray(out);
                drawHeader(out, sideHeaders[boardRow]);

                resetColor(out);
                out.println();
            }
        }
    }

    private static void drawSquares(PrintStream out, int rowNum, boolean forWhite) {
        var colorFirst = SET_BG_COLOR_WHITE;
        var colorSecond = SET_BG_COLOR_BLACK;
        ChessPiece myPiece;

        if (rowNum % 2 != 0) {
            colorFirst = SET_BG_COLOR_BLACK;
            colorSecond = SET_BG_COLOR_WHITE;
        }

        if (forWhite) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
                if (boardCol % 2 == 0) {
                    out.print(colorFirst);
                } else {
                    out.print(colorSecond);
                }
                myPiece = game.getBoard().getPiece(new ChessPosition(rowNum, boardCol + 1));
                out.print(SPACE);
                printPiece(out, myPiece);
                out.print(SPACE);
            }
        } else {
            for (int boardCol = BOARD_SIZE_IN_SQUARES; boardCol > 0; boardCol--) {
                if (boardCol % 2 == 0) {
                    out.print(colorFirst);
                } else {
                    out.print(colorSecond);
                }
                myPiece = game.getBoard().getPiece(new ChessPosition(rowNum + 1, boardCol));
                out.print(SPACE);
                printPiece(out, myPiece);
                out.print(SPACE);
            }
        }
    }

    private static void printPiece(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            if (piece.getTeamColor() == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_RED);
            }
            else {
                out.print(SET_TEXT_COLOR_BLUE);
            }
            switch (piece.getPieceType()) {
                case PAWN -> out.print("P");
                case KING -> out.print("K");
                case QUEEN -> out.print("Q");
                case BISHOP -> out.print("B");
                case KNIGHT -> out.print("N");
                case ROOK -> out.print("R");
                default -> out.print(SPACE);
            }
        } else {
            out.print(SPACE);
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