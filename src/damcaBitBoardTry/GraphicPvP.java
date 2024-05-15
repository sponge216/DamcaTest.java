package damcaBitBoardTry;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphicPvP extends AbstractGraphicBoard implements UnitAndMasksIE,TurnIE {
    private int unitRadius;

    public GraphicPvP() {
        super();
        addMouseListener(new ML());

    }

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
                unit = this.fatherBoard.getCell(i, j);
                if (unit == Unit.Black || unit == Unit.BlackKing) {
                    g2.setColor(Color.black);
                    if (this.fatherBoard.isKing(i, j))
                        g2.setColor(Color.decode("#f83e3e"));
                    g2.fillOval(w * j + w / 4, h * i + h / 4, unitRadius, unitRadius);
                } else if (unit == Unit.White || unit == Unit.WhiteKing) {
                    g2.setColor(Color.white);
                    if (this.fatherBoard.isKing(i, j))
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
        Point playerPos = super.fatherBoard.getPositions();
        Unit unit = super.fatherBoard.getCell(playerPos.getY(), playerPos.getX());

//        System.out.println(playerPos);
        if (((unit == Unit.White || unit == Unit.WhiteKing) && LogicalBoard.turn == Turn.WhiteTurn) || ((unit == Unit.Black || unit == Unit.BlackKing) && LogicalBoard.turn == Turn.BlackTurn)) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Point currentPos;
            g2.setColor(Color.blue);
            g2.drawRect(playerPos.getX() * w, playerPos.getY() * h, w, h);

            g2.setColor(Color.red);
            for (int i = 0; i < super.fatherBoard.moves.size(); i++) {
                currentPos = super.fatherBoard.moves.get(i);
                g2.drawRect(currentPos.getX() * w, currentPos.getY() * h, w, h);
//                System.out.println("row: " + currentPos.getY() + " col: " + currentPos.getX());
            }
        }
    }

    class ML extends MouseAdapter {
        Move move;

        @Override
        public void mousePressed(MouseEvent e) {
//            System.out.println(LogicalBoard.turn);
            move = fatherBoard.playerMove(e.getX(), e.getY(), getWidth(), getHeight());
            if (move != null) {
                moveStack.add(new Move(new Point(move.newPosition), new Point(move.oldPosition), move.killedUnits));
            }
            repaint();

        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            System.out.println(LogicalBoard.turn);
            fatherBoard.prevPositions = fatherBoard.positions;
            repaint();

        }
    }

}
