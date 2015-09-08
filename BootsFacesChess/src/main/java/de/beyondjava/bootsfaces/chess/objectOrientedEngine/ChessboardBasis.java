package de.beyondjava.bootsfaces.chess.objectOrientedEngine;

import de.beyondjava.bootsfaces.chess.common.ChessConstants;
import de.beyondjava.bootsfaces.chess.common.Move;
import de.beyondjava.bootsfaces.chess.common.Piece;
import de.beyondjava.bootsfaces.chess.exceptions.BlackIsCheckMateException;
import de.beyondjava.bootsfaces.chess.exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.exceptions.WhiteIsCheckMateException;

/**
 * Represents the chess board and provide a couple of methods on possible moves.
 * Date: 02.02.13 Time: 19:17
 */
public class ChessboardBasis implements ChessConstants {
	public int moveCount = 0;
	public static int evaluatedPositions = 0; // DEBUG / statistics
	public static long totalTime = 0; // DEBUG / statistics
	public static long realTimeOfCalculation; // DEBUG / statistics

	public final int[][] board;
	public final boolean activePlayerIsWhite;
	public byte[][] canBeReachedByWhitePiece = new byte[8][8];
	public byte[][] canBeReachedByBlackPiece = new byte[8][8];
	public int whiteMaterialValue; // effectively final
	public int blackMaterialValue; // effectively final
	/**
	 * Value of black pieces threatened by white pieces
	 */
	public int whitePotentialMaterialValue; // effectively final
	/**
	 * Value of white pieces threatened by black pieces
	 */
	public int blackPotentialMaterialValue; // effectively final
	public int whiteFieldPositionValue; // effectively final
	public int blackFieldPositionValue; // effectively final
	public int whiteMoveValue;
	public int blackMoveValue;
	public int whiteCoverageValue;
	public int blackCoverageValue;
	public boolean isWhiteKingThreatened;
	public boolean isBlackKingThreatened;
	public int whiteTotalValue; // effectively final
	public int blackTotalValue; // effectively final
	public int[] whiteMoves = new int[184]; // effectively final
	public int numberOfWhiteMoves = 0; // effectively final
	public int[] blackMoves = new int[184]; // effectively final
	public int numberOfBlackMoves = 0; // effectively final
	public boolean stalemate = false;
	public boolean checkmate = false;
	public boolean whiteCanCastleLeft = true;
	public boolean whiteCanCastleRight = true;
	public boolean whiteHasCastled = false;
	public boolean blackCanCastleLeft = true;
	public boolean blackCanCastleRight = true;
	public boolean blackHasCastled = false;

	private ChessboardBasis(boolean activePlayerIsWhite, int[][] board) {
		this.activePlayerIsWhite = activePlayerIsWhite;
		this.board = board;
		if (activePlayerIsWhite) {
			moveCount++;
		}
		evaluateBoard();
	}

	public ChessboardBasis() {
		this(true, ChessConstants.INITIAL_BOARD);
	}

	public ChessboardBasis(boolean activePlayerIsWhite, ChessboardBasis board) {
		this(activePlayerIsWhite, board.board);
	}

	public ChessboardBasis(boolean activePlayerIsWhite, Piece... pieces) {
		this(activePlayerIsWhite, getBoardFromPieces(pieces));
	}

