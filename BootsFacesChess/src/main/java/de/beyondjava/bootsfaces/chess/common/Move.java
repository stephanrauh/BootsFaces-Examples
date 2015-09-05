package de.beyondjava.bootsfaces.chess.common;

import static de.beyondjava.bootsfaces.chess.common.ChessConstants.*;

public class Move implements Comparable<Move> {
    public int fromColumn;
    public int fromRow;
    public int toColumn;
    public int toRow;
    public int materialValueAfterMove;
    public boolean opponentInCheck;
    public int positionalValue = 0;
    public boolean capture;
    public int piece;
    public int capturedPiece;
    public int boardAfterMove; // used by PrimitiveMoveGenerator
    public int moveValue; // value of the board after the move, according to the possible moves

    public Move(int piece, int fromRow, int fromColumn, int toRow, int toColumn, int materialValueAfterMove, boolean opponentInCheck, boolean capture, int capturedPiece) {
        this.piece = piece;
        this.fromColumn = fromColumn;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.toColumn = toColumn;
        this.materialValueAfterMove = materialValueAfterMove;
        this.opponentInCheck = opponentInCheck;
        this.capture = capture;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public int compareTo(Move o) {
        if (null == o) return -1;
        int m = o.materialValueAfterMove - materialValueAfterMove;
        int p = o.positionalValue - positionalValue;
        return -(m + p);
    }

    public String getNotation() {
        return getNotation(false);
    }

        public String getNotation(boolean enPassant) {
        String check = opponentInCheck ? "+" : " ";
        String s = capture ? "x" : "-";
        s += PIECE_NAME[capturedPiece + 1];
        if (piece==W_KING && fromRow==7 && toRow==7 && fromColumn==4 && toColumn==6)
        {
            return "0-0";
        }
        if (piece==W_KING && fromRow==7 && toRow==7 && fromColumn==4 && toColumn==2)
        {
            return "0-0-0";
        }
        if (piece==B_KING && fromRow==0 && toRow==0 && fromColumn==4 && toColumn==6)
        {
            return "0-0";
        }
        if (piece==B_KING && fromRow==0 && toRow==0 && fromColumn==4 && toColumn==2)
        {
            return "0-0-0";
        }
        String ep=enPassant?"e.p.":"";

        return PIECE_NAME[piece + 1] + COLUMNS[fromColumn] + ROWS[fromRow] + s + COLUMNS[toColumn] + ROWS[toRow] + ep + check;
    }

    public String toString() {
        String m = String.format("%5d", materialValueAfterMove);
        String p = String.format("%5d", positionalValue);
        String mv = String.format("%5d", moveValue);
        String s = String.format("%5d", materialValueAfterMove + positionalValue + moveValue);
        return getNotation() + " Value: M: " + m + " P: " + p + " Mv:" + mv + " Sum: " + s;
    }
}
