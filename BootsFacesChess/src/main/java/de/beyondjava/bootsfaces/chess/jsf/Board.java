package de.beyondjava.bootsfaces.chess.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.beyondjava.bootsfaces.chess.objectOrientedEngine.Chessboard;

@ManagedBean
@ViewScoped
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Chessboard chessboard = new Chessboard();
	
	private boolean isPieceSelected=false;
	
	private int selectedPieceRow;
	
	private int selectedPieceColumn;
	
	private List<Row> rows;
	
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
		if (!isPieceSelected) return "1.0";
		if (selectedPieceColumn==column && selectedPieceRow==row) return "1.0";
		return "0.7";
	}
	
	public void onclick(int row, int column) {
		if (isPieceSelected) {
			isPieceSelected=false;
		}
		else
		{
			isPieceSelected=true;
			selectedPieceRow=row;
			selectedPieceColumn=column;
		}
		redraw();
	}

}
