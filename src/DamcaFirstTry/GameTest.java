package DamcaFirstTry;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameTest extends JFrame {
	Board board;

	public GameTest() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		board = new Board();
		add(board);
		setSize(800, 800);
		setVisible(true);
		setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
	}

	public static void main(String[] args) {
		new GameTest();
		
	}
}
