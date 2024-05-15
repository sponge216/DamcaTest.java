package DamcaFirstTry;

public class BoardSpace implements UnitAndTurnIE, BoardIE {
	private Unit unit;
	private boolean isKing;

	public BoardSpace() {
		this.unit = Unit.Empty;
		this.isKing = false;
	}

	// set unit
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	// get unit
	public Unit getUnit() {
		return this.unit;
	}

	public void changeKingStatus(boolean isKing) {
		this.isKing = isKing;
	}

	public boolean getIsKing() {
		return this.isKing;
	}

	public static BoardSpace[][] initBoard(BoardSpace[][] board) {
		for (int i = 0; i < BOARD_LENGTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				board[i][j] = new BoardSpace();
			}
		}
		return board;
	}

	public void copy(BoardSpace space) {
		this.isKing = space.getIsKing();
		this.unit = space.getUnit();
		
	}
}
