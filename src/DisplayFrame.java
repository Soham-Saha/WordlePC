import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author Soham Saha
 **/
public class DisplayFrame {
	static JFrame frm = new JFrame("WordlePC");
	static JPanel pnl = new JPanel();
	static WordleCell[][] grid;
	static int cell = 53;
	static Dimension key = new Dimension(cell - 10, cell);
	static int line = 0;
	static String word;
	static String input = "";
	static String status = "";
	static Vector<String> words = new Vector<String>();
	static Color green = new Color(83, 141, 78), yellow = new Color(181, 159, 59), gray = new Color(58, 58, 60),
			bordercolor = new Color(58, 58, 60), bgcolor = new Color(18, 18, 19),
			filledBoxTextColor = new Color(215, 218, 220), unfilledBoxTextColor = new Color(215, 218, 220),
			defaultKeyColor = new Color(129, 131, 132);
	static int frmWIDTH = 10 * key.width + 5 * 11;
	static int frmHEIGHT = cell * 7 + key.height * 3 + 5 * 13;
	static char[] keynames = { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K',
			'L', ' ', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', ' ' };
	static JButton[] keys = new JButton[28];
	static int cellTextFontSize = 32;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new JButton(keynames[i] + "");
			if (i == 19) {
				keys[i].setText("ENTER");
			} else if (i == 27) {
				keys[i].setText("BKSP");
			}
			keys[i].setSize(key);
			keys[i].setBackground(defaultKeyColor);
			keys[i].setForeground(filledBoxTextColor);
			keys[i].setFont(new Font("Clear Sans", Font.BOLD, cellTextFontSize - 19));
			keys[i].setBorder(null);
			final int ct = i;
			keys[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (ct == 19) {
							passKeyCode(KeyEvent.VK_ENTER);
						} else if (ct == 27) {
							passKeyCode(KeyEvent.VK_BACK_SPACE);
						} else {
							passKeyCode(keys[ct].getText().charAt(0));
						}
						frm.requestFocusInWindow();
					} catch (InterruptedException e2) {
						// TODO: handle exception
					}
				}
			});
		}
		Scanner scan = new Scanner(ClassLoader.getSystemClassLoader().getResourceAsStream("wordlelist.txt"));
		while (scan.hasNext()) {
			words.add(scan.next().trim().toUpperCase());
		}
		scan.close();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setIconImage(Toolkit.getDefaultToolkit().getImage(DisplayFrame.class.getResource("/wordleicon.png")));
		frm.setSize(frmWIDTH + 16, frmHEIGHT + 40);
		frm.getContentPane().setBackground(bgcolor);
		frm.setLayout(null);
		frm.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frm.getSize().width / 2,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frm.getSize().height / 2);
		frm.setResizable(false);
		frm.setFocusable(true);
		frm.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					passKeyCode(e.getKeyCode());
				} catch (InterruptedException e2) {
					// TODO: handle exception
				}
			}
		});
		// keyboard display
		for (int i = 0; i < keys.length; i++) {
			JButton now = keys[i];
			if (i < 10) {
				now.setLocation((key.width + 5) * i + 5, frmHEIGHT - key.height * 3 - 5 * 3);
			} else if (i >= 10 && i <= 18) {
				now.setLocation((key.width + 5) * (i - 10) + 5 + frmWIDTH / 2 - (key.width * 9 + 5 * 10) / 2,
						frmHEIGHT - key.height * 2 - 5 * 2);
			} else if (i > 18) {
				now.setLocation((key.width + 5) * (i - 19) + 5 + frmWIDTH / 2 - (key.width * 9 + 5 * 10) / 2,
						frmHEIGHT - key.height * 1 - 5 * 1);
			}
			if (i == 19) {
				now.setLocation(5, now.getY());
				now.setSize(now.getWidth() + frmWIDTH / 2 - (key.width * 9 + 5 * 10) / 2, now.getHeight());
			} else if (i == 27) {
				now.setSize(now.getWidth() + frmWIDTH / 2 - (key.width * 9 + 5 * 10) / 2, now.getHeight());
			}
			frm.add(now);
		}
		frm.setVisible(true);
		UIManager.put("OptionPane.background", bgcolor);
		UIManager.put("Panel.background", bgcolor);
		UIManager.put("OptionPane.messageForeground", filledBoxTextColor);
		while (true) {
			grid = new WordleCell[5][6];
			word = chooseWord();
			display();
			status = "";
			line = 0;
			input = "";
			while (status.equals("")) {
				System.out.print("");
			}
			if (status.equals("won")) {
				if (JOptionPane.showConfirmDialog(frm, "You have won.\nDo you want to play again?", "WIN",
						JOptionPane.YES_NO_OPTION) == 0) {
				} else {
					break;
				}
			} else {
				if (JOptionPane.showConfirmDialog(frm,
						"You have lost. The word was " + word + ".\nDo you want to play again?", "LOST",
						JOptionPane.YES_NO_OPTION) == 0) {
				} else {
					break;
				}
			}
			for (JButton x : keys) {
				x.setBackground(defaultKeyColor);
			}
		}
		frm.dispose();
		System.exit(0);
	}

	protected static void passKeyCode(int keyCode) throws InterruptedException {
		if (status.equals("")) {
			if (keyCode == KeyEvent.VK_ENTER && input.length() == 5) {
				// if (words.contains(input)) {
				changeGrid(true);
				input = "";
				line++;
				// }
			} else if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if (input.length() > 0) {
					input = input.substring(0, input.length() - 1);
				}
			} else {
				char s = (char) keyCode;
				if (s >= 65 && s <= 90) {
					if (input.length() < 5) {
						input += s;
					}
				}
			}
			changeGrid(false);
		}

	}

	public static void changeGrid(boolean enter) throws InterruptedException {
		if (line > 5) {
			status = "lost";
		} else {
			boolean win = enter ? true : false;
			for (int i = 0; i < grid.length; i++) {
				if (i < input.length()) {
					Color col = null;
					if (!enter) {
						col = bgcolor;
					} else {
						if (word.contains(input.charAt(i) + "")) {
							if (word.charAt(i) == input.charAt(i)) {
								col = green;
								keys[new String(keynames).indexOf(input.charAt(i))].setBackground(col);
							} else {
								col = yellow;
								JButton btn = keys[new String(keynames).indexOf(input.charAt(i))];
								if (!btn.getBackground().equals(green)) {
									btn.setBackground(col);
								}
								win = false;
							}
						} else {
							col = gray;
							keys[new String(keynames).indexOf(input.charAt(i))].setBackground(col);
							win = false;
						}
					}
					grid[i][line] = new WordleCell(col, input.charAt(i));
				} else {
					grid[i][line] = null;
				}
			}
			if (win) {
				status = "won";
			}
			display();
		}
	}

	private static String chooseWord() {
		Collections.shuffle(words);
		return words.firstElement().trim().toUpperCase();
	}

	@SuppressWarnings("serial")
	public static void display() {
		frm.remove(pnl);
		pnl = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				// background
				g.setColor(bgcolor);
				g.fillRect(0, 0, frm.getWidth(), frm.getHeight());
				// divider
				g.setColor(bordercolor);
				g.drawLine(5, cell + 6, frmWIDTH - 5, cell + 6);
				// title
				g.setFont(new Font("Clear Sans", Font.BOLD, cellTextFontSize + 4));
				g.setColor(Color.white);
				g.drawString("WordlePC", frmWIDTH / 2 - g.getFontMetrics().stringWidth("WordlePC") / 2, cell - 10);
				g.setFont(new Font("Clear Sans", Font.BOLD, cellTextFontSize));
				int cell_x_incr = frmWIDTH / 2 - (cell * 5 + 5 * 6) / 2;
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						if (grid[i][j] == null) {
							g.setColor(bordercolor);
							g.drawRect(i * (cell + 5) + 5 + cell_x_incr, (j + 1) * (cell + 5) + 5 + 5, cell, cell);
							g.drawRect(i * (cell + 5) + 7 + cell_x_incr, (j + 1) * (cell + 5) + 7 + 5, cell - 4,
									cell - 4);
						} else {
							if (!bgcolor.equals(grid[i][j].col)) {
								g.setColor(grid[i][j].col);
								g.fillRect(i * (cell + 5) + 5 + cell_x_incr, (j + 1) * (cell + 5) + 5 + 5, cell, cell);
								g.setColor(filledBoxTextColor);
							} else {
								g.setColor(bordercolor);
								g.drawRect(i * (cell + 5) + 5 + cell_x_incr, (j + 1) * (cell + 5) + 5 + 5, cell, cell);
								g.drawRect(i * (cell + 5) + 7 + cell_x_incr, (j + 1) * (cell + 5) + 7 + 5, cell - 4,
										cell - 4);
								g.setColor(unfilledBoxTextColor);
							}
							Rectangle2D bound = g.getFontMetrics().getStringBounds(grid[i][j].name + "", g);
							g.drawString(grid[i][j].name + "",
									i * (cell + 5) + 5 + cell / 2 - (int) bound.getWidth() / 2 + cell_x_incr,
									(j + 1) * (cell + 5) + 5 + 5 + (int) bound.getHeight() + cell / 2
											- (int) bound.getHeight() / 2 - 9);
						}
					}
				}
			};
		};
		pnl.setSize(frmWIDTH, frmHEIGHT - key.height * 3 - 5 * 3);
		frm.add(pnl);
		frm.invalidate();
		frm.validate();
		frm.repaint();

	}
}
