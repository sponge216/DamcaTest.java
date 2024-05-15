package damcaBitBoardTry;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class DamcaTest extends JFrame {
	AbstractGraphicBoard board;

	public DamcaTest() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		board = new GraphicPvEBoard();
		add(board);
		setSize(800, 800);
		setVisible(true);
		setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
	}

	public static void main(String[] args) {
		new DamcaTest();
	}
}