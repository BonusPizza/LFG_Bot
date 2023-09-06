package me.bonuspizza;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	JFrame frame;
	JLabel label;

	int width, height;

	public Gui(int width, int height) {

		this.width = width;
		this.height = height;

		frame = new JFrame("BonusLFGBot");
		frame.setBounds(0, 0, width, height);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);

		label = new JLabel("Online!");
		label.setForeground(Color.GREEN);
		label.setBounds(width / 2 - 27, 0, 100, 60);
		frame.add(label);

		frame.setVisible(true);

	}

	public void error() {
		frame.setVisible(false);
		label.setText("Lost Connection!");
		label.setForeground(Color.RED);
		label.setBounds(width / 2 - 55, 0, 120, 60);
		frame.setVisible(true);
	}

}
