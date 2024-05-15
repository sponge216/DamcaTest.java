package DamcaSecondTry;

import java.util.ArrayList;
import java.util.HashMap;
import DamcaFirstTry.BoardIE;
import DamcaFirstTry.BoardSpace;
import DamcaFirstTry.Point;
import DamcaFirstTry.UnitAndTurnIE;

public class LogicalBoard implements UnitAndTurnIE, BoardIE {
	protected BoardSpace[][] board;
	protected ArrayList<Point> moves;
	protected HashMap<Point, ArrayList<Point>> killMovesMap;
	protected ArrayList<Point> killedUnits;

	protected Point positions;
	protected Point prevPositions;
	// Later change turn to be as input;
	protected static Turn turn = Turn.WhiteTurn;
	protected int countBlack;
	protected int countWhite;
	protected int countBlackKing;
	protected int countWhiteKing;

	public LogicalBoard() {
		this.board = new BoardSpace[BOARD_LENGTH][BOARD_WIDTH];
		this.moves = new ArrayList<Point>();
		this.killMovesMap = new HashMap<Point, ArrayList<Point>>();
		this.killedUnits = new ArrayList<Point>();
		this.positions = new Point();
		this.prevPositions = new Point();

		initDamcaBoard();
//		this.board[2][3].changeKingStatus(true);
//		this.board[6][7].setUnit(Unit.Empty);
//		this.board[2][5].changeKingStatus(true);
	}

	// give the appropriate unit value to each space in board.
	protected void initDamcaBoard() {
		turn = Turn.WhiteTurn;
		this.board = BoardSpace.initBoard(board);
		this.countBlack = 12;
		this.countWhite = 12;
		this.countBlackKing = 0;
		this.countWhiteKing = 0;
		for (int i = 0; i < BOARD_LENGTH / 2 - 1; i += 2) {
			for (int j = 1; j < BOARD_WIDTH; j += 2) {
				this.board[i][j].setUnit(Unit.White);
				this.board[BOARD_LENGTH - 1 - i][j - 1].setUnit(Unit.Black);
			}
		}
		for (int i = 0; i < BOARD_LENGTH; i += 2) {
			this.board[1][i].setUnit(Unit.White);
			this.board[BOARD_LENGTH - 2][i + 1].setUnit(Unit.Black);

		}
	}

	public ArrayList<Point> possibleMoves(int i, int j) {
		ArrayList<Point> moves = new ArrayList<Point>();
		Unit unit = this.board[i][j].getUnit();
		if (this.board[i][j].getIsKing()) {
			if (unit == Unit.White && turn == Turn.WhiteTurn) {
				moves = possibleWhiteKingMoves(i, j);
			}
			if (unit == Unit.Black && turn == Turn.BlackTurn) {
				moves = possibleBlackKingMoves(i, j);
			}
		} else if (unit == Unit.White && turn == Turn.WhiteTurn)
			moves = possibleWhiteMoves(i, j);
		else if (unit == Unit.Black && turn == Turn.BlackTurn)
			moves = possibleBlackMoves(i, j);
		return moves;
	}

	public ArrayList<Point> possibleWhiteKingMoves(int posY, int posX) {
		return possibleKingMovesTemplate(posY, posX, Unit.White);

	}

	public ArrayList<Point> possibleBlackKingMoves(int posY, int posX) {
		return possibleKingMovesTemplate(posY, posX, Unit.Black);

	}

	public ArrayList<Point> possibleWhiteMoves(int posY, int posX) {
		return possibleMovesTemplate(posY, posX, +1, Unit.White);
	}

	public ArrayList<Point> possibleBlackMoves(int posY, int posX) {
		return possibleMovesTemplate(posY, posX, -1, Unit.Black);
	}

	public ArrayList<Point> possibleKingMovesTemplate(int posY, int posX, Unit unit) {
		ArrayList<Point> moves = new ArrayList<Point>();
		ArrayList<Point> killedUnits = new ArrayList<Point>();
		int xDir, yDir;
		int x, y;
		Point point = new Point();
		for (int i = posY - 1; i < posY + 2; i += 2) {
			for (int j = posX - 1; j < posX + 2 && isInRangeY(i); j += 2) {
				xDir = j - posX;
				yDir = i - posY;
				y = i;
				x = j;
				if (canKingKillTemplate(y, x, yDir, xDir, unit)) {
					point = new Point(y + yDir, x + xDir);
					moves.add(point);
					killedUnits.add(new Point(y, x));
					this.killMovesMap.put(point, killedUnits);
					moves.addAll(possibleKingKillsTemplate(y + yDir, x + xDir, xDir, yDir, unit, killedUnits));
					killedUnits = new ArrayList<Point>();
				}
				while (isInRangeX(x) && isInRangeY(y) && (this.board[y][x].getUnit() == Unit.Empty)) {
					moves.add(new Point(y, x));
//					moves.addAll(possibleKingKillsTemplate(y, x, xDir, yDir, unit, killedUnits));
					printKilledMap();
					y += yDir;
					x += xDir;
				}

				moves.addAll(possibleKingKillsStraightTemplate(y, x, xDir, yDir, unit, killedUnits));
			}
		}
		return moves;
	}

