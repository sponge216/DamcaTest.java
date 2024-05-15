package damcaBitBoardTry;


import java.util.ArrayList;
import java.util.HashMap;

public class LogicalBoard implements UnitAndMasksIE,TurnIE {
    // Later change turn to be as input;
    public static Turn turn = Turn.WhiteTurn;
    protected long row0T7; // has 8 spaces
    protected long row8T15; // has 8 spaces
    protected long row16T23; // has 8 spaces
    protected long row24T31; // has 8 spaces
    protected ArrayList<Point> moves;
    protected HashMap<Point, ArrayList<Point>> killMovesMap;
    protected ArrayList<Point> killedUnits;
    protected Point positions;
    protected Point prevPositions;
    protected int countBlack;
    protected int countWhite;
    protected int countBlackKing;
    protected int countWhiteKing;

    public LogicalBoard() {

        initDamcaBoard();
    }

    public void initDamcaBoard() {
        this.moves = new ArrayList<Point>();
        this.killMovesMap = new HashMap<Point, ArrayList<Point>>();
        this.killedUnits = new ArrayList<Point>();
        this.positions = new Point();
        this.prevPositions = new Point();
        this.countBlack = 12;
        this.countWhite = 12;
        this.countBlackKing = 0;
        this.countWhiteKing = 0;
        turn = Turn.WhiteTurn;
        row0T7 = 00303030330303030L; // 011, 001, 000
        row8T15 = 030303030L;
        row16T23 = 00101010100000000L;
        row24T31 = 00101010110101010L;

    }

    public Unit getCell(int row, int column) {
        long x = 3 * ((row & 1) * 8 + column);
        if (row < 2) {
            return getUnitByValue((row0T7 >> x) & cellMask);
        } else if (row < 4) {
            return getUnitByValue((row8T15 >> x) & cellMask);
        } else if (row < 6) {
            return getUnitByValue((row16T23 >> x) & cellMask);
        } else {
            return getUnitByValue((row24T31 >> x) & cellMask);
        }
    }

    public Unit getUnitByValue(long unit) {
        if (unit == 0)
            return Unit.Empty;
        else if (unit == 0b1)
            return Unit.White;
        else if (unit == 0b11)
            return Unit.Black;
        else if (unit == 0b101)
            return Unit.WhiteKing;
        else if (unit == 0b111)
            return Unit.BlackKing;

        System.out.println("UNIT BY VAL ERROR: " + unit);
        return null;
    }


    public boolean isKing(int row, int column) {
        Unit unit = getCell(row, column);

        return unit == Unit.BlackKing || unit == Unit.WhiteKing;
    }

    public ArrayList<Point> possibleMoves(int row, int column) {
        ArrayList<Point> moves = new ArrayList<Point>();
        Unit unit = getCell(row, column);
        if (isKing(row, column)) {
            if (unit == Unit.WhiteKing && turn == Turn.WhiteTurn) {
                moves = possibleWhiteKingMoves(row, column);
            } else if (unit == Unit.BlackKing && turn == Turn.BlackTurn) {
                moves = possibleBlackKingMoves(row, column);
            }
        } else if (unit == Unit.White && turn == Turn.WhiteTurn)
            moves = possibleWhiteMoves(row, column);
        else if (unit == Unit.Black && turn == Turn.BlackTurn)
            moves = possibleBlackMoves(row, column);
        return moves;
    }

    public ArrayList<Point> possibleWhiteKingMoves(int posY, int posX) {
        return possibleKingMovesTemplate(posY, posX, Unit.White);

    }

    public ArrayList<Point> possibleBlackKingMoves(int posY, int posX) {
        return possibleKingMovesTemplate(posY, posX, Unit.Black);

    }

    public ArrayList<Point> possibleWhiteMoves(int posY, int posX) {
        return possibleMovesTemplate(posY, posX, -1, Unit.White);
    }

    public ArrayList<Point> possibleBlackMoves(int posY, int posX) {
        return possibleMovesTemplate(posY, posX, +1, Unit.Black);
    }

