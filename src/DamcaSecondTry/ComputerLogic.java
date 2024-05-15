package DamcaSecondTry;

import java.util.ArrayList;
import java.util.Random;

import DamcaFirstTry.BoardIE;
import DamcaFirstTry.Point;
import DamcaFirstTry.UnitAndTurnIE;
import DamcaFirstTry.UnitAndTurnIE.Turn;
import DamcaFirstTry.UnitAndTurnIE.Unit;

public class ComputerLogic implements UnitAndTurnIE, BoardIE {
	private ComputerBoard pcBoard;

	public ComputerLogic() {
		this.pcBoard = new ComputerBoard(1);
	}

	public Move bestMove() {
		if (LogicalBoard.turn == Turn.BlackTurn) {

			this.pcBoard.killedUnits = new ArrayList<Point>();
			this.pcBoard.setNewPoint(new Point());
			this.pcBoard.setCurrentPoint(new Point());

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
					if (this.pcBoard.getBoard()[i][j].getUnit() == Unit.Black) {
						this.pcBoard.setMoves(possibleMoves(i, j));
						;
						// check if there are any kill moves available
						if (this.pcBoard.killMovesMap.size() != 0) {
							System.out.println("kill move!");
							// get the best kill move
							for (Point key : this.pcBoard.killMovesMap.keySet()) {
								currentKills = this.pcBoard.killMovesMap.get(key).size();
								maxKills = updateMaxKills(key, currentKills, maxKills, i, j);
							}
						} else {
							// check if no kill moves were found before, and there are available moves.
							// if so, return a non kill move.
							if (maxKills == 0 && this.pcBoard.moves.size() > 0) {
								this.pcBoard.setNewPoint(
										this.pcBoard.moves.get(new Random().nextInt(this.pcBoard.moves.size())));
								this.pcBoard.setCurrentPoint(new Point(i, j));
							}
						}
						this.pcBoard.killMovesMap.clear();
					}
				}
			}
			for (Point point : this.pcBoard.killedUnits) {
				killUnit(point.getY(), point.getX());
			}
			turnEnding();
			return new Move(this.pcBoard.getNewPoint(), this.pcBoard.getNewPoint(), this.pcBoard.killedUnits);
		}
		return null;
	}

	public void turnEnding() {
		moveKing(this.pcBoard.getCurrentPoint(), this.pcBoard.getNewPoint());
		moveUnit(this.pcBoard.getCurrentPoint(), this.pcBoard.getNewPoint());
		makeBlackKing(this.pcBoard.getNewPoint());
		changePlayersTurn();
	}

	public int updateMaxKills(Point key, int currentKills, int maxKills, int i, int j) {
		if (currentKills > maxKills) {
			this.pcBoard.killedUnits = this.pcBoard.killMovesMap.get(key);
			this.pcBoard.setNewPoint(key);
			this.pcBoard.setCurrentPoint(new Point(i, j));
			return currentKills;
		}
		return maxKills;
	}

	public void killUnit(int y, int x) {
		if (this.pcBoard.board[y][x].getUnit() == Unit.Black)
			this.pcBoard.countBlack--;
		else if (this.pcBoard.board[y][x].getUnit() == Unit.White)
			this.pcBoard.countWhite--;
		this.pcBoard.board[y][x].setUnit(Unit.Empty);
		if (this.pcBoard.board[y][x].getIsKing())
			this.pcBoard.board[y][x].changeKingStatus(false);
//		System.out.println(
//				"New black count: " + this.pcBoard.countBlack + "\nNew white count: " + this.pcBoard.countWhite);

	}

	public void makeMove(Move move) {
		System.out.println(move);
		if (move != null) {
			moveKing(move.oldPosition, move.newPosition);
			moveUnit(move.oldPosition, move.newPosition);
			makeBlackKing(move.newPosition);
			for (Point point : move.killedUnits) {
				killUnit(point.getY(), point.getX());
			}
			Win();
		}
	}

	public void moveUnit(Point currentPoint, Point newPoint) {
		this.pcBoard.board[newPoint.getY()][newPoint.getX()]
				.setUnit(pcBoard.board[currentPoint.getY()][currentPoint.getX()].getUnit());
		this.pcBoard.board[currentPoint.getY()][currentPoint.getX()].setUnit(Unit.Empty);
	}

	public void moveKing(Point currentPoint, Point newPoint) {
		if (this.pcBoard.board[currentPoint.getY()][currentPoint.getX()].getIsKing()) {
			pcBoard.board[currentPoint.getY()][currentPoint.getX()].changeKingStatus(false);
			pcBoard.board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
		}
	}

	public void makeBlackKing(Point newPoint) {
		if (newPoint.getY() == 0 && this.pcBoard.board[newPoint.getY()][newPoint.getX()].getUnit() == Unit.Black)
			this.pcBoard.board[newPoint.getY()][newPoint.getX()].changeKingStatus(true);
	}

	public ArrayList<Point> possibleMoves(int i, int j) {
		ArrayList<Point> moves = new ArrayList<Point>();
		Unit currentUnit = this.pcBoard.board[i][j].getUnit();
		if (this.pcBoard.board[i][j].getIsKing()) {
			if (currentUnit == Unit.Black) {
				moves = possibleComputerKingMoves(i, j);
			}
		} else if (currentUnit == Unit.Black)
			moves = possibleComputerMoves(i, j);
		return moves;
	}

	public ArrayList<Point> possibleComputerKingMoves(int posY, int posX) {
		return null;
//		return possibleKingMovesTemplate(posY, posX, Unit.Black);
	}

	public ArrayList<Point> possibleComputerMoves(int posY, int posX) {
		return null;
//		return possibleMovesTemplate(posY, posX, -1, Unit.Black);
	}

	public void changePlayersTurn() {
		if (LogicalBoard.turn == Turn.BlackTurn)
			LogicalBoard.turn = Turn.WhiteTurn;
		else
			LogicalBoard.turn = Turn.BlackTurn;
	}

	public void Win() {
		if (this.pcBoard.countBlack == 0) {
			System.out.println("White won!");
			try {
				Thread.sleep(300);
				this.pcBoard.initDamcaBoard();
			} catch (InterruptedException e) {

			}
		} else if (this.pcBoard.countWhite == 0) {
			System.out.println("Black won!");
			try {
				Thread.sleep(300);
				pcBoard.initDamcaBoard();
			} catch (InterruptedException e) {

			}
		}
	}
}