	public ChessboardBasis(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn,
			int promotedPiece) {
		this(!oldBoard.activePlayerIsWhite, getNewBoard(oldBoard, fromRow, fromColumn, toRow, toColumn, promotedPiece));
		whiteCanCastleLeft = oldBoard.whiteCanCastleLeft;
		whiteCanCastleRight = oldBoard.whiteCanCastleRight;
		whiteHasCastled = oldBoard.whiteHasCastled;
		blackCanCastleLeft = oldBoard.blackCanCastleLeft;
		blackCanCastleRight = oldBoard.blackCanCastleRight;
		blackHasCastled = oldBoard.blackHasCastled;
		int piece = oldBoard.board[fromRow][fromColumn];
		if ((piece == B_KING && fromRow == 0)) {
			if (fromColumn == 4 && toColumn == 6) {
				blackHasCastled = true;
			}
			if (fromColumn == 4 && toColumn == 2) {
				blackHasCastled = true;
			}
		}
		if ((piece == W_KING && fromRow == 7)) {
			if (fromColumn == 4 && toColumn == 6) {
				whiteHasCastled = true;
			}
			if (fromColumn == 4 && toColumn == 2) {
				whiteHasCastled = true;
			}
		}
		if (piece==B_KING) {
			blackCanCastleLeft=false;
			blackCanCastleRight=false;
		}
		if (piece==B_ROOK) {
			if (fromColumn==0)
				blackCanCastleLeft=false;
			if (fromColumn==7)
				blackCanCastleRight=false;
		}
		if (piece==W_KING) {
			whiteCanCastleLeft=false;
			whiteCanCastleRight=false;
		}
		if (piece==W_ROOK) {
			if (fromColumn==0)
				whiteCanCastleLeft=false;
			if (fromColumn==7)
				whiteCanCastleRight=false;
		}
		whiteTotalValue += (whiteCanCastleRight ? 400 : 0) +(whiteCanCastleLeft ? 400 : 0) + (whiteHasCastled ? 1000 : 0);
		blackTotalValue += (blackCanCastleRight ? 400 : 0)+ (blackCanCastleLeft ? 400 : 0) + (blackHasCastled ? 1000 : 0);
	}

	private static int[][] getNewBoard(ChessboardBasis oldBoard, int fromRow, int fromColumn, int toRow, int toColumn,
			int promotedPiece) {
		int[][] newBoard = new int[8][8];
		for (int row = 0; row < 8; row++) {
			newBoard[row] = new int[8];
			for (int y = 0; y < 8; y++) {
				int piece = oldBoard.getChessPiece(row, y);
				if (piece < 0)
					piece = 0; // forget that en passant once was possible
				newBoard[row][y] = piece;
			}
		}

		if (oldBoard.getChessPiece(toRow, toColumn) == -1) {
			// capture en passant
			newBoard[fromRow][toColumn] = 0;
		}
		int piece = newBoard[fromRow][fromColumn];
		if (piece == W_PAWN) {
			if (fromRow - toRow == 2)
				newBoard[fromRow - 1][toColumn] = -1; // allow for en passant
			if (toRow == 0) {
				// promotion
				if (promotedPiece > 0) {
					piece = promotedPiece;
				} else
					piece = W_QUEEN;
			}
		}
		if (piece == B_PAWN) {
			if (fromRow - toRow == -2)
				newBoard[fromRow + 1][toColumn] = -1; // allow for en passant
			if (toRow == 7) {
				// promotion
				if (promotedPiece > 0) {
					piece = promotedPiece;
				} else
					piece = B_QUEEN;
			}
		}
		if ((piece == W_KING && fromRow == 7) || (piece == B_KING && fromRow == 0)) {
			if (fromColumn == 4 && toColumn == 6) {
				// castling right hand side
				newBoard[fromRow][5] = newBoard[fromRow][7];
				newBoard[fromRow][7] = 0;
			}
			if (fromColumn == 4 && toColumn == 2) {
				// castling left hand side
				newBoard[fromRow][3] = newBoard[fromRow][0];
				newBoard[fromRow][0] = 0;
			}
		}
		newBoard[toRow][toColumn] = piece;
		newBoard[fromRow][fromColumn] = 0;
		return newBoard;
	}

	private static int[][] getBoardFromPieces(Piece[] pieces) {
		int[][] board = new int[8][8];
		for (Piece p : pieces) {
			board[p.row][p.column] = p.piece;
		}
		return board;
	}

