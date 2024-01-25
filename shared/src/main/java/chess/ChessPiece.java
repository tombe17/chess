package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private final HashSet<ChessMove> possMoves;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        possMoves = new HashSet<>();
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

        return pieceColor;
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
        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
            makeMove(board, myPosition, myPosition, 1,1,null,true);
            makeMove(board, myPosition, myPosition, 1,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,1,null,true);
        }
        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            makeMove(board, myPosition, myPosition, 1,0,null,true);
            makeMove(board, myPosition, myPosition, 0,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,0,null,true);
            makeMove(board, myPosition, myPosition, 0,1,null,true);
        }
        if (type == PieceType.KNIGHT) {
            makeMove(board, myPosition, myPosition, 2,1,null,false);
            makeMove(board, myPosition, myPosition, 2,-1,null,false);
            makeMove(board, myPosition, myPosition, -2,1,null,false);
            makeMove(board, myPosition, myPosition, -2,-1,null,false);
            makeMove(board, myPosition, myPosition, 1,2,null,false);
            makeMove(board, myPosition, myPosition, -1,2,null,false);
            makeMove(board, myPosition, myPosition, 1,-2,null,false);
            makeMove(board, myPosition, myPosition, -1,-2,null,false);
        }
        if (type == PieceType.KING) {
            makeMove(board, myPosition, myPosition, 1,0,null,false);
            makeMove(board, myPosition, myPosition, 1,-1,null,false);
            makeMove(board, myPosition, myPosition, 0,-1,null,false);
            makeMove(board, myPosition, myPosition, -1,-1,null,false);
            makeMove(board, myPosition, myPosition, -1,0,null,false);
            makeMove(board, myPosition, myPosition, -1,1,null,false);
            makeMove(board, myPosition, myPosition, 0,1,null,false);
            makeMove(board, myPosition, myPosition, 1,1,null,false);
        }
        if (type == PieceType.PAWN) {
            movePawn(board, myPosition);
        }

        return possMoves;
    }

    //function makeMove takes board, startPos, curPos, rowChange, colChange, and canRecurse
    public boolean makeMove(ChessBoard board, ChessPosition startPosition, ChessPosition curPosition, int rowChange, int colChange, PieceType promo, boolean canRecurse) {
        var checkPosition = new ChessPosition(curPosition.getRow() + rowChange, curPosition.getColumn() + colChange);
        //first check if in bounds
        if (checkPosition.getRow() > 8 || checkPosition.getRow() < 1) {
            return false;
        }
        if (checkPosition.getColumn() > 8 || checkPosition.getColumn() < 1) {
            return false;
        }
        //check if the spot is empty or if there's a piece
        if (!checkIfEmpty(board, checkPosition)) { // if there's a piece
            //if opposite team add to moves, else stop
            var checkPiece = board.getPiece(checkPosition);
            if (checkPiece.getTeamColor() != pieceColor) {
                possMoves.add(new ChessMove(startPosition, checkPosition, promo));
            }
            return false;
        }
        //make a move
        possMoves.add(new ChessMove(startPosition, checkPosition, promo));
        //if pawn, king, or knight stop
        if (!canRecurse) {
            return false;
        }

        return makeMove(board, startPosition, checkPosition, rowChange, colChange, promo, canRecurse);
    }
    //function checkIfEmpty to see if a spot is empty as a helper function
    public boolean checkIfEmpty(ChessBoard board, ChessPosition myPosition) {
        return board.getPiece(myPosition) == null; //true if empty, false if not
    }
    //function to help with promotion
    public void paProFun(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange) {
        makeMove(board, myPosition, myPosition, rowChange, colChange, PieceType.QUEEN, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, PieceType.ROOK, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, PieceType.BISHOP, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, PieceType.KNIGHT, false);
    }

    public void movePawn(ChessBoard board, ChessPosition myPosition) {
        //test version
        var currRow = myPosition.getRow();
        int moveDirection;
        int promoRow;
        int startRow;

        switch (pieceColor) {
            case WHITE -> {
                moveDirection = 1;
                promoRow = 7;
                startRow = 2;
            }
            case BLACK -> {
                moveDirection = -1;
                promoRow = 2;
                startRow = 7;
            }
            case null, default -> {
                return;
            }
        }

        //check in front
        var checkPosition = new ChessPosition(myPosition.getRow() + moveDirection, myPosition.getColumn());
        if (checkIfEmpty(board, checkPosition)) {
            //if so also check if you're about to promote
            if (currRow == promoRow) {
                paProFun(board, myPosition, moveDirection, 0);
            } else {
                makeMove(board, myPosition, myPosition, moveDirection, 0, null, false);
                if (currRow == startRow) {
                    //if starting on first row and not blocked add a second move forward if not blocked
                    int moveTwo = moveDirection * 2;
                    checkPosition = new ChessPosition(myPosition.getRow() + moveTwo, myPosition.getColumn());
                    if (checkIfEmpty(board, checkPosition)) {
                        makeMove(board, myPosition, myPosition, moveTwo, 0, null, false);
                    }
                }
            }
        }
        //check sides
        checkPawnSide(board, myPosition, moveDirection, -1, promoRow);
        checkPawnSide(board, myPosition, moveDirection, 1, promoRow);
    }

    public void checkPawnSide (ChessBoard board, ChessPosition myPosition, int moveDirection, int colChange, int promoRow) {
        var currRow = myPosition.getRow();
        var checkPosition = new ChessPosition(myPosition.getRow() + moveDirection, myPosition.getColumn() + colChange);
        if (!checkIfEmpty(board, checkPosition)) {
            var checkPiece = board.getPiece(checkPosition);
            if (checkPiece.pieceColor != pieceColor) {
                //check row
                if (currRow == promoRow) {
                    paProFun(board, myPosition, moveDirection, colChange);
                } else {
                    makeMove(board, myPosition, myPosition, moveDirection, colChange, null, false);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type && Objects.equals(possMoves, that.possMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, possMoves);
    }
}