	public ArrayList<Point> possibleKingKillsTemplate(int posY, int posX, int xDir, int yDir, Unit unit,
			ArrayList<Point> killedUnits) {
		ArrayList<Point> moves = new ArrayList<Point>();
        ArrayList<Point> killedUnitsLocal = new ArrayList<Point>(killedUnits);
		Point point = new Point();
		int xDirLocal, yDirLocal;
		for (int i = posY - 1; i < posY + 2; i += 2) {
			for (int j = posX - 1; j < posX + 2 && isInRangeY(i); j += 2) {
				xDirLocal = j - posX;
				yDirLocal = i - posY;
				if ((yDirLocal != -yDir || xDirLocal != -xDir)) {
					if (canKingKillTemplate(i, j, yDirLocal, xDirLocal, unit)) {
						point = new Point(i + yDirLocal, j + xDirLocal);
						moves.add(point);
						killedUnitsLocal.add(new Point(i, j));
						this.killMovesMap.put(point, killedUnitsLocal);
						moves.addAll(possibleKingKillsTemplate(i + yDirLocal, j + xDirLocal, xDirLocal, yDirLocal, unit,
								killedUnitsLocal));
                        killedUnitsLocal = new ArrayList<Point>(killedUnits);
					}
				}
			}
		}
		return moves;
	}

	public ArrayList<Point> possibleKingKillsStraightTemplate(int posY, int posX, int xDir, int yDir, Unit unit,
			ArrayList<Point> killedUnits) {
		ArrayList<Point> moves = new ArrayList<Point>();
        ArrayList<Point> killedUnitsLocal = new ArrayList<Point>(killedUnits);
		Point point = new Point();

		if (canKingKillTemplate(posY, posX, yDir, xDir, unit)) {
			point = new Point(posY + yDir, posX + xDir);
			moves.add(point);
			killedUnitsLocal.add(new Point(posY, posX));
			this.killMovesMap.put(point, killedUnitsLocal);
			moves.addAll(possibleKingKillsTemplate(posY + yDir, posX + xDir, xDir, yDir, unit, killedUnitsLocal));
            killedUnitsLocal = new ArrayList<Point>(killedUnits);
		}

		return moves;
	}

	public boolean canKingKillTemplate(int y, int x, int yDir, int xDir, Unit unit) {
		if (unit == Unit.White)
			unit = Unit.Black;
		else if (unit == Unit.Black)
			unit = Unit.White;
		if (isInRangeY(y) && isInRangeX(x) && this.board[y][x].getUnit() == unit) {
			if (isInRangeY(y + yDir) && isInRangeX(x + xDir) && isEmpty(x + xDir, y + yDir)) {
				return true;
			}

		}
		return false;
	}

	public ArrayList<Point> possibleMovesTemplate(int posY, int posX, int change, Unit unit) {
		ArrayList<Point> moves = new ArrayList<Point>();
		ArrayList<Point> killedUnits = new ArrayList<Point>();
		int xDir;
		Point point = new Point();
		for (int i = posX - 1; i < posX + 2 && isInRangeY(posY + change); i += 2) {
			xDir = i - posX;
			if (isInRangeX(i) && (this.board[posY + change][i].getUnit() != unit)) {

				if (canKillTemplate(xDir, i, posY + change, change, unit)) {
					if (xDir < 0) {
						point = new Point(posY + 2 * change, i - 1);
						moves.add(point);
						killedUnits.add(new Point(posY + change, i));
						this.killMovesMap.put(point, killedUnits);
						
						moves.addAll(possibleKillsTemplate(posY + 2 * change, i - 1, change, unit, killedUnits));
						killedUnits = new ArrayList<Point>();
					} else {
						point = new Point(posY + 2 * change, i + 1);
						moves.add(point);
						killedUnits.add(new Point(posY + change, i));
						this.killMovesMap.put(point, killedUnits);
						moves.addAll(possibleKillsTemplate(posY + 2 * change, i + 1, change, unit, killedUnits));
					}
				} else if (isEmpty(i, posY + change))
					moves.add(new Point(posY + change, i));
			}
		}
		return moves;
	}