	public void evaluateBoard() {
		long timer = System.nanoTime();
		evaluateMaterialValue();
		evaluateFieldPositionalValue();
		findLegalMovesIgnoringCheck();
		evaluateThreats();
		whiteTotalValue = whiteMaterialValue * 10 + (whitePotentialMaterialValue >> 2) + whiteFieldPositionValue
				+ whiteMoveValue + whiteCoverageValue;
		// The evaluation of castling has been moved to the constructor of ChessboardBasis!
		blackTotalValue = blackMaterialValue * 10 + (blackPotentialMaterialValue >> 2) + blackFieldPositionValue
				+ blackMoveValue;
		// The evaluation of castling has been moved to the constructor of ChessboardBasis!
		evaluatedPositions++;
		long dauer = System.nanoTime() - timer;
		totalTime += dauer;
	}

	public int getChessPiece(int row, int column) {
		return board[row][column];
	}

	public Chessboard moveChessPiece(int fromRow, int fromColumn, int toRow, int toColumn, int promotedPiece) {
		return new Chessboard(this, fromRow, fromColumn, toRow, toColumn, promotedPiece);
	}

	public Chessboard moveChessPiece(Move move) {
		return new Chessboard(this, move.fromRow, move.fromColumn, move.toRow, move.toColumn, 0);
	}

	protected boolean isWhitePiece(int piece) {
		if (piece < 2) {
			return false;
		}
		return (((piece / 2) % 2) == 0);
	}

	protected boolean isBlackPiece(int piece) {
		if (piece < 2) {
			return false;
		}
		return (((piece / 2) % 2) == 1);
	}

	public boolean isActivePlayersPiece(int piece) {
		return activePlayerIsWhite == isWhitePiece(piece);
	}

	protected boolean isInsideBoard(int row, int column) {
		if (row < 0 || row > 7)
			return false;
		if (column < 0 || column > 7)
			return false;
		return true;
	}

	protected boolean isEmptyField(int row, int column) {
		if (!isInsideBoard(row, column))
			return false;
		if (getChessPiece(row, column) <= 0)
			return true;
		return false;
	}

	protected boolean isOpponentsPiece(int row, int column, boolean p_activePlayerIsWhite) {
		if (!isInsideBoard(row, column))
			return false;
		if (getChessPiece(row, column) <= 0)
			return false;
		if (p_activePlayerIsWhite) {
			return (isBlackPiece(getChessPiece(row, column)));
		} else {
			return (isWhitePiece(getChessPiece(row, column)));
		}
	}

	protected boolean isEmptyOrCanBeCaptured(int row, int column, boolean p_activePlayerIsWhite) {
		if (!isInsideBoard(row, column))
			return false;
		int piece = getChessPiece(row, column);
		if (piece <= 0)
			return true;
		if (p_activePlayerIsWhite) {
			return (isBlackPiece(piece));
		} else {
			return (isWhitePiece(piece));
		}
	}

	private void evaluateMaterialValue() {
		int whiteValue = 0;
		int blackValue = 0;
		for (int fromRow = 0; fromRow < 8; fromRow++)
			for (int toColumn = 0; toColumn < 8; toColumn++) {
				int piece = getChessPiece(fromRow, toColumn);
				if (isWhitePiece(piece)) {
					whiteValue += s_MATERIAL_VALUE[piece];
				}
				if (isBlackPiece(piece)) {
					blackValue += s_MATERIAL_VALUE[piece];
				}
			}
		whiteMaterialValue = whiteValue - 13999; // normalization (13999=initial
													// sum)
		blackMaterialValue = blackValue - 13999; // normalization (13999=initial
													// sum)
	}

	public void evaluateValueOfLegalMoves() {
		int whiteValue = numberOfWhiteMoves;
		for (int i = 0; i < whiteValue; i++) {
			int gain = whiteMoves[i] >> 24;
			whiteValue += gain / 10;
		}
		whiteMoveValue = whiteValue;
		int blackValue = numberOfBlackMoves;
		for (int i = 0; i < blackValue; i++) {
			int gain = blackMoves[i] >> 24;
			blackValue += gain / 10;
		}
		blackMoveValue = blackValue;
	}

