package damcaBitBoardTry;

public interface UnitAndMasksIE {

	public long cellMask = 0b111;
	public long kingMask = 0b100;

	public enum Unit {
		Empty(0), 
		White(0b1),Black(0b11), 
		WhiteKing(0b101), BlackKing(0b111);

		private final long unit;

		private Unit(long unit) {
			this.unit = unit;
		}

		public long getUnit() {
			return this.unit;
		}
	}
}