	public ArrayList<Point> possibleKillsTemplate(int posY, int posX, int change, Unit unit,
			ArrayList<Point> killedUnits) {
		ArrayList<Point> moves = new ArrayList<Point>();
		ArrayList<Point> killedUnitsLocal = new ArrayList<Point>();
		killedUnitsLocal.addAll(killedUnits);
		int xDir;
		Point point = new Point();
		for (int i = posX - 1; i < posX + 2 && isInRangeY(posY + change); i += 2) {
			xDir = i - posX;
			if (isInRangeX(i) && (this.board[posY + change][i].getUnit() != unit)) {
				if (canKillTemplate(xDir, i, posY + change, change, unit)) {
					if (xDir < 0) {
						point = new Point(posY + 2 * change, i - 1);
						moves.add(point);
						killedUnitsLocal.add(new Point(posY + change, i));
						this.killMovesMap.put(point, killedUnitsLocal);
						moves.addAll(possibleKillsTemplate(posY + 2 * change, i - 1, change, unit, killedUnitsLocal));
						killedUnitsLocal = new ArrayList<Point>();
					} else {
						point = new Point(posY + 2 * change, i + 1);
						moves.add(point);
						killedUnitsLocal.add(new Point(posY + change, i));
						this.killMovesMap.put(point, killedUnitsLocal);
						moves.addAll(possibleKillsTemplate(posY + 2 * change, i + 1, change, unit, killedUnitsLocal));
					}
				}
			}
		}
		return moves;
	}

	public boolean canKillTemplate(int xDir, int x, int y, int change, Unit unit) {
		if (unit == Unit.White)
			unit = Unit.Black;
		else if (unit == Unit.Black)
			unit = Unit.White;

		if (this.board[y][x].getUnit() == unit && isInRangeY(y + change)) {
			if (xDir < 0 && isInRangeX(x - 1) && isEmpty(x - 1, y + change)) {
//				System.out.println("Y: " + y + " X:" + x + " Killed");
				return true;
			} else if (xDir > 0 && isInRangeX(x + 1) && isEmpty(x + 1, y + change))
				return true;
		}
		return false;
	}

	public boolean isInRangeX(int x) {
		return x < BOARD_WIDTH && x > -1;
	}

	public boolean isInRangeY(int y) {
		return y < BOARD_LENGTH && y > -1;
	}

	public boolean isInMoves() {
		for (Point move : this.moves) {
			if (move.getY() == positions.getY() && move.getX() == positions.getX())
				return true;
		}
		return false;
	}

	public void makeKing() {
		if (this.positions.getY() == 0 && this.board[0][positions.getX()].getUnit() == Unit.Black) {
			this.board[0][positions.getX()].changeKingStatus(true);
			this.countBlackKing++;
			this.countBlack--;
		} else if (this.positions.getY() == BOARD_LENGTH - 1
				&& board[BOARD_LENGTH - 1][this.positions.getX()].getUnit() == Unit.White) {
			this.board[BOARD_LENGTH - 1][this.positions.getX()].changeKingStatus(true);
			this.countWhiteKing++;
			this.countWhite--;
		}
	}

	public void printMoves() {
		for (int i = 0; i < this.moves.size(); i++) {
			System.out.println("[ " + this.moves.get(i).getY() + " , " + moves.get(i).getX() + " ]");
		}
		System.out.println("positions: [ " + this.positions.getY() + " , " + this.positions.getX());
		System.out.println("prev positions: [ " + this.prevPositions.getY() + " , " + this.prevPositions.getX());
	}

	public void updateUnit() {
		if (this.board[this.prevPositions.getY()][this.prevPositions.getX()].getIsKing()) {
			this.board[this.prevPositions.getY()][this.prevPositions.getX()].changeKingStatus(false);
			this.board[this.positions.getY()][this.positions.getX()].changeKingStatus(true);
		}
		this.board[this.positions.getY()][this.positions.getX()]
				.setUnit(this.board[this.prevPositions.getY()][this.prevPositions.getX()].getUnit());
		this.board[this.prevPositions.getY()][this.prevPositions.getX()].setUnit(Unit.Empty);
	}

