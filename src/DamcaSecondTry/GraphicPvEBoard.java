package DamcaSecondTry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import DamcaFirstTry.BoardIE;
import DamcaFirstTry.Point;
import DamcaFirstTry.UnitAndTurnIE.Turn;
import DamcaFirstTry.UnitAndTurnIE.Unit;

public class GraphicPvEBoard extends AbstractGraphicBoard implements BoardIE {
	private int unitRadius;
	private ComputerBoard pcBoard;

	public GraphicPvEBoard() {
		super();
		pcBoard = new ComputerBoard(1);
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
				unit = pcBoard.getBoard()[i][j].getUnit();
				if (unit == Unit.Black) {
					g2.setColor(Color.black);
					if (this.pcBoard.getBoard()[i][j].getIsKing())
						g2.setColor(Color.decode("#f83e3e"));
					g2.fillOval(w * j + w / 4, h * i + h / 4, unitRadius, unitRadius);
				} else if (unit == Unit.White) {
					g2.setColor(Color.white);
					if (this.pcBoard.getBoard()[i][j].getIsKing())
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
		System.out.println(playerPos);
		if ((pcBoard.getBoard()[playerPos.getY()][playerPos.getX()].getUnit() == Unit.White
				&& LogicalBoard.turn == Turn.WhiteTurn)) {
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
			}
		}
	}

	class ML extends MouseAdapter {
		Move move;

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println(LogicalBoard.turn);
			if (LogicalBoard.turn == Turn.WhiteTurn) {
				move = fatherBoard.playerMove(e.getX(), e.getY(), getWidth(), getHeight());
				pcBoard.makeComputerMove(move);
			}
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			System.out.println(LogicalBoard.turn);

			fatherBoard.playerRelease();
			if (LogicalBoard.turn == Turn.BlackTurn) {
				try {
					Thread.sleep(700);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				fatherBoard.makeMove(pcBoard.bestMove());

			}
			pcBoard.Win();
			fatherBoard.Win();
			repaint();
		}
	}
}
