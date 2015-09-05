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
	
	private List<Row> rows;
	
	public Board() {
		
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

}
