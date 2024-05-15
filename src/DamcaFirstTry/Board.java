package DamcaFirstTry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JPanel;

public class Board extends JPanel implements UnitAndTurnIE, BoardIE {
	private Board boardPointer = this;
	private BoardSpace[][] board;
	private ArrayList<Point> moves;
	private HashMap<Point, ArrayList<Point>> killMovesMap;
	Point positions;
	Point prevPositions;
	// Later change turn to be as input;
	private Turn turn;
	private int unitRadius;
	private int countBlack;
	private int countWhite;

	public Board() {
		this.board = new BoardSpace[BOARD_LENGTH][BOARD_WIDTH];
		this.moves = new ArrayList<Point>();
		this.killMovesMap = new HashMap<Point, ArrayList<Point>>();
		this.positions = new Point();
		this.prevPositions = new Point();
		this.turn = Turn.WhiteTurn;

		addMouseListener(new ML());
		initDamcaBoard();
//		this.board[5][2].changeKingStatus(true);
//		this.board[2][5].changeKingStatus(true);
	}

	// give the appropriate unit value to each space in board.
	public void initDamcaBoard() {
		this.turn = Turn.WhiteTurn;
		this.board = BoardSpace.initBoard(board);
		this.countBlack = 12;
		this.countWhite = 12;
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

	// Draw the board and its content.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);

		int w = getWidth() / BOARD_WIDTH;
		int h = getHeight() / BOARD_LENGTH;
		this.unitRadius = w / 2;

		paintSquares(g, w, h);
		paintUnits(g, w, h);
		showPossibleMoves(g, w, h);
	}