    public ArrayList<Point> possibleMovesTemplate(int posY, int posX, int change, Unit unit) {
        ArrayList<Point> moves = new ArrayList<Point>();
        ArrayList<Point> killedUnits = new ArrayList<Point>();
        int xDir;
        Point point = new Point();
        for (int i = posX - 1; i < posX + 2 && isInRangeY(posY + change); i += 2) {
            xDir = i - posX;
            if (isInRangeX(i) && (getCell(posY + change, i) != unit)) {

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
        ArrayList<Point> killedUnitsLocal = new ArrayList<Point>(killedUnits);
        int xDir;
        Point point = new Point();
        for (int i = posX - 1; i < posX + 2 && isInRangeY(posY + change); i += 2) {
            xDir = i - posX;
            if (isInRangeX(i) && (getCell(posY + change, i) != unit)) {
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
                while (isInRangeX(x) && isInRangeY(y) && (isEmpty(x, y))) {
                    moves.add(new Point(y, x));
                    //moves.addAll(possibleKingKillsTemplate(y, x, xDir, yDir, unit, killedUnits));
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
        if (isInRangeY(y) && isInRangeX(x) && getCell(y, x) == unit) {
            return isInRangeY(y + yDir) && isInRangeX(x + xDir) && isEmpty(x + xDir, y + yDir);

        }
        return false;
    }

    public boolean canKillTemplate(int xDir, int x, int y, int change, Unit unit) {
        if (unit == Unit.White)
            unit = Unit.Black;
        else if (unit == Unit.Black)
            unit = Unit.White;

        if (getCell(y, x) == unit && isInRangeY(y + change)) {
            if (xDir < 0 && isInRangeX(x - 1) && isEmpty(x - 1, y + change)) {
                return true;
            } else return xDir > 0 && isInRangeX(x + 1) && isEmpty(x + 1, y + change);
        }
        return false;
    }

    public boolean isInRangeX(int x) {
        return x < 8 && x > -1;
    }

    public boolean isInRangeY(int y) {
        return y < 8 && y > -1;
    }

    public boolean isEmpty(int x, int y) {
        return getCell(y, x) == Unit.Empty;
    }

    public boolean isInMoves() {
        for (Point move : this.moves) {
            if (move.equals(this.positions))
                return true;
        }
        return false;
    }

    public void makeKing() {
        if (this.positions.getY() == 7 && getCell(7, positions.getX()) == Unit.Black) {
            changeKingStatus(7, positions.getX(), true);
            this.countBlackKing++;

        } else if (this.positions.getY() == 0 && getCell(0, this.positions.getX()) == Unit.White) {
            changeKingStatus(0, positions.getX(), true);
            this.countWhiteKing++;

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
        setUnit(this.positions.getY(), this.positions.getX(),
                getCell(this.prevPositions.getY(), this.prevPositions.getX()));
        setUnit(this.prevPositions.getY(), this.prevPositions.getX(), Unit.Empty);
        if (isKing(this.prevPositions.getY(), this.prevPositions.getX())) {
            changeKingStatus(this.prevPositions.getY(), this.prevPositions.getX(), false);
            changeKingStatus(this.positions.getY(), this.positions.getX(), true);
        }


    }

    public void changePlayersTurn() {
        if (turn == Turn.BlackTurn)
            turn = Turn.WhiteTurn;
        else
            turn = Turn.BlackTurn;
    }

    public Point findSquare(int pressx, int pressy, int w, int h) {
        pressy /= (h / 8);
        pressx /= (w / 8);

        return new Point(pressy, pressx);
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
        Unit unit = getCell(y, x);
        if (!isKing(y, x)) {

            if (unit == Unit.Black)
                this.countBlack--;
            else
                this.countWhite--;

        } else {
            changeKingStatus(y, x, false);
            if (getCell(y, x) == Unit.BlackKing)
                this.countBlackKing--;
            else
                this.countWhiteKing--;
        }
        setUnit(y, x, Unit.Empty);

        System.out.println("New black count: " + this.countBlack + "\nNew white count: " + this.countWhite);
        System.out.println("New blackKing count: " + this.countBlackKing + "\nNew whiteKing count: " + this.countWhiteKing);

    }

    public void changeKingStatus(int row, int column, boolean state) {
        int x = 3 * ((row & 1) * 8 + column);
        long m = kingMask;
        if (state) {
            m = m << x;
            if (row < 2) {
                row0T7 |= m;
            } else if (row < 4) {
                row8T15 |= m;
            } else if (row < 6) {
                row16T23 |= m;
            } else {
                row24T31 |= m;
            }
        } else {
            m = ~(m << x);
            if (row < 2) {
                row0T7 &= m;
            } else if (row < 4) {
                row8T15 &= m;
            } else if (row < 6) {
                row16T23 &= m;
            } else {
                row24T31 &= m;
            }
        }


    }

    public void setUnit(int row, int column, Unit unit) {
        long mask = unit.getUnit();
        int x = 3 * ((row & 1) * 8 + column);
        long m = ~(0b111L << x);

        if (row < 2) {

            row0T7 &= m;
            row0T7 |= mask << x;

        } else if (row < 4) {

            row8T15 &= m;
            row8T15 |= mask << x;

        } else if (row < 6) {

            row16T23 &= m;
            row16T23 |= mask << x;

        } else {

            row24T31 &= m;
            row24T31 |= mask << x;

        }
    }

    public void printKilledMap() {
        for (Point name : this.killMovesMap.keySet()) {
            String key = name.toString();
            String value = this.killMovesMap.get(name).toString();
            System.out.println(key + " " + value);
        }
    }

    public Point getPositions() {
        return this.positions;
    }

    public void playerRelease() {
        changePlayersTurn();
    }

    // for pvp
    public Move playerMove(int pressx, int pressy, int w, int h) {


        this.positions = findSquare(pressx, pressy, w, h);
        if (!isInMoves()) {
            this.killMovesMap.clear();
            this.moves = possibleMoves(this.positions.getY(), this.positions.getX());
            printKilledMap();
        } else if (true) {
            this.moves = new ArrayList<>();
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
            Win();
            return new Move(this.positions, this.prevPositions, this.killedUnits);
        }
        Win();

        return null;
    }

    public void makeMove(Move move) {
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
        setUnit(newPoint.getY(), newPoint.getX(), getCell(currentPoint.getY(), currentPoint.getX()));
        setUnit(currentPoint.getY(), currentPoint.getX(), Unit.Empty);
    }

    public void moveKing(Point currentPoint, Point newPoint) {
        if (isKing(currentPoint.getY(), currentPoint.getX())) {
            changeKingStatus(currentPoint.getY(), currentPoint.getX(), false);
            changeKingStatus(newPoint.getY(), newPoint.getX(), true);
        }
    }

    public void makeKing(Point newPoint) {
        if (newPoint.getY() == 0 && getCell(newPoint.getY(), newPoint.getX()) == Unit.Black)
            changeKingStatus(newPoint.getY(), newPoint.getX(), true);
        else if (newPoint.getY() == 7 && getCell(newPoint.getY(), newPoint.getX()) == Unit.White)
            changeKingStatus(newPoint.getY(), newPoint.getX(), true);
    }
}

