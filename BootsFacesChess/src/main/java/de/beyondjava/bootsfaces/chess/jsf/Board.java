package de.beyondjava.bootsfaces.chess.jsf;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import de.beyondjava.bootsfaces.chess.common.ChessConstants;
import de.beyondjava.bootsfaces.chess.common.Move;
import de.beyondjava.bootsfaces.chess.common.Settings;
import de.beyondjava.bootsfaces.chess.exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.objectOrientedEngine.Chessboard;

@ManagedBean
@ViewScoped
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;

	private String averageEvaluation;

	private Chessboard chessboard = new Chessboard();

	private String cpuTime;

	private String elapsedTime;

	private String evaluatedMoves;

	private List<String> history = new ArrayList<>();

	private String info = "";

	private boolean isPieceSelected = false;

	private List<Row> rows;

	private int selectedPieceColumn;

	private int selectedPieceRow;

	private boolean endOfGame = false;
	
	private EndOfGameException gameOverException;

	@ManagedProperty("#{settings}")
	private Settings settings;

	private boolean startOpponentsMove = false;

	public Board() {
	}

	public void flipSides(ActionEvent event) {
		startOpponentsMove = true;
		opponentsMove(event);
	}

	public String getAverageEvaluation() {
		return averageEvaluation;
	}

	public String getCpuTime() {
		return cpuTime;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getEvaluatedMoves() {
		return evaluatedMoves;
	}

	public List<String> getHistory() {
		return history;
	}

	public String getInfo() {
		return info;
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

	public List<Row> getRows() {
		return rows;
	}

	public Settings getSettings() {
		return settings;
	}

	public String getTitle() {
		if (endOfGame) {
			return "Game over." + gameOverException.getClass().getSimpleName();
		} else if (startOpponentsMove)
			return "Calculating next move...";
		else
			return "Waiting for your move";
	}

	public boolean isStartOpponentsMove() {
		return startOpponentsMove;
	}

	public void onclick(int row, int column) {
		if (endOfGame) {
			FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "The game is already over.");
			FacesContext.getCurrentInstance().addMessage(null, fm);
		} else {
			if (isPieceSelected) {
				isPieceSelected = false;
				if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column)) {
					int piece = chessboard.getChessPiece(selectedPieceRow, selectedPieceColumn);
					int capturedPiece = chessboard.getChessPiece(row, column);

					chessboard = chessboard.moveChessPiece(selectedPieceRow, selectedPieceColumn, row, column,
							ChessConstants.W_QUEEN);
					boolean chess = chessboard.isWhiteKingThreatened || chessboard.isBlackKingThreatened;
					Move m = new Move(piece, selectedPieceRow, selectedPieceColumn, row, column,
							Chessboard.s_MATERIAL_VALUE[1] - Chessboard.s_MATERIAL_VALUE[0], chess,
							capturedPiece > ChessConstants.W_EMPTY, capturedPiece);
					info = m.toString();
					String move = m.getNotation();
					history.add(move);
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
		}
		redraw();
	}

	public void opponentsMove(ActionEvent event) {
		if (endOfGame) {
			FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "The game is already over.");
			FacesContext.getCurrentInstance().addMessage(null, fm);
		} else {
			if (startOpponentsMove) {
				startOpponentsMove = false;
				try {
					Move move = chessboard.findBestMove();
					history.add(move.getNotation());
					info = move.toString();
					chessboard = chessboard.moveChessPiece(move.fromRow, move.fromColumn, move.toRow, move.toColumn,
							ChessConstants.W_QUEEN);
				} catch (EndOfGameException e) {
					FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "The game is already over.");
					FacesContext.getCurrentInstance().addMessage(null, fm);
					endOfGame = true;
					gameOverException=e;
					info = e.getClass().getSimpleName();
				}
				elapsedTime = ((Chessboard.realTimeOfCalculation / 1000) / 1000.0d) + "ms";
				cpuTime = ((Chessboard.totalTime / 1000) / 1000) + " ms";
				evaluatedMoves = NumberFormat.getInstance().format(Chessboard.evaluatedPositions);
				averageEvaluation = Chessboard.totalTime / chessboard.evaluatedPositions + " ns";
			}
		}
		redraw();

	}

	@PostConstruct
	public void postConstruct() {
		String serverName = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
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

	public void setHistory(List<String> history) {
		this.history = history;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void setStartOpponentsMove(boolean startOppenentsMove) {
		this.startOpponentsMove = startOppenentsMove;
	}

	public boolean isEndOfGame() {
		return endOfGame;
	}

	public void setEndOfGame(boolean endOfGame) {
		this.endOfGame = endOfGame;
	}

}
