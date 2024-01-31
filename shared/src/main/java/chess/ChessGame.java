package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currTeam;
    private ChessBoard board;
    public ChessGame() {
        currTeam = TeamColor.WHITE;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var myPiece = board.getPiece(startPosition);
        return myPiece == null ? null : myPiece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //First if there are exceptions throw it, else make the move
        var movePiece = board.getPiece(move.getStartPosition());
        //wrong team
        if (movePiece.getTeamColor() != currTeam) {
            throw new InvalidMoveException("Not your team.");
        }else if (!checkAttackPos(move.getStartPosition(), move.getEndPosition())) { //not a valid move
            throw new InvalidMoveException("Invalid move.");
        } else if (isInCheck(currTeam) && !willBlockCheck(move)) { //CHECK IF IN CHECK AND IF AFTER MOVE YOU WON'T BE IN CHECK
            throw new InvalidMoveException("Still in Check");
        } else {
            //using a given move it will move the piece to the new position and remove the old piece there
            //if promotion piece then make sure to change the piece
            if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(currTeam, move.getPromotionPiece()));
                board.removePiece(move.getStartPosition());
            } else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.removePiece(move.getStartPosition());
            }

            setTeamTurn(currTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingPos = board.getKingPosition(teamColor);
        //get list of oppTeam
        Collection<ChessPosition> oppTeamPositions = board.getTeamPositions(oppTeam);
        //check each piece to see if it's attacking King
        Collection<ChessMove> attackMoves;
        for (ChessPosition oppPosition : oppTeamPositions) {
            //get possible moves, see if an ending position will attack the king
            attackMoves = validMoves(oppPosition);
            for (ChessMove possMove : attackMoves) {
                if (possMove.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        //if yes return true, else return false
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //first check if in check
        if (isInCheck(teamColor)) {
            //then get each move the team can make and see if any of them can break it
            Collection<ChessPosition> myPositions = board.getTeamPositions(teamColor);
            Collection<ChessMove> possMoves;
            for (ChessPosition currPosition : myPositions) {
                possMoves = validMoves(currPosition);
                for (ChessMove currMove : possMoves) {
                    if (willBlockCheck(currMove)) {
                        return false; //if the move blocks check then return false
                    }
                }
            }
            return true; //if no moves can block, then in checkmate

        } else {
            return false;
        }

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //Condition - NOT in check, no movements besides the king, king can't move
        if (!isInCheck(teamColor)) {
            //get King's spot and iterate through your positions
            var kingPos = board.getKingPosition(teamColor);
            Collection<ChessPosition> piecePositions = board.getTeamPositions(teamColor);
            Collection<ChessMove> possMoves;

            for (ChessPosition curPos : piecePositions) {
                //if not King - is there any moves?
                possMoves = validMoves(curPos);
                if (!curPos.equals(kingPos)) {
                    if (!possMoves.isEmpty()) { //there are valid moves
                        return false;
                    }
                } else { //if King - loop through his moves and see if he can move
                    for (ChessMove curMove : possMoves) {
                        if (willBlockCheck(curMove)) { //move will still be in Check
                            return false; //The king can still move
                        }
                    }
                }

            }
        }
        //Only King left
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     *checks if a given piece has an end position
     * where another piece is
     */
    public boolean checkAttackPos(ChessPosition attackPos, ChessPosition defendPos) {
        //first get attackPos moves, if it exists then continue
        var attackMoves = validMoves(attackPos);
        if (attackMoves != null) {
            //iterate through moves and check if there
            for (ChessMove possMove : attackMoves) {
                if (possMove.getEndPosition().equals(defendPos)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     *returns true if the move blocks check
     * else returns false
     */
    public boolean willBlockCheck(ChessMove move) {
        var blockPiece = board.getPiece(move.getStartPosition());
        if (blockPiece != null) {
            ChessBoard tempBoard = board.copyBoard();
            //with our tempBoard we check the move and then see if the King is still in check
            tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
            tempBoard.removePiece(move.getStartPosition());

            Collection<ChessPosition> oppTeamPositions = tempBoard.getTeamPositions(getOppTeam());
            ChessPosition kingPos = tempBoard.getKingPosition(currTeam);
            Collection<ChessMove> attackMoves;

            for (ChessPosition currPosition : oppTeamPositions) {
                attackMoves = tempBoard.getPiece(currPosition).pieceMoves(tempBoard, currPosition);
                for (ChessMove possMove : attackMoves) {
                    if (possMove.getEndPosition().equals(kingPos)) {
                        return false; //can still be checked
                    }
                }

            }
            return true; //no attacks hit king
        }
        return false; //this move was null and didn't stop check
    }

    public TeamColor getOppTeam() {
        return getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return currTeam == chessGame.currTeam && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currTeam, board);
    }
}
