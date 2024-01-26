package chess;

import java.util.Collection;
import java.util.HashSet;

/*
Assigns a set of rules based on what type of piece it is.
You give it the board, the piece type/team, and it's position,
 and it'll figure the rest out
 */
public class ChessRules {

    private final HashSet<ChessMove> possMoves;
    private final ChessGame.TeamColor pieceColor;
    ChessRules(ChessBoard board, ChessPiece.PieceType type, ChessGame.TeamColor pieceColor, ChessPosition myPosition) {
        this.pieceColor = pieceColor;
        this.possMoves = new HashSet<>();
        if (type == ChessPiece.PieceType.BISHOP || type == ChessPiece.PieceType.QUEEN) {
            makeMove(board, myPosition, myPosition, 1,1,null,true);
            makeMove(board, myPosition, myPosition, 1,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,1,null,true);
        }
        if (type == ChessPiece.PieceType.ROOK || type == ChessPiece.PieceType.QUEEN) {
            makeMove(board, myPosition, myPosition, 1,0,null,true);
            makeMove(board, myPosition, myPosition, 0,-1,null,true);
            makeMove(board, myPosition, myPosition, -1,0,null,true);
            makeMove(board, myPosition, myPosition, 0,1,null,true);
        }
        if (type == ChessPiece.PieceType.KNIGHT) {
            makeMove(board, myPosition, myPosition, 2,1,null,false);
            makeMove(board, myPosition, myPosition, 2,-1,null,false);
            makeMove(board, myPosition, myPosition, -2,1,null,false);
            makeMove(board, myPosition, myPosition, -2,-1,null,false);
            makeMove(board, myPosition, myPosition, 1,2,null,false);
            makeMove(board, myPosition, myPosition, -1,2,null,false);
            makeMove(board, myPosition, myPosition, 1,-2,null,false);
            makeMove(board, myPosition, myPosition, -1,-2,null,false);
        }
        if (type == ChessPiece.PieceType.KING) {
            makeMove(board, myPosition, myPosition, 1,0,null,false);
            makeMove(board, myPosition, myPosition, 1,-1,null,false);
            makeMove(board, myPosition, myPosition, 0,-1,null,false);
            makeMove(board, myPosition, myPosition, -1,-1,null,false);
            makeMove(board, myPosition, myPosition, -1,0,null,false);
            makeMove(board, myPosition, myPosition, -1,1,null,false);
            makeMove(board, myPosition, myPosition, 0,1,null,false);
            makeMove(board, myPosition, myPosition, 1,1,null,false);
        }
        if (type == ChessPiece.PieceType.PAWN) {
            movePawn(board, myPosition);
        }
    }

    public Collection<ChessMove> getMoves() {
        return possMoves;
    }

    //function makeMove takes board, startPos, curPos, rowChange, colChange, and canRecurse
    public boolean makeMove(ChessBoard board, ChessPosition startPosition, ChessPosition curPosition, int rowChange, int colChange, ChessPiece.PieceType promo, boolean canRecurse) {
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
        makeMove(board, myPosition, myPosition, rowChange, colChange, ChessPiece.PieceType.QUEEN, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, ChessPiece.PieceType.ROOK, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, ChessPiece.PieceType.BISHOP, false);
        makeMove(board, myPosition, myPosition, rowChange, colChange, ChessPiece.PieceType.KNIGHT, false);
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
            if (checkPiece.getTeamColor() != pieceColor) {
                //check row
                if (currRow == promoRow) {
                    paProFun(board, myPosition, moveDirection, colChange);
                } else {
                    makeMove(board, myPosition, myPosition, moveDirection, colChange, null, false);
                }
            }
        }
    }
}
