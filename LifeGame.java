import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class LifeGame extends JFrame implements ActionListener {
	int generation = 0;
	int n = 80; // number of cell is n*n
	int w = 500; // size of window
	int h = 650;
	int wait = 100; //msec
	Panel top;
	Panel display;
	Panel buttons;
	JButton[] bs;
	Cell[][] cs;
	JMenuBar mb;
	JMenu mn;
	JMenuItem[] mi;
	JLabel msg;
	int[][] state;
	Boolean running = false;
	// 36x9
	int[][] glidergun =
	{
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
			{1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	};

	int[][] acorn =
	{
			{0,1,0,0,0,0,0},
			{0,0,0,1,0,0,0},
			{1,1,0,0,1,1,1}
	};

	int[][] nebura =
	{
			{1,1,1,1,1,1,0,1,1},
			{1,1,1,1,1,1,0,1,1},
			{0,0,0,0,0,0,0,1,1},
			{1,1,0,0,0,0,0,1,1},
			{1,1,0,0,0,0,0,1,1},
			{1,1,0,0,0,0,0,1,1},
			{1,1,0,0,0,0,0,0,0},
			{1,1,0,1,1,1,1,1,1},
			{1,1,0,1,1,1,1,1,1}
	};

	LifeGame() {
		setTitle("The Game of Life");
		setSize(w, h);

		addWindowListener(new Closing());

		// init display
		display = new Panel();
		display.setLayout(new GridLayout(n, n));
		add(display, BorderLayout.CENTER);

		// init buttons
		buttons = new Panel();
		buttons.setLayout(new GridLayout(1, 4));
		bs = new JButton[4];
		bs[0] = new JButton("start");
		bs[1] = new JButton("stop");
		bs[2] = new JButton("clear");
		bs[3] = new JButton("random");
		for(int i = 0; i < bs.length; i++) {
			bs[i].addActionListener(this);
			buttons.add(bs[i]);
		}
		add(buttons, BorderLayout.SOUTH);

		/// init top
		top = new Panel();
		top.setLayout(new GridLayout(2,1));

		// init menu
		mb = new JMenuBar();
		mn = new JMenu();
		mn.setText("Demo");
		mi = new JMenuItem[4];
		for(int i = 0; i < mi.length; i++) {
			mi[i] = new JMenuItem();
			mi[i].addActionListener(this);
			mn.add(mi[i]);
		}

		mi[0].setText("Glider gun");
		mi[1].setText("Acorn");
		mi[2].setText("10 cell row");
		mi[3].setText("Nebura");
		mb.add(mn);
		top.add(mb);

		// init message
		msg = new JLabel("", JLabel.CENTER);
		top.add(msg);
		add(top, BorderLayout.NORTH);

		// init cells
		cs = new Cell[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				cs[i][j] = new Cell(i, j);
				cs[i][j].addActionListener(this);
				display.add(cs[i][j]);
			}
		}

		state = new int[n][n];
		clearState();
		update();
		validate();
		setVisible(true);
	}

	public void mainloop() {
		while(true){
			if(running) {
				nextGeneration();
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void glidergunState() {
		clearState();
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 36; j++)
				state[i][j] = glidergun[i][j];
	}

	public void acornState() {
		clearState();
		int pos = n/2;
		for(int i = 0; i < 3; i++)
			for(int j = 0;j < 7; j++)
				state[pos+i][pos+20+j] = acorn[i][j];
	}

	public void row10State() {
		clearState();
		int pos = n/2;
		for(int i = 0; i < 10; i++)
			state[pos][pos-5+i] = 1;
	}

	public void neburaState() {
		clearState();
		int pos = n/2;
		for(int i = 0; i < 9; i++)
			for(int j = 0;j < 9; j++)
				state[pos-4+i][pos-4+j] = nebura[i][j];
	}

	public void randomState() {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				int r = (int) (Math.random()*10);
				if(r < 1)
					state[i][j] = 1;
			}
		}
	}

	public void clearState() {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				state[i][j] = 0;
			}
		}
	}

	public void nextGeneration() {
		int sum = 0;
		int[][] next = new int[n][n];
		generation++;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				int around = 0;
				for(int dy = -1; dy <= 1; dy++) {
					for(int dx = -1; dx <= 1; dx++) {
						if(dy != 0 || dx != 0) {// skip itself
							if(0 <= i+dy && i+dy < n && 0 <= j+dx && j+dx < n ) {
								if(state[i+dy][j+dx] == 1) {
									around++;
								}
							}
						}
					}
				}
				if(state[i][j] == 1) {
					if(around <= 1 || around >= 4) {
						next[i][j] = 0;
						cs[i][j].setBackground(Color.black);
					}
					else {
						next[i][j] = 1;
						cs[i][j].setBackground(Color.blue);
						sum++;
					}
				}
				else if(around == 3) {
					next[i][j] = 1;
					cs[i][j].setBackground(Color.blue);
					sum++;
				}
				else {
					next[i][j] = 0;
					cs[i][j].setBackground(Color.black);
				}
			}
		}
		state = next;
		setMessage(sum);
	}

	public void setColor() {
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				if(state[i][j] == 0)
					cs[i][j].setBackground(Color.black);
				else if(state[i][j] == 1)
					cs[i][j].setBackground(Color.blue);
	}

	public void setMessage() {
		setMessage(countCell());
	}

	public void setMessage(int i) {
		msg.setText("Generation: " + generation + "   Cells: " + i);
	}

	public void update() {
		setMessage();
		setColor();
	}

	public int countCell() {
		int ret = 0;
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				ret += state[i][j];
		return ret;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			String text = ((AbstractButton) src).getText();
			if(text == "start") {
				if(countCell() != 0)
					running = true;
			}
			else if(text == "stop") {
				running = false;
			}
			else if(text == "clear") {
				running = false;
				generation = 0;
				clearState();
				update();
			}
			else if(text == "random") {
				generation = 0;
				randomState();
				update();
			}
			else if(text == "Glider gun") {
				running = false;
				generation = 0;
				glidergunState();
				update();
			}
			else if(text == "Acorn") {
				running = false;
				generation = 0;
				acornState();
				update();
			}
			else if(text == "10 cell row") {
				running = false;
				generation = 0;
				row10State();
				update();
			}
			else if(text == "Nebura") {
				running = false;
				generation = 0;
				neburaState();
				update();
			}
			else {// cell clicked
				int x = ((Cell)src).x;
				int y = ((Cell)src).y;
				state[y][x] = state[y][x] ^ 1;
				update();
			}
	}
}