	private void evaluateFieldPositionalValue() {
		int whiteValue = 0;
		int blackValue = 0;
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				int piece = getChessPiece(row, col);
				if (piece == W_PAWN) {
					whiteValue += WHITE_PAWN_POSITION_VALUES[row][col];
				} else if (piece == B_PAWN) {
					blackValue += WHITE_PAWN_POSITION_VALUES[7 - row][col];
				} else if (isWhitePiece(piece)) {
					whiteValue += POSITIONAL_VALUES[row][col];
				} else if (isBlackPiece(piece)) {
					blackValue += POSITIONAL_VALUES[row][col];
				}
			}
		whiteFieldPositionValue = whiteValue;
		blackFieldPositionValue = blackValue;
	}

	private void evaluateThreats() {
		// int whiteValue = 0;
		// int blackValue = 0;
		// int whiteAdvantage = activePlayerIsWhite ? 1 : -1;
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				whiteCoverageValue += whitePieceReach(row, col);
				blackCoverageValue += blackPieceReach(row, col);
				/*
				 * int piece = board[row][col]; if (piece != 0) { if (piece < 0)
				 * { if (activePlayerIsWhite) piece = B_PAWN; else piece =
				 * W_PAWN; } if (isWhitePiece(piece)) { if
				 * (canBeReachedByTheseBlackPieces[row][col] > 0) { if
				 * (canBeReachedByTheseBlackPieces[row][col] >
				 * canBeReachedByTheseWhitePieces[row][col] + whiteAdvantage) {
				 * // piece is very likely to be captured
				 * blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 1; } else if (canBeReachedByTheseBlackPieces[row][col] >=
				 * canBeReachedByTheseWhitePieces[row][col] + whiteAdvantage) {
				 * // it's likely to became an equal exchange
				 * blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 2; } else { // piece might get lost if something goes wrong
				 * blackPotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 3; } } } else { if (canBeReachedByTheseWhitePieces[row][col]
				 * > 0) { if (canBeReachedByTheseBlackPieces[row][col] <
				 * canBeReachedByTheseWhitePieces[row][col] - whiteAdvantage) {
				 * // piece is very likely to be captured
				 * whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 1; } else if (canBeReachedByTheseBlackPieces[row][col] <=
				 * canBeReachedByTheseWhitePieces[row][col] - whiteAdvantage) {
				 * // it's likely to became an equal exchange
				 * whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 2; } else { // piece might get lost if something goes wrong
				 * whitePotentialMaterialValue += (s_MATERIAL_VALUE[piece]) >>
				 * 3; } } } }
				 */
			}
	}

	public void findLegalMovesIgnoringCheck() {
		numberOfBlackMoves=0;
		numberOfWhiteMoves=0;
		for (int fromRow = 0; fromRow < 8; fromRow++)
			for (int fromColumn = 0; fromColumn < 8; fromColumn++) {
				int piece = getChessPiece(fromRow, fromColumn);
				if (piece >= 2) {
					boolean pieceIsWhite = isWhitePiece(piece);
					switch ((piece + 2) >> 2) {
					case 1: /* Pawn */
						getLegalMovesForAPawn(fromRow, fromColumn, pieceIsWhite);
						break;
					case 2: /* Rook */
						getLegalMovesForARook(fromRow, fromColumn, pieceIsWhite);
						break;
					case 3: /* knight (Springer) */
						getLegalMovesForAKnight(fromRow, fromColumn, pieceIsWhite);
						break;
					case 4: /* bishop (Laeufer) */
						getLegalMovesForABishop(fromRow, fromColumn, pieceIsWhite);
						break;
					case 5: /* Queen */
						getLegalMovesForAQueen(fromRow, fromColumn, pieceIsWhite);
						break;
					case 6: /* King */
						getLegalMovesForAKing(fromRow, fromColumn, pieceIsWhite);
						break;
					}

				}
			}
	}

	private void getLegalMovesForAPawn(int row, int column, boolean pieceIsWhite) {
		int nr;
		int nc;
		int direction = pieceIsWhite ? -1 : 1;

		// simple move forward
		nr = row + direction;
		nc = column;
		if (nr < 0 || nr >= 8) {
			return;
		}
		if (isInsideBoard(nr, nc) && isEmptyField(nr, nc)) {
			if (nr == 0 && pieceIsWhite) {
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, B_KNIGHT);
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, B_BISHOP);
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, B_ROOK);
				addPossibleMove(row, column, nr, nc, pieceIsWhite, B_QUEEN);
			} else if (nr == 7 && (!pieceIsWhite)) {
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, W_KNIGHT);
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, W_BISHOP);
				// addPossibleMove(row, column, nr, nc, pieceIsWhite, W_ROOK);
				addPossibleMove(row, column, nr, nc, pieceIsWhite, W_QUEEN);
			} else {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
			}
			// double move forward
			if ((row == 6 && pieceIsWhite) || (row == 1 && (!pieceIsWhite))) {
				// first move
				nr = row + 2 * direction;
				nc = column;
				if (isEmptyField(nr, nc)) {
					addPossibleMove(row, column, nr, nc, pieceIsWhite);
				}
			}
		}

		// capture piece at left hand side
		nr = row + direction;
		nc = column - 1;
		if (isOpponentsPiece(nr, nc, pieceIsWhite)) {
			addPossibleMove(row, column, nr, nc, pieceIsWhite);
		} else if (canBeCapturedEnPassant(nr, nc)) {
			addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}

		// capture piece at right hand side
		nr = row + direction;
		nc = column + 1;
		if (isOpponentsPiece(nr, nc, pieceIsWhite)) {
			addPossibleMove(row, column, nr, nc, pieceIsWhite);
		} else if (canBeCapturedEnPassant(nr, nc)) {
			addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
	}

	private boolean canBeCapturedEnPassant(int nr, int nc) {
		return isInsideBoard(nr, nc) && (getChessPiece(nr, nc) == -1);
	}

	private void getLegalMovesForAQueen(int row, int column, boolean pieceIsWhite) {
		getLegalMovesForARook(row, column, pieceIsWhite);
		getLegalMovesForABishop(row, column, pieceIsWhite);
	}

	private void getLegalMovesForARook(int row, int column, boolean pieceIsWhite) {
		int nr;
		int nc;

		nr = row;
		nc = column;
		while (true) {
			nr++;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nr--;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nc++;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nc--;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
	}

	private void getLegalMovesForABishop(int row, int column, boolean pieceIsWhite) {
		int nr;
		int nc;

		nr = row;
		nc = column;
		while (true) {
			nr++;
			nc++;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nr--;
			nc++;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nr++;
			nc--;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
		nr = row;
		nc = column;
		while (true) {
			nr--;
			nc--;
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
				if (!isEmptyField(nr, nc)) {
					break;
				}
			} else {
				break;
			}
		}
	}

	private void getLegalMovesForAKing(int row, int column, boolean pieceIsWhite) {
		int nr;
		int nc;

		nr = row;
		nc = column;
		nr++;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			// To do: recalculate the canBeReachedByBlackPiece is the offending
			// piece is to be captured by the king
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
			
		}
		nr = row;
		nc = column;
		nr--;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite)){
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
			}
		}
		nr = row;
		nc = column;
		nc++;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		nr = row;
		nc = column;
		nc--;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		nr = row;
		nc = column;
		nr++;
		nc++;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
			else if (!canBeReachedByOpponentsPieceAfterCapturing(row, column, nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		nr = row;
		nc = column;
		nr--;
		nc++;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		nr = row;
		nc = column;
		nr++;
		nc--;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		nr = row;
		nc = column;
		nr--;
		nc--;
		if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
			if (!canBeReachedByOpponentsPiece(nr, nc, pieceIsWhite))
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
		}
		// castling to the right hand side
		nr = row;
		nc = column;
		if (((nr == 0 && (!pieceIsWhite))) || ((nr == 7) && pieceIsWhite))
			if (nc == 4)

			{
				if (isEmptyField(nr, 5) && isEmptyField(nr, 6)) {
					int piece = getChessPiece(nr, 7);
					if (piece == B_ROOK && (!pieceIsWhite)) {
						if (canBeReachedByWhitePiece(nr, 4) == false && canBeReachedByWhitePiece(nr, 5) == false
								&& canBeReachedByWhitePiece(nr, 6) == false) {
							addPossibleMove(row, column, row, column + 2, pieceIsWhite);
						}
					}
					if (piece == W_ROOK && (pieceIsWhite)) {
						if (canBeReachedByBlackPiece(nr, 4) == false && canBeReachedByBlackPiece(nr, 5) == false
								&& canBeReachedByBlackPiece(nr, 6) == false) {
							addPossibleMove(row, column, row, column + 2, pieceIsWhite);
						}
					}
				}
			}
		// castling to the left hand side
		nr = row;
		nc = column;
		if (((nr == 0 && (!pieceIsWhite))) || ((nr == 7) && pieceIsWhite))
			if (nc == 4) {
				if (isEmptyField(nr, 3) && isEmptyField(nr, 2) && isEmptyField(nr, 1)) {
					int piece = getChessPiece(nr, 0);
					if (piece == B_ROOK && (!pieceIsWhite)) {
						if (canBeReachedByWhitePiece(nr, 4) == false && canBeReachedByWhitePiece(nr, 3) == false
								&& canBeReachedByWhitePiece(nr, 2) == false) {
							addPossibleMove(row, column, row, column - 2, pieceIsWhite);
						}
					}
					if (piece == W_ROOK && (pieceIsWhite)) {
						if (canBeReachedByBlackPiece(nr, 4) == false && canBeReachedByBlackPiece(nr, 3) == false
								&& canBeReachedByBlackPiece(nr, 2) == false) {
							addPossibleMove(row, column, row, column - 2, pieceIsWhite);
						}
					}
				}
			}

	}

	private void getLegalMovesForAKnight(int row, int column, boolean pieceIsWhite) {
		int nr;
		int nc;
		int moveRow[] = { 1, 2, 2, 1, -1, -2, -2, -1 };
		int moveCol[] = { -2, -1, 1, 2, -2, -1, 1, 2 };

		for (int i = 0; i < moveCol.length; i++) {
			nr = row + moveRow[i];
			nc = column + moveCol[i];
			if (isEmptyOrCanBeCaptured(nr, nc, pieceIsWhite)) {
				addPossibleMove(row, column, nr, nc, pieceIsWhite);
			}
		}
	}

	public void addPossibleMove(int fromRow, int fromColumn, int toRow, int toColumn, boolean pieceIsWhite) {
		int promotedPiece = 0;
		addPossibleMove(fromRow, fromColumn, toRow, toColumn, pieceIsWhite, promotedPiece);
	}

	private void addPossibleMove(int fromRow, int fromColumn, int toRow, int toColumn, boolean pieceIsWhite,
			int promotedPiece) {
		int capturedPiece = getChessPiece(toRow, toColumn);
		if (capturedPiece < 0) {
			capturedPiece = pieceIsWhite ? B_PAWN : W_PAWN;
		} else if (capturedPiece == W_KING) {
			isWhiteKingThreatened = true;
		} else if (capturedPiece == B_KING) {
			isBlackKingThreatened = true;
		}
		int m = move(fromRow, fromColumn, toRow, toColumn, capturedPiece, promotedPiece);
		if (pieceIsWhite) {
			whiteMoves[numberOfWhiteMoves++] = m;
		} else {
			blackMoves[numberOfBlackMoves++] = m;
		}
		addFieldToPieceReach(toRow, toColumn, getChessPiece(fromRow, fromColumn), pieceIsWhite);
	}

	private void addFieldToPieceReach(int row, int column, int piece, boolean pieceIsWhite) {
		if (pieceIsWhite) {
			canBeReachedByWhitePiece[row][column]++;
		} else {
			canBeReachedByBlackPiece[row][column]++;
		}
	}

	private boolean canBeReachedByWhitePiece(int row, int column) {
		return whitePieceReach(row, column) > 0;
	}

	private boolean canBeReachedByBlackPiece(int row, int column) {
		return blackPieceReach(row, column) > 0;
	}

	private int whitePieceReach(int row, int column) {
		return canBeReachedByWhitePiece[row][column];
	}

	private boolean canBeReachedByOpponentsPiece(int row, int column, boolean pieceIsWhite) {
		if (pieceIsWhite)
			return canBeReachedByBlackPiece(row, column);
		else
			return canBeReachedByWhitePiece(row, column);
	}

	private boolean canBeReachedByOpponentsPieceAfterCapturing(int row, int column, int toRow, int toColumn,
			boolean pieceIsWhite) {
		// preconditions:
		// piece(row, column) is occupied by a king.
		// piece(toRow, toColumn) is occupied by a piece that can be captured by
		// this kind. I.e. it's an oppenent's piece, it's not empty
		// and it's exactly one step away.
		if (blackPieceReach(row, column) == 0)
			return false;
		if (blackPieceReach(row, column) > 1)
			return true;
		int offendingPiece = getChessPiece(toRow, toColumn);
		if (row == toRow || column == toColumn) {
			// king might be captured by queen or rook (or another king)
			if (offendingPiece == B_QUEEN || offendingPiece == W_QUEEN || offendingPiece == B_ROOK
					|| offendingPiece == W_ROOK || offendingPiece == B_KING || offendingPiece == W_KING) {
				return false;
			}
		} else {
			// king might be captured by queen, bishop, pawn (or another king)
			if (offendingPiece == B_QUEEN || offendingPiece == W_QUEEN || offendingPiece == B_BISHOP
					|| offendingPiece == W_BISHOP || offendingPiece == B_KING || offendingPiece == W_KING) {
				return false;
			}
			if (offendingPiece == B_PAWN && row > toRow)
				return false;
			if (offendingPiece == W_PAWN && row < toRow)
				return false;
		}
		return true;
	}

	private int blackPieceReach(int row, int column) {
		return canBeReachedByBlackPiece[row][column];
	}

	public int move(int fromRow, int fromColumn, int toRow, int toColumn, int capturedPiece, int promotedPiece) {
		int capturedValue = s_MATERIAL_VALUE[capturedPiece];
		int compact = (capturedValue << 24) + (promotedPiece << 16) + (fromRow << 12) + (fromColumn << 8) + (toRow << 4)
				+ toColumn;
		return compact;
	}

	public boolean isMovePossible(int fromRow, int fromColumn, int toRow, int toColumn) {
		int compact = move(fromRow, fromColumn, toRow, toColumn, 0, 0);
		int[] legalMoves;
		if (activePlayerIsWhite) {
			legalMoves = whiteMoves;
		} else {
			legalMoves = blackMoves;
		}
		for (int c : legalMoves) {
			if ((c & 0xFFFF) == compact) {
				Chessboard b = moveChessPiece(new Move(0, fromRow, fromColumn, toRow, toColumn, 0, false, false, 0));
				try {
					if (b.activePlayerIsWhite) {
						b.findBestWhiteMoves(0, 1, false);
					} else {
						b.findBestBlackMoves(0, 1, false);
					}
					return true;
				} catch (WhiteIsCheckMateException p_impossibleMove) {
					return !activePlayerIsWhite;
				} catch (BlackIsCheckMateException p_impossibleMove) {
					return activePlayerIsWhite;
				} catch (EndOfGameException e) {
					return true;
				}
			}
		}
		return false;
	}

}
