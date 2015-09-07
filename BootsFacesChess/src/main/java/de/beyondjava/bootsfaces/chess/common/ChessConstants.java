package de.beyondjava.bootsfaces.chess.common;

/**
 * Created with IntelliJ IDEA.
 * User: SoyYo
 * Date: 02.02.13
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public interface ChessConstants {
    public static final boolean s_WHITE = true;
    public static final boolean s_BLACK = false;
    public static final int B_EMPTY = 0;
    public static final int W_EMPTY = 1;
    public static final int B_PAWN = 2;
    public static final int W_PAWN = 4;
    public static final int B_ROOK = 6;
    public static final int W_ROOK = 8;
    public static final int B_KNIGHT = 10;
    public static final int W_KNIGHT = 12;
    public static final int B_BISHOP = 14;
    public static final int W_BISHOP = 16;
    public static final int B_QUEEN = 18;
    public static final int W_QUEEN = 20;
    public static final int B_KING = 22;
    public static final int W_KING = 24;
    public static final int[][] INITIAL_BOARD = {{6, 10, 14, 18, 22, 14, 10, 6},
            {2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {4, 4, 4, 4, 4, 4, 4, 4},
            {8, 12, 16, 20, 24, 16, 12, 8}
    };
    public static final int[] s_MATERIAL_VALUE = {0, 0, // empty fields
            100, 100, 100, 100, // pawns
            500, 500, 500, 500, // rooks,
            275, 275, 275, 275, // knights (Springer)
            325, 325, 325, 325, // bishops (Läufer)
            1000, 1000, 1000, 1000, // queens
            9999, 9999, 9999, 9999 // kings
    };
    public static final String[] COLUMNS = {"A", "B", "C", "D", "E", "F", "G", "H"};
    public static final String[] ROWS = {"8", "7", "6", "5", "4", "3", "2", "1"};
    public static final String[] PIECE_NAME = {"E" /* en passant */,
            " ", " ", "P", "P", "P", "P",
            "R", "R", "R", "R",
            "N", "N", "N", "N",
            "B", "B", "B", "B",
            "Q", "Q", "Q", "Q",
            "K", "K", "K", "K"};


    public static final int[][] POSITIONAL_VALUES =
                    {{0, 10, 20,  30,  30, 20, 10, 0},
                    {10, 20, 30,  40,  40, 30, 20, 10},
                    {15, 30, 60,  80,  80, 60, 30, 15},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {20, 40, 70, 100, 100, 70, 40, 20},
                    {15, 30, 40,  80,  80, 40, 30, 15},
                    {10, 20, 30,  40,  40, 30, 20, 10},
                    { 0, 10, 20,  30,  30, 20, 10, 0},
            };
    public static final int[][] WHITE_PAWN_POSITION_VALUES =
            {{200, 200, 200, 200, 200, 200, 200, 200},
                    {55, 70, 110, 180, 180, 110, 70, 55},
                    {45, 60, 90, 160, 160, 90, 60, 45},
                    {35, 50, 70, 130, 130, 70, 50, 35},
                    {20, 40, 50, 110, 110, 50, 40, 20},
                    {15, 30, 40, 50, 50, 40, 30, 15},
                    {20, 30, 60, 40, 40, 60, 40, 30},
                    {0, 0, 0, 0, 0, 0, 0, 0},
            };

    public static final int[] DOUBLE_PAWN_BONUS = {50 /* n/a */,50 /* A+B */,
            60 /* B+C */,
            70 /* C+D */,
            80 /* D+E */,
            70 /* E + F */,
            60 /* F + G */,
            50 /* G + H */};
}
