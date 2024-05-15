package DamcaFirstTry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import DamcaFirstTry.UnitAndTurnIE.Unit;

public class BoardAI implements UnitAndTurnIE, BoardIE {
	private Board playerBoard;
	private BoardSpace[][] board;
	private ArrayList<Point> moves;
	private HashMap<Point, ArrayList<Point>> killMovesMap;
	private Unit unit;
	private int pcUnitCount;
	private int playerUnitCount;

	public BoardAI(Board playerBoard, BoardSpace[][] board, Unit unit, int pcUnitCount, int playerUnitCount) {
		this.playerBoard = playerBoard;
		this.board = Arrays.copyOf(board, BOARD_SIZE);
		this.moves = new ArrayList<Point>();
		this.killMovesMap = new HashMap<Point, ArrayList<Point>>();
		this.unit = unit;
		this.pcUnitCount = pcUnitCount;
		this.playerUnitCount = playerUnitCount;
		bestMove();

	}

	public void bestMove() {
		ArrayList<Point> killedDuringMove = new ArrayList<Point>();
		Point newPoint = new Point();
		Point currentPoint = new Point();
		int maxKills = 0;
		int currentKills = 0;
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < BOARD_LENGTH; i++) {
			for (int j = 1 - i % 2; j < BOARD_WIDTH; j += 2) {
				if (this.board[i][j].getUnit() == this.unit) {
					this.moves = possibleMoves(i, j);
					if (this.killMovesMap.size() != 0) {
						System.out.println("kill move!");
						for (Point key : this.killMovesMap.keySet()) {
							currentKills = this.killMovesMap.get(key).size();
							if (currentKills > maxKills) {
								maxKills = currentKills;
								killedDuringMove = this.killMovesMap.get(key);
								newPoint = key;
								currentPoint = new Point(i, j);
							}
						}

					} else {
						if (maxKills == 0 && this.moves.size() > 0) {
							newPoint = this.moves.get(new Random().nextInt(this.moves.size()));
							currentPoint = new Point(i, j);
							System.out.println(currentPoint);
							System.out.println(newPoint);
						}
					}
					this.killMovesMap.clear();
				}
			}
		}
		for (Point point : killedDuringMove) {
			killUnit(point.getY(), point.getX());
		}
		moveKing(currentPoint, newPoint);
		moveUnit(currentPoint, newPoint);
		this.playerBoard.updateCounts(pcUnitCount, playerUnitCount);
		System.out.println(currentPoint);
		System.out.println(newPoint);
		makeKing(newPoint);
		
	}
	public void makeKing(Point newPoint) {
		if (this.unit == Unit.Black) {
			if (newPoint.getY() == 0)
				this.playerBoard.getBoard()[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
		}
	}
	public void moveUnit(Point currentPoint, Point newPoint) {
		this.playerBoard.getBoard()[newPoint.getY()][newPoint.getX()]
				.setUnit(board[currentPoint.getY()][currentPoint.getX()].getUnit());
		this.playerBoard.getBoard()[currentPoint.getY()][currentPoint.getX()].setUnit(Unit.Empty);
	}

	public void moveKing(Point currentPoint, Point newPoint) {
		if (this.playerBoard.getBoard()[currentPoint.getY()][currentPoint.getX()].getIsKing()) {
			board[currentPoint.getY()][currentPoint.getX()].changeKingStatus(false);
			board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
		}
	}

	public ArrayList<Point> possibleMoves(int i, int j) {
		ArrayList<Point> moves = new ArrayList<Point>();
		Unit currentUnit = this.board[i][j].getUnit();
		if (this.board[i][j].getIsKing()) {
			if (this.unit == currentUnit) {
				moves = possibleComputerKingMoves(i, j);
			}
		} else if (this.unit == currentUnit)
			moves = possibleComputerMoves(i, j);
		return moves;
	}

	public ArrayList<Point> possibleComputerKingMoves(int posY, int posX) {
		return possibleKingMovesTemplate(posY, posX, this.unit);

	}

	public ArrayList<Point> possibleComputerMoves(int posY, int posX) {
		return possibleMovesTemplate(posY, posX, -1, this.unit);
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
					moves.addAll(possibleKingKillsTemplate(y, x, xDir, yDir, unit, killedUnits));
					printKilledMap();
					y += yDir;
					x += xDir;
				}
			}
		}
		return moves;
	}

	public ArrayList<Point> possibleKingKillsTemplate(int posY, int posX, int xDir, int yDir, Unit unit,
			ArrayList<Point> killedUnits) {
		ArrayList<Point> moves = new ArrayList<Point>();
		ArrayList<Point> killedUnitsLocal = new ArrayList<Point>();
		killedUnitsLocal.addAll(killedUnits);
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
						killedUnitsLocal = new ArrayList<Point>();
						killedUnitsLocal.addAll(killedUnits);
					}
				}
			}
		}
		return moves;
	}

	public boolean canKingKillTemplate(int y, int x, int yDir, int xDir, Unit unit) {
		if (unit == Unit.White)
			unit = Unit.Black;
		else if (unit == Unit.Black)
			unit = Unit.White;
		System.out.println("Y: " + y + " X:" + x + " Ydir: " + yDir);
		if (isInRangeY(y) && isInRangeX(x) && this.board[y][x].getUnit() == unit) {
			if (isInRangeY(y + yDir) && isInRangeX(x + xDir) && isEmpty(x + xDir, y + yDir)) {
				System.out.println("killing y:" + y + " x:" + x);
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
				} else if (isEmpty(i, posY + change)) {
					moves.add(new Point(posY + change, i));
					System.out.println("new empty move!");
				}
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
				System.out.println("Y: " + y + " X:" + x + " Killed");
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

	public Point findSquare(int pressx, int pressy, int w, int h) {
		return new Point(pressy / (h / BOARD_LENGTH), pressx / (w / BOARD_WIDTH));
	}

	public boolean isEmpty(int x, int y) {
		return this.board[y][x].getUnit() == Unit.Empty;
	}

	public void killUnit(int y, int x) {
		if (this.board[y][x].getUnit() == this.unit)
			this.pcUnitCount--;
		else
			this.playerUnitCount--;
		this.board[y][x].setUnit(Unit.Empty);
		if (this.board[y][x].getIsKing())
			this.board[y][x].changeKingStatus(false);
		System.out.println("New pc count: " + this.pcUnitCount + "\nNew player count: " + this.playerUnitCount);
	}

	public void printKilledMap() {
		for (Point name : killMovesMap.keySet()) {
			String key = name.toString();
			String value = killMovesMap.get(name).toString();
			System.out.println(key + " " + value);
		}
	}
}
