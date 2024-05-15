package DamcaSecondTry;

import DamcaFirstTry.BoardSpace;
import DamcaFirstTry.Point;

import java.util.ArrayList;
import java.util.Collections;

public class ComputerBoard extends LogicalBoard {
    private static int boards = 0;
    private static int test = 0;
    private static Unit initialPcUnit = Unit.Black;
    private static int lossWeight = -12;
    private static int winWeight = 12;
    private static int endRecursion;
    private Point newPoint;
    private Point currentPoint;
    private Turn pcTurn;
    private Unit pcUnit;
    private ArrayList<Move> allMoves;

    // first instance of computer board
    public ComputerBoard(int end) {
        super();
        this.pcUnit = Unit.Black;
        this.pcTurn = Turn.WhiteTurn;
        endRecursion = end;
    }

    // instance used in recursive functions
    public ComputerBoard(BoardSpace[][] board, Turn turn, int countBlack, int countWhite, Move lastMove) {
        super();
        this.board = board;
        this.pcTurn = turn;
        this.pcUnit = getUnitByTurn();
        this.countBlack = countBlack;
        this.countWhite = countWhite;
        makeComputerMove(lastMove);

    }

    public Move bestMove() {
        if (LogicalBoard.turn == Turn.BlackTurn) {
            test = 0;
            long start1 = System.nanoTime();
            Move chosenMove;
            Move move;
            this.allMoves = new ArrayList<Move>();
            for (int i = 0; i < BOARD_LENGTH; i++) {
                for (int j = 1 - (i & 1); j < BOARD_WIDTH; j += 2) {
                    if (this.board[i][j].getUnit() == initialPcUnit) {
                        this.moves = possibleMoves(i, j);
                        for (Point movePoint : this.moves) {
                            System.out.println(movePoint + "Move!");
                            move = new Move(movePoint, new Point(i, j),
                                    this.killMovesMap.getOrDefault(movePoint, null));
                            System.out.println(i);
                            this.allMoves.add(move);
                            getNewComputerBoard(movePoint, i, j).bestMoveRecursion(move, 0, 0, this.countBlack,
                                    this.countWhite, 0);
                        }
                        this.killMovesMap.clear();
                    }
                }
            }

            if (this.allMoves.isEmpty()) {
                turnEnding(null);
                return null;
            }
            chosenMove = Collections.max(this.allMoves);
            turnEnding(chosenMove);
            System.out.println(chosenMove);
            System.out.println(turn + "end");
            Win();
            long end1 = System.nanoTime();
            System.out.println(end1 - start1);
            System.out.println(test + " test");
            return chosenMove;
        }
        return null;
    }

    public void bestMoveRecursion(Move initialMove, int end, int boardCount, int prevBlack, int prevWhite,
                                  int lineWeight) {
        lineWeight += updateLineWeight(initialMove, prevBlack, prevWhite);

        if (end == endRecursion) {
            initialMove.updateWeight(lineWeight);
            System.out.println("Line updated! " + ++test);
            return;
        }
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 1 - (i & 1); j < BOARD_WIDTH; j += 2) {
                if (this.board[i][j].getUnit() == this.pcUnit) {
                    this.moves = possibleMoves(i, j);
                    for (Point move : this.moves) {
                        ++boardCount;
                        System.out.println(this.pcTurn + " This! " + boardCount);
                        getNewComputerBoardRecursive(move, i, j).bestMoveRecursion(initialMove, end + 1, boardCount,
                                this.countBlack, this.countWhite, lineWeight);
                    }
                    this.killMovesMap.clear();
                }
            }
        }
    }

    public void makeComputerMove(Move move) {
//		System.out.println(move);
        if (move != null) {
            moveKing(move.oldPosition, move.newPosition);
            moveUnit(move.oldPosition, move.newPosition);
            makeKing(move.newPosition);
            if (!move.killedUnits.isEmpty())
                for (Point point : move.killedUnits) {
                    killUnit(point.getY(), point.getX());
                }

        }
    }



    public int updateLineWeight(Move move, int prevBlack, int prevWhite) {
        int weight = (this.countBlack - prevBlack) + (prevWhite - this.countWhite)
                + 2 * (this.countBlackKing - this.countWhiteKing);
        if (this.countBlack == 0 && this.countBlackKing == 0) // check loss
            weight += lossWeight;
        else if (this.countWhite == 0 && this.countWhiteKing == 0) // check win
            weight += winWeight;
        if (prevBlack - this.countBlack > 1)
            weight += lossWeight;
        if (prevWhite - this.countWhite > 1)
            weight += winWeight;
        return weight;
    }

    public void turnEnding(Move move) {
        makeComputerMove(move);
        this.pcTurn = LogicalBoard.turn;
        System.out.println(this.pcTurn);
    }

    public ArrayList<Point> possibleMoves(int i, int j) {
        ArrayList<Point> moves = new ArrayList<Point>();
        if (this.board[i][j].getIsKing()) {
            System.out.println("king!" + i + " " + j);
            moves = possibleComputerKingMoves(i, j, this.pcUnit);
        } else
            moves = possibleComputerMoves(i, j, this.pcUnit);
        return moves;
    }

    public Turn getOtherTurn() {
        if (this.pcTurn == Turn.BlackTurn)
            return Turn.WhiteTurn;
        return Turn.BlackTurn;
    }

    public Unit getUnitByTurn() {
        if (this.pcTurn == Turn.BlackTurn)
            return Unit.White;
        return Unit.Black;
    }

    public int getCountByUnit() {
        if (initialPcUnit == Unit.White)
            return this.countWhite;
        return this.countBlack;
    }

    public Turn getTurnByInitUnit() {
        if (initialPcUnit == Unit.Black)
            return Turn.BlackTurn;
        return Turn.WhiteTurn;
    }

    public ArrayList<Point> possibleComputerKingMoves(int posY, int posX, Unit unit) {
        return possibleKingMovesTemplate(posY, posX, unit);
    }

    public ArrayList<Point> possibleComputerMoves(int posY, int posX, Unit unit) {
        if (unit == Unit.Black)
            return possibleMovesTemplate(posY, posX, -1, unit);
        return possibleMovesTemplate(posY, posX, +1, unit);

    }

    public Point getNewPoint() {
        return this.newPoint;
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public Point getCurrentPoint() {
        return this.currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }

    public ArrayList<Point> getMoves() {
        return this.moves;
    }

    public void setMoves(ArrayList<Point> moves) {
        this.moves = moves;
    }

    public BoardSpace[][] getCopyOfBoard() {
        BoardSpace[][] newBoard = new BoardSpace[BOARD_LENGTH][BOARD_WIDTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                newBoard[i][j] = new BoardSpace();
                newBoard[i][j].copy(this.board[i][j]);
            }
        }
        return newBoard;
    }

    public ComputerBoard getNewComputerBoard(Point move, int i, int j) {
        System.out.println("Board no: " + ++boards);

        return new ComputerBoard(getCopyOfBoard(), getOtherTurn(), this.countBlack, this.countWhite,
                new Move(move, new Point(i, j), this.killMovesMap.getOrDefault(move, null)));
    }

    public ComputerBoard getNewComputerBoardRecursive(Point move, int i, int j) {
        System.out.println("Board no: " + ++boards);

        return new ComputerBoard(getCopyOfBoard(), getOtherTurn(), this.countBlack, this.countWhite,
                new Move(move, new Point(i, j), this.killMovesMap.getOrDefault(move, null)));
    }
}