	public void paintUnits(Graphics g, int w, int h) {
		Unit unit;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < BOARD_LENGTH; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				unit = board[i][j].getUnit();
				if (unit == Unit.Black) {
					g2.setColor(Color.black);
					if (this.board[i][j].getIsKing())
						g2.setColor(Color.decode("#f83e3e"));
					g2.fillOval(w * j + w / 4, h * i + h / 4, unitRadius, unitRadius);
				} else if (unit == Unit.White) {
					g2.setColor(Color.white);
					if (this.board[i][j].getIsKing())
						g2.setColor(Color.decode("#6dd7fd"));
					g2.fillOval(w * j + w / 4, h * i + h / 4, unitRadius, unitRadius);
				}
			}
		}
	}

	public void paintSquares(Graphics g, int w, int h) {

		boolean colorChanger = true;
		for (int i = 0; i < BOARD_LENGTH; i++) {
			colorChanger = !colorChanger;
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (colorChanger)
					g.setColor(Color.decode("#C19A6B"));
				else
					g.setColor(Color.BLACK);
				g.fillRect(w * j, h * i, w, h);
				colorChanger = !colorChanger;
			}
		}
	}

	public void showPossibleMoves(Graphics g, int w, int h) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Point currentPos;
		if ((this.board[this.positions.getY()][positions.getX()].getUnit() == Unit.Black && turn == Turn.BlackTurn)
				|| (this.board[this.positions.getY()][positions.getX()].getUnit() == Unit.White
						&& turn == Turn.WhiteTurn)) {
			g2.setColor(Color.blue);
			g2.drawRect(this.positions.getX() * w, this.positions.getY() * h, w, h);
		}

		g2.setColor(Color.red);
		for (int i = 0; i < this.moves.size(); i++) {
			currentPos = this.moves.get(i);
			g2.drawRect(currentPos.getX() * w, currentPos.getY() * h, w, h);
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

	class ML extends MouseAdapter {

		ArrayList<Point> killedUnits;

		@Override
		public void mousePressed(MouseEvent e) {
			int pressx = e.getX();
			int pressy = e.getY();
			int w = getWidth();
			int h = getHeight();
			if (turn == Turn.WhiteTurn) {
				positions = findSquare(pressx, pressy, w, h);
				if (!isInMoves()) {
					killMovesMap.clear();
					moves = possibleMoves(positions.getY(), positions.getX());
					printKilledMap();
				} else if (true) {
					moves.removeAll(moves);
					killedUnits = killMovesMap.get(positions);
					if (killedUnits != null)
						for (int i = 0; i < killedUnits.size(); i++) {
							killUnit(killedUnits.get(i).getY(), killedUnits.get(i).getX());
							System.out
									.println("Killed: " + killedUnits.get(i).getY() + " " + killedUnits.get(i).getX());
						}
					updateUnit();
					makeKing();
					changePlayersTurn();
				}
			} else {

			}
			repaint();
			Win();
		}

		private boolean isInMoves() {
			for (Point move : moves) {
				if (move.getY() == positions.getY() && move.getX() == positions.getX())
					return true;
			}
			return false;
		}

		public void makeKing() {
			if (positions.getY() == 0 && board[0][positions.getX()].getUnit() == Unit.Black)
				board[0][positions.getX()].changeKingStatus(true);
			else if (positions.getY() == BOARD_LENGTH - 1
					&& board[BOARD_LENGTH - 1][positions.getX()].getUnit() == Unit.White)
				board[BOARD_LENGTH - 1][positions.getX()].changeKingStatus(true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if (turn == Turn.BlackTurn) {
				new BoardAI(boardPointer, board, Unit.Black, countBlack, countWhite);
				turn = Turn.WhiteTurn;
				positions = new Point();
				prevPositions = new Point();

				repaint();
			} else
				prevPositions = positions;
		}

		public void printMoves() {
			for (int i = 0; i < moves.size(); i++) {
				System.out.println("[ " + moves.get(i).getY() + " , " + moves.get(i).getX() + " ]");
			}
			System.out.println("positions: [ " + positions.getY() + " , " + positions.getX());
			System.out.println("prev positions: [ " + prevPositions.getY() + " , " + prevPositions.getX());
		}

		public void updateUnit() {
			if (board[prevPositions.getY()][prevPositions.getX()].getIsKing()) {
				board[prevPositions.getY()][prevPositions.getX()].changeKingStatus(false);
				board[positions.getY()][positions.getX()].changeKingStatus(true);
			}
			board[positions.getY()][positions.getX()]
					.setUnit(board[prevPositions.getY()][prevPositions.getX()].getUnit());
			board[prevPositions.getY()][prevPositions.getX()].setUnit(Unit.Empty);
		}

		public void changePlayersTurn() {
			if (turn == Turn.BlackTurn)
				turn = Turn.WhiteTurn;
			else
				turn = Turn.BlackTurn;
		}

	}

	public Point findSquare(int pressx, int pressy, int w, int h) {
		return new Point(pressy / (h / BOARD_LENGTH), pressx / (w / BOARD_WIDTH));
	}

	public boolean isEmpty(int x, int y) {
		return this.board[y][x].getUnit() == Unit.Empty;
	}

	public void Win() {
		if (this.countBlack == 0) {
			System.out.println("White won!");
			try {
				repaint();
				Thread.sleep(300);
				initDamcaBoard();
			} catch (InterruptedException e) {

			}
		} else if (this.countWhite == 0) {
			System.out.println("Black won!");
			try {
				repaint();
				Thread.sleep(300);
				initDamcaBoard();
			} catch (InterruptedException e) {

			}
		}

	}

	public void killUnit(int y, int x) {
		if (this.board[y][x].getUnit() == Unit.Black)
			countBlack--;
		else
			countWhite--;
		this.board[y][x].setUnit(Unit.Empty);
		if (this.board[y][x].getIsKing())
			this.board[y][x].changeKingStatus(false);
		System.out.println("New black count: " + this.countBlack + "\nNew white count: " + this.countWhite);
		repaint();

	}

	public void printKilledMap() {
		for (Point name : killMovesMap.keySet()) {
			String key = name.toString();
			String value = killMovesMap.get(name).toString();
			System.out.println(key + " " + value);
		}
	}

	public BoardSpace[][] getBoard() {
		return this.board;
	}

	public void updateCounts(int countBlack, int countWhite) {
		this.countBlack = countBlack;
		this.countWhite = countWhite;
	}
}
