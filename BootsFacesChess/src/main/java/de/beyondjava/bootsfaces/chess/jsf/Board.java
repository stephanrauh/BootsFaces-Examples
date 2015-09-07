package de.beyondjava.bootsfaces.chess.jsf;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import de.beyondjava.bootsfaces.chess.Exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.common.ChessConstants;
import de.beyondjava.bootsfaces.chess.common.Move;
import de.beyondjava.bootsfaces.chess.common.Settings;
import de.beyondjava.bootsfaces.chess.objectOrientedEngine.Chessboard;

@ManagedBean
@ViewScoped
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;

	private Chessboard chessboard = new Chessboard();
	
	@ManagedProperty("#{settings}")
	private Settings settings;

	private boolean isPieceSelected = false;

	private int selectedPieceRow;

	private int selectedPieceColumn;

	private List<Row> rows;

	private boolean startOpponentsMove = false;

	private String elapsedTime;

	private String evaluatedMoves;

	private String cpuTime;

	String averageEvaluation;

	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getEvaluatedMoves() {
		return evaluatedMoves;
	}

	public String getCpuTime() {
		return cpuTime;
	}

	public String getAverageEvaluation() {
		return averageEvaluation;
	}

	public Board() {
	}
	
	@PostConstruct
	public void postInit() {
		String serverName=FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		if ("127.0.0.1".equals(serverName) || "localhost".equals(serverName)) {
			settings.unlockPowerMode();
		}
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

	public String getOpacityAndBorder(int row, int column) {
		if (startOpponentsMove)
			return "opacity:0.7;border:1px solid black";
		if (!isPieceSelected)
			return "opacity:1.0;border:1px solid black";
		if (selectedPieceColumn == column && selectedPieceRow == row)
			return "opacity:1.0;border: 2px solid blue;";
		if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column))
			return "opacity:1.0;border: 2px solid red;";
		return "opacity:1.0;border: 1px solid black";
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
		       elapsedTime = ((Chessboard.realTimeOfCalculation / 1000) / 1000.0d) + "ms";
		        cpuTime= ((Chessboard.totalTime/1000)/1000) + " ms";
		        evaluatedMoves=NumberFormat.getInstance().format( Chessboard.evaluatedPositions);
		        averageEvaluation=Chessboard.totalTime/chessboard.evaluatedPositions + " ns";

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
		startOpponentsMove = true;
		opponentsMove(event);
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
