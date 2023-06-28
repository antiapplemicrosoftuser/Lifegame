package lifegame;

import java.util.Deque;
import java.util.ArrayDeque;

// ライフゲームの盤面を持つ
public class BoardModel {
	private int cols;// 水平方向の行数
	private int rows;// 垂直方向の列数
	private boolean[][] cells;// 各セルの状態(生or死)を格納する2次元配列
	private static int[][] direction = new int[][] { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 },
			{ 0, -1 }, { 1, -1 } }; // セルを囲むセルの方向を格納する2次元配列
	private Deque<boolean[][]> history;// 盤面の履歴を格納する
	private boolean[][] copyCells = null;

	/**
	 * 盤面を作成 引数:水平方向の列数,垂直方向の行数
	 */
	public BoardModel(int c, int r) {
		cols = c;
		rows = r;
		cells = new boolean[rows][cols];
		history = new ArrayDeque<>();
	}

	/**
	 * 外部からcolsを読み出す
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * 外部からrowsを読み出す
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * 盤面の内容を出力する
	 */
	public void printForDebug() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (cells[i][j]) {
					System.out.print("*");
				} else {
					System.out.print(".");
				}
			}
			System.out.print("\n");
		}
		System.out.println("---------------------------------");
	}

	/**
	 * (x, y) でy行x列セルの状態を変更する。 死んでいる状態なら生きている状態にし、生きている状態なら死んでいる状態にする。
	 */
	public void changeCellState(int x, int y) {
		addHistory();
		boolean[][] newcells = new boolean[rows][cols];
		// cellsをコピー
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = cells[i][j];
			}
		}
		newcells[y][x] = !newcells[y][x];
		cells = newcells;
	}

	/**
	 * 1回呼び出すごとに盤面の状態をライフゲームのルールに沿って1世代更新してからfireUpdateを呼び出す
	 */
	public void next() {
		addHistory();
		boolean[][] newcells = new boolean[rows][cols];// 各セルの新しい状態(生or死)を格納する2次元配列
		int count = 0;// 周囲の生きているセルの数をカウント
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				count = 0;
				for (int k = 0; k < 8; k++) {
					// 配列外参照をブロック(縦方向)
					if (i + direction[k][0] < 0 || i + direction[k][0] >= rows) {
						continue;
					}
					// 配列外参照をブロック(横方向)
					if (j + direction[k][1] < 0 || j + direction[k][1] >= cols) {
						continue;
					}
					// 生きているセルをカウント
					if (cells[i + direction[k][0]][j + direction[k][1]]) {
						count++;
					}
				}
				// 前の世代でセルが生きていた場合
				if (cells[i][j]) {
					if (count == 2 || count == 3) {
						newcells[i][j] = cells[i][j];
					}
				}
				// 前の世代でセルが死んでいた場合
				else {
					if (count == 3) {
						newcells[i][j] = !cells[i][j];
					}
				}
			}
		}
		// cellsの更新
		cells = newcells;

		// fireUpdateの呼び出し
	}

	public void undo() {
		cells = history.pollLast();
		rows = cells.length;
		cols = cells[0].length;
	}

	public boolean isUndoable() {
		if (history.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isAlive(int y, int x) {
		return cells[y][x];
	}

	public void addcellsRight(int vel) {
		if (rows + vel < 1) {
			return;
		}
		addHistory();
		boolean[][] newcells = new boolean[rows + vel][cols];
		// cellsをコピー
		for (int i = 0; i < Math.min(rows, rows + vel); i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = cells[i][j];
			}
		}
		rows = rows + vel;
		cells = newcells;
	}

	public void addcellsLeft(int vel) {
		if (rows + vel < 1) {
			return;
		}
		addHistory();
		boolean[][] newcells = new boolean[rows + vel][cols];
		// cellsをコピー
		for (int i = Math.max(0, vel); i < rows + vel; i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = cells[i - vel][j];
			}
		}
		rows = rows + vel;
		cells = newcells;
	}

	public void addcellsBottom(int vel) {
		if (cols + vel < 1) {
			return;
		}
		addHistory();
		boolean[][] newcells = new boolean[rows][cols + vel];
		// cellsをコピー
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < Math.min(cols, cols + vel); j++) {
				newcells[i][j] = cells[i][j];
			}
		}
		cols = cols + vel;
		cells = newcells;
	}

	public void addcellsTop(int vel) {
		if (cols + vel < 1) {
			return;
		}
		addHistory();
		boolean[][] newcells = new boolean[rows][cols + vel];
		// cellsをコピー
		for (int i = 0; i < rows; i++) {
			for (int j = Math.max(0, vel); j < cols + vel; j++) {
				newcells[i][j] = cells[i][j - vel];
			}
		}
		cols = cols + vel;
		cells = newcells;
	}

	private void addHistory() {
		// 過去の状態の格納
		if (history.size() == 32) {
			history.poll();
		}
		history.addLast(cells);
	}

	public void writeCells(int i, int j, boolean[][] tmpcells) {
		addHistory();
		boolean[][] newcells = new boolean[i][j];
		for (int a = 0; a < i; a++) {
			for (int b = 0; b < j; b++) {
				newcells[a][b] = tmpcells[a][b];
			}
		}
		rows = i;
		cols = j;
		cells = newcells;
	}

	public void AllChange(boolean state) {
		addHistory();
		boolean[][] newcells = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = state;
			}
		}
		cells = newcells;
	}

	public void Reverse() {
		addHistory();
		boolean[][] newcells = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = !cells[i][j];
			}
		}
		cells = newcells;
	}

	public void Checker(boolean state) {
		addHistory();
		boolean[][] newcells = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				newcells[i][j] = ((i + j) % 2 == 0) == state;
			}
		}
		cells = newcells;
	}

	public void Copy() {
		copyCells = cells;
	}

	public boolean Paste() {
		if (copyCells == null) {
			return false;
		}
		addHistory();
		cells = copyCells;
		rows = copyCells.length;
		cols = copyCells[0].length;
		return true;
	}
}
