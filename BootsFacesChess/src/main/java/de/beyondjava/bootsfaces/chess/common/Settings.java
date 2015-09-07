package de.beyondjava.bootsfaces.chess.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Settings {
    private static int lookAhead = 5;
    private static int movesToConsider = 7;
    private static boolean multithreading = true;
    
	public int getLookAhead() {
		return lookAhead;
	}
	public void setLookAhead(int lookAhead) {
		Settings.lookAhead = lookAhead;
	}
	public int getMovesToConsider() {
		return movesToConsider;
	}
	public void setMovesToConsider(int movesToConsider) {
		Settings.movesToConsider = movesToConsider;
	}
	public boolean isMultithreading() {
		return multithreading;
	}
	public void setMultithreading(boolean multithreading) {
		Settings.multithreading = multithreading;
	}

}
