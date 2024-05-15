package damcaBitBoardTry;


import java.util.ArrayList;

public class Move implements Comparable<Move> {
	public Point newPosition;
	public Point oldPosition;
	public ArrayList<Point> killedUnits;
	private int weight = 0;

	public Move() {
	}

	public Move(Point newPos, Point oldPos, ArrayList<Point> killedUnits) {
		this.newPosition = newPos;
		this.oldPosition = oldPos;
		this.killedUnits = new ArrayList<Point>();
		if (killedUnits != null) {
			this.killedUnits.addAll(killedUnits);
		}
	}

	public void updateWeight(int w) {
		this.weight += w;
	}

	public int getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(Move o) {

		return this.weight - o.weight;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "New: " + this.newPosition.toString() + "\nOld: " + this.oldPosition.toString() + "\nWeight: "
				+ this.weight +"\n" + (this.killedUnits).toString();
	}
}
