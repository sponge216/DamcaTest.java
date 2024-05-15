package DamcaFirstTry;

public interface UnitAndTurnIE {

	public enum Unit {
		Empty(0), Black(1), White(2);

		private final int unit;

		private Unit(int unit) {
			this.unit = unit;
		}

		public int getUnit() {
			return this.unit;
		}
	}

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
