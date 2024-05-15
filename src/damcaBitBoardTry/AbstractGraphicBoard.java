package damcaBitBoardTry;


import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public abstract class AbstractGraphicBoard extends JPanel implements BoardIE {
    protected LogicalBoard fatherBoard;
    protected Stack<Move> moveStack;

    public AbstractGraphicBoard() {
        this.fatherBoard = new LogicalBoard();
        this.moveStack = new Stack<>();

    }

    public abstract void paintUnits(Graphics g, int w, int h);

    public abstract void paintSquares(Graphics g, int w, int h);

    public abstract void showPossibleMoves(Graphics g, int w, int h);
}
