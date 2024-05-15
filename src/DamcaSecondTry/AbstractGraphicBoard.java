package DamcaSecondTry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import DamcaFirstTry.BoardIE;
import DamcaFirstTry.Point;
import DamcaFirstTry.UnitAndTurnIE.Turn;
import DamcaFirstTry.UnitAndTurnIE.Unit;

public abstract class AbstractGraphicBoard extends JPanel implements BoardIE {
	protected LogicalBoard fatherBoard;

	public AbstractGraphicBoard() {
		this.fatherBoard = new LogicalBoard();

	}

	public abstract void paintUnits(Graphics g, int w, int h);

	public abstract void paintSquares(Graphics g, int w, int h);

	public abstract void showPossibleMoves(Graphics g, int w, int h);
}
