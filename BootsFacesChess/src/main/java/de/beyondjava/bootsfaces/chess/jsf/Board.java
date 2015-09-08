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
import de.beyondjava.bootsfaces.chess.exceptions.BlackIsCheckMateException;
import de.beyondjava.bootsfaces.chess.exceptions.EndOfGameException;
import de.beyondjava.bootsfaces.chess.exceptions.StaleMateException;
import de.beyondjava.bootsfaces.chess.exceptions.WhiteIsCheckMateException;
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

	private boolean blackIsTop = true;

	private EndOfGameException gameOverException;

	@ManagedProperty("#{settings}")
	private Settings settings;

	private boolean startOpponentsMove = false;

	public Board() {
	}

	public void flipSides(ActionEvent event) {
		blackIsTop = !blackIsTop;
		startOpponentsMove = true;
		opponentsMove(event);
	}

	public String getBackgroundImage(int row, int column) {
		if ((row + column) % 2 == 0)
			return "wikimediaimages/s_feld.png";
		else
			return "wikimediaimages/w_feld.png";
	}

	public void newGameWhite(ActionEvent event) {
		chessboard = new Chessboard();
		history.clear();
		endOfGame = false;
		isPieceSelected = false;
		blackIsTop = true;
		redraw();
	}

	public void newGameBlack(ActionEvent event) {
		newGameWhite(null);
		blackIsTop = false;
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
		chessboard.evaluateBoard();
		info = "W:" + String.valueOf(chessboard.whiteTotalValue) + " B: " + String.valueOf(chessboard.blackTotalValue);
		return info;
	}

	public String getBorder(int row, int column) {
		if (!blackIsTop) {
			row = 7 - row;
			column = 7 - column;
		}
		if (startOpponentsMove)
			return "border:1px solid black";
		if (!isPieceSelected)
			return "border:1px solid black";
		if (selectedPieceColumn == column && selectedPieceRow == row)
			return "border: 2px solid blue;";
		if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column))
			return "border: 2px solid red;";
		return "border: 1px solid black";
	}

	public String getOpacity(int row, int column) {
		if (!blackIsTop) {
			row = 7 - row;
			column = 7 - column;
		}
		if (startOpponentsMove)
			return "opacity:0.7";
		if (!isPieceSelected)
			return "opacity:1.0";
		if (selectedPieceColumn == column && selectedPieceRow == row)
			return "opacity:1.0";
		if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column))
			return "opacity:1.0";
		return "opacity:1.0";
	}
	
	public String getLegalTargets() {
		String targets="";
		for (int row = 0; row<8; row++)
			for (int column=0; column<8;column++)
				if (isLegalTarget(row, column))
					targets+="("+row+","+column+")";
		return targets;
	}
	
	public boolean isLegalTarget(int row, int column) {
		if (!blackIsTop) {
			row = 7 - row;
			column = 7 - column;
		}
		if (startOpponentsMove)
			return false;
		if (!isPieceSelected)
			return false;
		if (selectedPieceColumn == column && selectedPieceRow == row)
			return false;
		if (chessboard.isMovePossible(selectedPieceRow, selectedPieceColumn, row, column))
			return true;
		return false;
	}

	public List<Row> getRows() {
		return rows;
	}

	public Settings getSettings() {
		return settings;
	}

	public String getTitle() {
		if (endOfGame) {
			if (gameOverException instanceof WhiteIsCheckMateException)
				return "White is checkmate!";
			if (gameOverException instanceof BlackIsCheckMateException)
				return "Black is checkmate!";
			if (gameOverException instanceof StaleMateException)
				return "Stalemate!";
			return "Game over. " + gameOverException.getClass().getSimpleName();
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
			if (!blackIsTop) {
				row = 7 - row;
				column = 7 - column;
			}
			if (isPieceSelected) {
				isPieceSelected = false;
				// chessboard.findLegalMovesIgnoringCheck(); // debug
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
					if (history.size() % 2 == 0) {
						move = String.valueOf((history.size() / 2) + 1) + ". " + move;
					}
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
			startOpponentsMove = false;
		} else {
			if (startOpponentsMove) {
				startOpponentsMove = false;
				try {
					Move m = chessboard.findBestMove();
					String move = m.getNotation();
					if (history.size() % 2 == 0) {
						move = String.valueOf((history.size() / 2) + 1) + ". " + move;
					}
					history.add(move);
					info = move.toString();
					chessboard = chessboard.moveChessPiece(m.fromRow, m.fromColumn, m.toRow, m.toColumn,
							ChessConstants.W_QUEEN);
				} catch (EndOfGameException e) {
					FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "The game is already over.");
					FacesContext.getCurrentInstance().addMessage(null, fm);
					endOfGame = true;
					gameOverException = e;
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
			settings.setMovesToConsider(7);
			settings.setMultithreading(true);
			settings.setLookAhead(6);
		}
		redraw();
	}

	private void redraw() {
		int[][] pieces = chessboard.board;
		setRows(new ArrayList<Row>());
		if (blackIsTop) {
			for (int i = 0; i < 8; i++) {
				getRows().add(new Row(i, pieces[i], false));
			}
		} else {
			for (int i = 7; i >= 0; i--) {
				getRows().add(new Row(i, pieces[i], true));
			}

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

	public void ondragstart(int fromRow, int fromColumn) {
		isPieceSelected=false;
		onclick(fromRow, fromColumn);
	}

	public void ondragdrop(int toRow, int toColumn) {
		onclick(toRow, toColumn);
	}

}
