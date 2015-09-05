package de.beyondjava.bootsfaces.chess.common;

public class Piece implements ChessConstants {
	public int row;
	public int column;
	public int piece;

	public Piece(String s) {
		char color = s.charAt(0);
		char p = s.charAt(1);
		char c = s.charAt(2);
		char r = s.charAt(3);
		row = 56 - r; // note that row 8 is chessboard[0], row 1 is chessboard[7]
		column = c - 65;
		switch (p) {
		case 'P':
			piece = B_PAWN;
			break;
		case 'R':
			piece = B_ROOK;
			break;
		case 'B':
			piece = B_BISHOP;
			break;
		case 'N':
			piece = B_KNIGHT;
			break;
		case 'Q':
			piece = B_QUEEN;
			break;
		case 'K':
			piece = B_KING;
			break;
		default:
			System.out.println("Unknown piece");
		}
		if (color == 'W') {
			piece += 2;
		}
	}

	public int getRow() {
		return row;
	}
}
