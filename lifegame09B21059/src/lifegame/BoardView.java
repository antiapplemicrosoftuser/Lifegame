package lifegame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class BoardView extends JPanel implements MouseListener, MouseMotionListener {
	private int colLine;// 水平方向に引くべき直線の本数
	private int rowLine;// 垂直方向に引くべき直線の本数
	private int cellSize;
	private int startColLine;
	private int startRowLine;
	private int clickX;
	private int clickY;
	private int cellX;
	private int cellY;
	private int lastCellX = -1;
	private int lastCellY = -1;
	private BoardModel model;
	private JButton button2;

	public BoardView(BoardModel m, JButton b) {
		model = m;
		button2 = b;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g); // JPanel の描画処理（背景塗りつぶし）

		colLine = model.getCols() + 1;
		rowLine = model.getRows() + 1;
		cellSize = Math.min((this.getWidth() - 20) / (rowLine - 1), (this.getHeight() - 20) / (colLine - 1));

		startRowLine = (this.getWidth() - cellSize * (rowLine - 1)) / 2;
		startColLine = (this.getHeight() - cellSize * (colLine - 1)) / 2;
		int whiteZone = (3 * cellSize) / 20;
		int colorWidth = cellSize - (whiteZone * 2);
		for (int i = 0; i < colLine; i++) {
			g.drawLine(startRowLine, startColLine + i * cellSize, startRowLine + cellSize * (rowLine - 1),
					startColLine + i * cellSize);
		}
		for (int i = 0; i < rowLine; i++) {
			g.drawLine(startRowLine + i * cellSize, startColLine, startRowLine + i * cellSize,
					startColLine + cellSize * (colLine - 1));
		}
		for (int i = 0; i < rowLine - 1; i++) {
			for (int j = 0; j < colLine - 1; j++) {
				if (model.isAlive(i, j)) {
					g.fillRect(startRowLine + i * cellSize + whiteZone, startColLine + j * cellSize + whiteZone,
							colorWidth, colorWidth);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (culcCellsXY(e)) {
			return;
		}
		// (cellY, cellX) のセルの状態を反転させる
		model.changeCellState(cellY, cellX);
		if (button2.isEnabled() == false) {
			button2.setEnabled(true);
		}
		this.repaint();
		lastCellX = cellX;
		lastCellY = cellY;
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (culcCellsXY(e)) {
			return;
		}
		if (lastCellX == cellX && lastCellY == cellY) {
			return;
		}
		model.changeCellState(cellY, cellX);
		if (button2.isEnabled() == false) {
			button2.setEnabled(true);
		}
		this.repaint();
		lastCellX = cellX;
		lastCellY = cellY;
	}

	public void mouseMoved(MouseEvent e) {
	}

	public boolean culcCellsXY(MouseEvent e) {
		// 座標の変換
		clickX = e.getX();
		clickY = e.getY();
		if (clickX - startRowLine < 0 || clickY - startColLine < 0) {
			lastCellX = -1;
			lastCellY = -1;
			return true;
		}
		cellX = (clickX - startRowLine) / cellSize;
		cellY = (clickY - startColLine) / cellSize;
		if (cellY >= colLine - 1 || cellX >= rowLine - 1) {
			lastCellX = -1;
			lastCellY = -1;
			return true;
		}
		return false;
	}
}
