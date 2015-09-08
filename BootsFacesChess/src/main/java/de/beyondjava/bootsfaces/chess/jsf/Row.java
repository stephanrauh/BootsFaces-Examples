package de.beyondjava.bootsfaces.chess.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Row implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> fileNames = new ArrayList<>();

	public Row(int row, int[] pieces, boolean reverse) {
		int background = 1 - row % 2;
		if (reverse) {
			for (int col = pieces.length-1; col >=0; col--) {
				int piece = pieces[col];
				if (piece < 0)
					piece = 0; // treat en-passant-fields as empty fields
				fileNames.add("wikimediaimages/" + ImageNames.chessPiecesFileNameCropped[piece/2]);
				background = 1 - background;
			}

		} else {
			for (int col = 0; col < pieces.length; col++) {
				int piece = pieces[col];
				if (piece < 0)
					piece = 0; // treat en-passant-fields as empty fields
				fileNames.add("wikimediaimages/" + ImageNames.chessPiecesFileNameCropped[piece/2]);
				background = 1 - background;
			}
		}
	}

	public List<String> getImages() {
		return fileNames;
	}

}
