package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
        HashSet<ChessMove> possMoves = new HashSet<>();
        int initRow = myPosition.getRow();
        int initCol = myPosition.getColumn();
        //then based on the type, start looping through possible locations
        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
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

        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            //go to the right
            int rookCol = initCol + 1;
            while (rookCol <= 8) {
                ChessPosition newPosition = new ChessPosition(initRow, rookCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                    break;
                }
                //add to collection
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                null));
                rookCol++;
            }
            //go left
            rookCol = initCol - 1;
            while (rookCol > 0) {
                ChessPosition newPosition = new ChessPosition(initRow, rookCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                    break;
                }
                //add to collection
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                        null));
                rookCol--;
            }
            //go UP
            int rookRow = initRow + 1;
            while (rookRow <= 8) {
                ChessPosition newPosition = new ChessPosition(rookRow, initCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                    break;
                }
                //add to collection
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                        null));
                rookRow++;
            }
            //go down
            rookRow = initRow - 1;
            while (rookRow > 0) {
                ChessPosition newPosition = new ChessPosition(rookRow, initCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                    break;
                }
                //add to collection
                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                        null));
                rookRow--;
            }
        }

        if (type == PieceType.KNIGHT) {
            //go two rows up and then one to the left and right
            var currRow = initRow + 2;
            if (currRow <= 8) { //first check if that goes out of bounds
                var currCol = initCol - 1;
                if (currCol > 0) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else { //if not still add a spot cuz it's valid
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
                currCol = initCol + 1;
                if (currCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
            }
            //go down 2, then left and right 1
            currRow = initRow - 2;
            if (currRow > 0) { //first check if that goes out of bounds
                var currCol = initCol - 1;
                if (currCol > 0) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else { //if not still add a spot cuz it's valid
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
                currCol = initCol + 1;
                if (currCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
            }
            //go left 2, up and down 1
            var currCol = initCol - 2;
            if (currCol > 0) { //first check if that goes out of bounds
                currRow = initRow - 1;
                if (currRow > 0) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else { //if not still add a spot cuz it's valid
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
                currRow = initRow + 1;
                if (currRow <= 8) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
            }
            //go right 2
            currCol = initCol + 2;
            if (currCol <= 8) { //first check if that goes out of bounds
                currRow = initRow - 1;
                if (currRow > 0) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else { //if not still add a spot cuz it's valid
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
                currRow = initRow + 1;
                if (currRow <= 8) {
                    ChessPosition newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow,initCol), newPosition,
                                null));
                    }
                }
            }
        }

        if (type == PieceType.KING) {
            //go up, then left and right
            var currRow = initRow + 1;
            if (currRow <= 8) {
                ChessPosition newPosition = new ChessPosition(currRow, initCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                } else {
                    possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                            null));
                }
                //go left and right and repeat
                var currCol = initCol - 1;
                if (currCol > 0) {
                    newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                }
                currCol = initCol + 1;
                if (currCol <= 8) {
                    newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                }
            }
            //go down, left and right
            currRow = initRow - 1;
            if (currRow > 0) {
                ChessPosition newPosition = new ChessPosition(currRow, initCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                } else {
                    possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                            null));
                }
                //go left and right and repeat
                var currCol = initCol - 1;
                if (currCol > 0) {
                    newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                }
                currCol = initCol + 1;
                if (currCol <= 8) {
                    newPosition = new ChessPosition(currRow, currCol);
                    //check if there is a piece there
                    if (board.getPiece(newPosition) != null) {
                        var currPiece = board.getPiece(newPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                    null));
                        }
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                }
            }
            //go left, go right
            var currCol = initCol - 1;
            if (currCol > 0) {
                ChessPosition newPosition = new ChessPosition(initRow, currCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                } else {
                    possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                            null));
                }
            }
            currCol = initCol + 1;
            if (currCol <= 8) {
                ChessPosition newPosition = new ChessPosition(initRow, currCol);
                //check if there is a piece there
                if (board.getPiece(newPosition) != null) {
                    var currPiece = board.getPiece(newPosition);
                    if (currPiece.getTeamColor() != this.pieceColor) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                                null));
                    }
                } else {
                    possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition,
                            null));
                }
            }
        }

        if (type == PieceType.PAWN) {//pawn
            var team = getTeamColor();
            if (team == ChessGame.TeamColor.WHITE) {
                var currRow = initRow + 1;
                ChessPosition newPosition = new ChessPosition(currRow, initCol);

                if (CheckIfEmpty(board, newPosition)) {
                    if (currRow == 8) { //add move where they get promoted
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.BISHOP));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.QUEEN));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.ROOK));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.KNIGHT));
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, null));
                    }
                    if (initRow == 2) { //starting row for white give an extra space
                        currRow++;
                        ChessPosition twoPosition = new ChessPosition(currRow, initCol);
                        if (CheckIfEmpty(board, twoPosition)) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), twoPosition, null));
                        }
                    }
                }
                //check to sides to attack
                currRow = initRow + 1;
                boolean promo = currRow == 8;
                var leftCol = initCol - 1;
                var rightCol = initCol + 1;
                if (leftCol > 0) { //go left, check if there is a piece to attack, and then get the team
                    ChessPosition leftPosition = new ChessPosition(currRow, leftCol);

                    if (!CheckIfEmpty(board, leftPosition)) {
                        var currPiece = board.getPiece(leftPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            if (promo) {
                                //add promo moves
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.BISHOP));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.QUEEN));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.ROOK));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.KNIGHT));
                            } else {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition,
                                        null));
                            }
                        }
                    }
                }
                if (rightCol <= 8) { //go right, check if there is a piece to attack, and then get the team
                    ChessPosition rightPosition = new ChessPosition(currRow, rightCol);
                    if (!CheckIfEmpty(board, rightPosition)) {
                        var currPiece = board.getPiece(rightPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            if (promo) {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.BISHOP));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.QUEEN));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.ROOK));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.KNIGHT));
                            } else {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition,
                                        null));
                            }
                        }
                    }
                }

            } else {    //can assume it's black team
                var currRow = initRow - 1;
                ChessPosition newPosition = new ChessPosition(currRow, initCol);
                //check right in front of the pawn for movement
                if (CheckIfEmpty(board, newPosition)) {
                    if (currRow == 1) {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.BISHOP));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.QUEEN));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.ROOK));
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, PieceType.KNIGHT));
                    } else {
                        possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), newPosition, null));
                    }

                    if (initRow == 7) { //starting row for white give an extra space
                        currRow--;
                        ChessPosition twoPosition = new ChessPosition(currRow, initCol);

                        if (CheckIfEmpty(board, twoPosition)) {
                            possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), twoPosition, null));
                        }
                    }
                }
                //check to sides to attack
                currRow = initRow - 1;
                boolean promo = currRow == 1;
                var leftCol = initCol - 1;
                var rightCol = initCol + 1;
                if (leftCol > 0) { //go left, check if there is a piece to attack, and then get the team
                    ChessPosition leftPosition = new ChessPosition(currRow, leftCol);
                    if (!CheckIfEmpty(board, leftPosition)) {
                        var currPiece = board.getPiece(leftPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            if (promo) {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.BISHOP));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.QUEEN));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.ROOK));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition, PieceType.KNIGHT));
                            } else {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), leftPosition,
                                        null));
                            }
                        }
                    }
                }
                if (rightCol <= 8) { //go right, check if there is a piece to attack, and then get the team
                    ChessPosition rightPosition = new ChessPosition(currRow, rightCol);
                    if (!CheckIfEmpty(board, rightPosition)) {
                        var currPiece = board.getPiece(rightPosition);
                        if (currPiece.getTeamColor() != this.pieceColor) {
                            if (promo) {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.BISHOP));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.QUEEN));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.ROOK));
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition, PieceType.KNIGHT));
                            } else {
                                possMoves.add(new ChessMove(new ChessPosition(initRow, initCol), rightPosition,
                                        null));
                            }
                        }
                    }
                }

            }
        }
        //return the arraylist
        return possMoves;
    }

    public boolean CheckIfEmpty(ChessBoard board, ChessPosition checkPosition) { //return true if empty, false if not
        return board.getPiece(checkPosition) == null;
    }


}