	public void changePlayersTurn() {
		if (turn == Turn.BlackTurn)
			turn = Turn.WhiteTurn;
		else
			turn = Turn.BlackTurn;
	}

	public Point findSquare(int pressx, int pressy, int w, int h) {
		return new Point(pressy / (h / BOARD_LENGTH), pressx / (w / BOARD_WIDTH));
	}

	public boolean isEmpty(int x, int y) {
		return this.board[y][x].getUnit() == Unit.Empty;
	}

	public void Win() {
		if (this.countBlack == 0 && this.countBlackKing == 0) {
			System.out.println("White won!");
			try {
				Thread.sleep(300);
				initDamcaBoard();
			} catch (InterruptedException e) {

			}
		} else if (this.countWhite == 0 && this.countWhiteKing == 0) {
			System.out.println("Black won!");
			try {
				Thread.sleep(300);
				initDamcaBoard();
			} catch (InterruptedException e) {

			}
		}

	}

	public void killUnit(int y, int x) {

		if (!this.board[y][x].getIsKing()) {

			if (this.board[y][x].getUnit() == Unit.Black)
				this.countBlack--;
			else if (this.board[y][x].getUnit() == Unit.White)
				this.countWhite--;

		} else {
			this.board[y][x].changeKingStatus(false);
			if (this.board[y][x].getUnit() == Unit.Black)
				this.countBlackKing--;
			if (this.board[y][x].getUnit() == Unit.White)
				this.countWhiteKing--;
		}
		this.board[y][x].setUnit(Unit.Empty);

		System.out.println("New black count: " + this.countBlack + "\nNew white count: " + this.countWhite);

	}

	public void printKilledMap() {
		for (Point name : this.killMovesMap.keySet()) {
			String key = name.toString();
			String value = this.killMovesMap.get(name).toString();
			System.out.println(key + " " + value);
		}
	}

	public BoardSpace[][] getBoard() {
		return this.board;
	}

	public Point getPositions() {
		return this.positions;
	}

	public void playerRelease() {
		if (turn == Turn.WhiteTurn) {
			this.prevPositions = this.positions;

		} else {
			this.prevPositions = new Point();
			this.positions = new Point();
		}
	}

	public Move playerMove(int pressx, int pressy, int w, int h) {

		if (turn == Turn.WhiteTurn) {
			this.positions = findSquare(pressx, pressy, w, h);
			if (!isInMoves()) {
				this.killMovesMap.clear();
				this.moves = possibleMoves(this.positions.getY(), this.positions.getX());
				printKilledMap();
			} else if (true) {
				this.moves.removeAll(moves);
				this.killedUnits = this.killMovesMap.get(this.positions);
				if (this.killedUnits != null)
					for (int i = 0; i < this.killedUnits.size(); i++) {
						killUnit(this.killedUnits.get(i).getY(), this.killedUnits.get(i).getX());
						System.out.println(
								"Killed: " + this.killedUnits.get(i).getY() + " " + this.killedUnits.get(i).getX());
					}
				this.killMovesMap.clear();
				updateUnit();
				makeKing();
				changePlayersTurn();
				System.out.println(turn);
				Win();
				return new Move(this.positions, this.prevPositions, this.killedUnits);
			}
			Win();
		}
		return null;
	}

	public void makeMove(Move move) {
		System.out.println(move);
		if (move != null) {
			moveKing(move.oldPosition, move.newPosition);
			moveUnit(move.oldPosition, move.newPosition);
			makeKing(move.newPosition);
			for (Point point : move.killedUnits) {
				killUnit(point.getY(), point.getX());
			}
		}
	}

	public void moveUnit(Point currentPoint, Point newPoint) {
		this.board[newPoint.getY()][newPoint.getX()].setUnit(board[currentPoint.getY()][currentPoint.getX()].getUnit());
		this.board[currentPoint.getY()][currentPoint.getX()].setUnit(Unit.Empty);
	}

	public void moveKing(Point currentPoint, Point newPoint) {
		if (this.board[currentPoint.getY()][currentPoint.getX()].getIsKing()) {
			board[currentPoint.getY()][currentPoint.getX()].changeKingStatus(false);
			board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
		}
	}

	public void makeKing(Point newPoint) {
		if (newPoint.getY() == 0 && this.board[newPoint.getY()][newPoint.getX()].getUnit() == Unit.Black)
			this.board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
		else if (newPoint.getY() == BOARD_LENGTH - 1
				&& this.board[newPoint.getY()][newPoint.getX()].getUnit() == Unit.White)
			this.board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
	}
}
