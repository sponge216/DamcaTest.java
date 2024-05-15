package damcaBitBoardTry;

public interface TurnIE {



	public enum Turn {
		WhiteTurn("WT"), BlackTurn("BT"), ComputerTurn("CT");

		private final String turn;

		private Turn(String turn) {
			this.turn = turn;
		}

		public String getTurn() {
			return this.turn;
		}
	}

}
