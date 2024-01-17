package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;  //returns the color it was given
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //first make an ArrayList, init position
        ArrayList<ChessMove> possMoves = new ArrayList<>();
        int initRow = myPosition.getRow();
        int initCol = myPosition.getColumn();
        //then based on the type, start looping through possible locations
        if (type == PieceType.BISHOP) {
            //loop through diagonally to the right
            int newRow = initRow + 1;
            int newCol = initCol + 1;
            while (newRow <= 8 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                    break;
                }
                //add to collection
                possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                    null));
                //increment both by one
                newRow++;
                newCol++;
            }
            //diagonally top left
            newRow = initRow + 1;
            newCol = initCol - 1;
            while (newRow <= 8 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) { //check if on team
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                    break;
                }
                //if not then add a move spot since it's empty
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), new ChessPosition(newRow, newCol),
                        null));
                newRow++;
                newCol--;
            }
            //bottom left
            newRow = initRow - 1;
            newCol = initCol - 1;
            while (newRow >= 1 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                    break;
                }
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), new ChessPosition(newRow, newCol),
                        null));
                newRow--;
                newCol--;
            }
            //bottom right
            newRow = initRow - 1;
            newCol = initCol + 1;
            while (newRow >= 1 && newCol <= 8 ) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                    break;
                }
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), new ChessPosition(newRow, newCol),
                        null));
                newRow--;
                newCol++;
            }

        }
        //return the arraylist
        return possMoves;
    }

}
