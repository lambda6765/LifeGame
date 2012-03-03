import javax.swing.JButton;

public class Cell extends JButton {
	int x;
	int y;
	Cell(int y, int x) {
		this.x = x;
		this.y = y;
		setBorderPainted(false);
	}
}

