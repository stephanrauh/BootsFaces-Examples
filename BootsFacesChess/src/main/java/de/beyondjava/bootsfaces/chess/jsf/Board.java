package de.beyondjava.bootsfaces.chess.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import de.beyondjava.bootsfaces.chess.Exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.common.ChessConstants;
import de.beyondjava.bootsfaces.chess.common.Move;
import de.beyondjava.bootsfaces.chess.objectOrientedEngine.Chessboard;

@ManagedBean
@SessionScoped
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;

	private Chessboard chessboard = new Chessboard();

	private boolean isPieceSelected = false;

	private int selectedPieceRow;

	private int selectedPieceColumn;

	private List<Row> rows;

	private boolean startOpponentsMove = false;

	public Board() {
		redraw();
	}

	private void redraw() {
		int[][] pieces = chessboard.board;
		setRows(new ArrayList<Row>());
		for (int i = 0; i < 8; i++) {
			getRows().add(new Row(i, pieces[i]));
		}
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public String getOpacity(int row, int column) {
		if (startOpponentsMove) return "0.7";
		if (!isPieceSelected)
			return "1.0";
		if (selectedPieceColumn == column && selectedPieceRow == row)
			return "1.0";
		if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column))
			return "1.0";
		return "0.8";
	}

	public void onclick(int row, int column) {
		if (isPieceSelected) {
			isPieceSelected = false;
			if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column)) {
				// chessboard = chessboard.move(selectedPieceRow,
				// selectedPieceColumn, row, column,
				// fields, board, guiState, images, checkmate, whiteMoves,
				// blackMoves);

				chessboard = chessboard.moveChessPiece(selectedPieceRow, selectedPieceColumn, row, column,
						ChessConstants.W_QUEEN);
				selectedPieceRow = 0;
				selectedPieceColumn = 0;
				// opponentsMove();
				startOpponentsMove = true;
			}

		} else {
			int piece = chessboard.getChessPiece(row, column);
			if (piece != ChessConstants.B_EMPTY && piece != ChessConstants.W_EMPTY) {
				if (chessboard.isActivePlayersPiece(piece)) {
					isPieceSelected = true;
					selectedPieceRow = row;
					selectedPieceColumn = column;
				}
			}
		}
		redraw();
	}

	public void opponentsMove(ActionEvent event) {
		if (startOpponentsMove) {
			startOpponentsMove = false;
			try {
				Move move = chessboard.findBestMove();
				chessboard = chessboard.moveChessPiece(move.fromRow, move.fromColumn, move.toRow, move.toColumn,
						ChessConstants.W_QUEEN);
			} catch (EndOfGameException e) {
				e.printStackTrace();
			}
			redraw();
		}
	}

	public boolean isStartOpponentsMove() {
		return startOpponentsMove;
	}

	public void setStartOpponentsMove(boolean startOppenentsMove) {
		this.startOpponentsMove = startOppenentsMove;
	}
	
	public void flipSides(ActionEvent event) {
		startOpponentsMove=true;
		opponentsMove(event);
	}

}
