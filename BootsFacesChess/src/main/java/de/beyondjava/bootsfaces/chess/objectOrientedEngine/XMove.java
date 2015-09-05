package de.beyondjava.bootsfaces.chess.objectOrientedEngine;

import de.beyondjava.bootsfaces.chess.common.Move;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 14.02.13
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class XMove implements Comparable<XMove> {
    public int move;
    public int whiteMaterialValue;
    public int blackMaterialValue;
    /**
     * Value of black pieces threatened by white pieces
     */
    public int whitePotentialMaterialValue;
    /**
     * Value of white pieces threatened by black pieces
     */
    public int blackPotentialMaterialValue;
    public int whiteFieldPositionValue;
    public int blackFieldPositionValue;
    public int whiteMoveValue;
    public int blackMoveValue;
    public int whiteCoverageValue;
    public int blackCoverageValue;
    public boolean isWhiteKingThreatened;
    public boolean isBlackKingThreatened;
    public int whiteTotalValue;
    public int blackTotalValue;
    public int numberOfWhiteMoves;
    public int numberOfBlackMoves ;
    public int piece;
    public Chessboard boardAfterMove;
    public boolean stalemate;
    public boolean checkmate;

    @Override
    public int compareTo(XMove o) {
        return (whiteTotalValue-blackTotalValue) - (o.whiteTotalValue-o.blackTotalValue);
    }

    public String getNotation()
    {
        int fromRow = (move >> 12) & 0x000F;
        int fromColumn = (move >> 8) & 0x000F;
        int toRow = (move >> 4) & 0x000F;
        int toColumn = move & 0x000F;
        boolean capture = (move >> 24)>0;
        Move m = new Move(piece, fromRow, fromColumn, toRow, toColumn, 0, false, capture, 0 );
        return m.getNotation();
    }
    public String toString()
    {
        String wm = String.format(" WM: %4d", whiteMaterialValue);
        String bm = String.format(" BM: %4d", blackMaterialValue);
        String wpm = String.format(" WPM: %4d", whitePotentialMaterialValue);
        String bpm = String.format(" BPM: %4d", blackPotentialMaterialValue);
        String wf = String.format(" WF: %4d", whiteFieldPositionValue);
        String bf = String.format(" BF: %4d", blackFieldPositionValue);
        String wmv = String.format(" WMv: %4d", whiteMoveValue);
        String bmv = String.format(" Bmv: %4d", blackMoveValue);
        String wc = String.format(" WC: %4d", whiteCoverageValue);
        String bc = String.format(" BC: %4d", blackCoverageValue);
        String wt = String.format(" WT: %4d", whiteTotalValue);
        String bt = String.format(" BT: %4d ", blackTotalValue);
        String total = String.format("T: %6d ", whiteTotalValue-blackTotalValue);
        if ((piece&0x2)>0)
        {
            // black move
            total=String.format("T: %6d ", -whiteTotalValue+blackTotalValue);
        }
        return total + wm + wpm + bm + bpm + wf + bf + wc + bc + wt + bt + getNotation();
    }

}

