package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //for reset first clear board then cycle through
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        populateRoyals(ChessGame.TeamColor.BLACK);
        populateRoyals(ChessGame.TeamColor.WHITE);
        populatePawns(ChessGame.TeamColor.BLACK);
        populatePawns(ChessGame.TeamColor.WHITE);
    }

    public void removePiece(ChessPosition remPosition) {
        board[remPosition.getRow() - 1][remPosition.getColumn() - 1] = null;
    }

    public Collection<ChessPosition> getTeamPositions(ChessGame.TeamColor team) {
        HashSet<ChessPosition> teamPositions = new HashSet<>();
        ChessPiece curPiece;
        //iterate through array and check each spot if it's on the opp team
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                curPiece = board[i][j];
                if (curPiece != null && curPiece.getTeamColor() == team) {
                    teamPositions.add(new ChessPosition(i + 1,j + 1));
                }
            }
        }
        //return list of opp team positions
        return teamPositions;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor kingTeam) {
        Collection<ChessPosition> teamPositions = getTeamPositions(kingTeam);
        //iterate through and return the king position
        for (ChessPosition curPosition : teamPositions) {
            if (getPiece(curPosition).getPieceType() == ChessPiece.PieceType.KING) {
                return curPosition;
            }
        }
        //couldn't find king and so is wrong
        return null;
    }

    //have helper functions for adding royalty and pawns
    public void populateRoyals(ChessGame.TeamColor team) {
        int row;
        if (team == ChessGame.TeamColor.BLACK) {
            row = 8;
        } else {
            row = 1;
        }
        addPiece(new ChessPosition(row, 1), new ChessPiece(team, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 8), new ChessPiece(team, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 7), new ChessPiece(team, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 6), new ChessPiece(team, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(team, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(team, ChessPiece.PieceType.KING));
    }

    public void populatePawns(ChessGame.TeamColor team) {
        int row;
        if (team == ChessGame.TeamColor.WHITE) {
            row = 2;
        } else {
            row = 7;
        }
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(row, i + 1), new ChessPiece(team, ChessPiece.PieceType.PAWN));
        }
    }

    public ChessBoard copyBoard() {
        ChessBoard copyBoard = new ChessBoard();
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var position = new ChessPosition(i, j);
                var currPiece = getPiece(position);
                if (currPiece != null) {
                copyBoard.addPiece(position, new ChessPiece(currPiece.getTeamColor(),currPiece.getPieceType()));
                }
            }
        }
        return copyBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
