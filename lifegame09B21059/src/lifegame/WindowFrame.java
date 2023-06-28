package lifegame;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class WindowFrame extends JFrame implements WindowListener {
	
	// タスクが実行されているか格納する変数の設定
	private boolean taskflag = false;
	// タイマーの生成
	private Timer timer;

	// ボードサイズ変更ボタンの作成
	private JButton buttonAddOnTop = new JButton("＋");
	private JButton buttonAddToTheRight = new JButton("＋");
	private JButton buttonAddBelow = new JButton("＋");
	private JButton buttonAddToTheLeft = new JButton("＋");
	private JButton buttonReduceTop = new JButton("ー");
	private JButton buttonReduceToTheRight = new JButton("ー");
	private JButton buttonReduceTheBottom = new JButton("ー");
	private JButton buttonReduceToTheLeft = new JButton("ー");
	final int MAXSIZE = 100;
	final Color trueButtonColor = new Color(40, 40, 40);
	final Color falseButtonColor = new Color(100, 100, 100);

	public WindowFrame() {
		final int DEFAULTSIZE_X = 12;
		final int DEFAULTSIZE_Y = 13;
		final Color buttonWordColor = new Color(240, 240, 240);
		BoardModel model = new BoardModel(DEFAULTSIZE_X, DEFAULTSIZE_Y);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// ウィンドウ内部を占有する「ベース」パネルを作成する
		JPanel base = new JPanel();
		this.setContentPane(base);
		this.setMinimumSize(new Dimension(450, 350)); // 最小サイズの指定
		this.addWindowListener(this);

		// ボタンの作成
		JButton buttonNext = new JButton("Next");
		JButton buttonUndo = new JButton("Undo");
		JButton buttonNewGame = new JButton("New Game");
		JButton buttonAuto = new JButton("Auto");

		// 一部のボタンの色を設定
		buttonAddOnTop.setBackground(trueButtonColor);
		buttonAddToTheRight.setBackground(trueButtonColor);
		buttonAddBelow.setBackground(trueButtonColor);
		buttonAddToTheLeft.setBackground(trueButtonColor);
		buttonReduceTop.setBackground(trueButtonColor);
		buttonReduceToTheRight.setBackground(trueButtonColor);
		buttonReduceTheBottom.setBackground(trueButtonColor);
		buttonReduceToTheLeft.setBackground(trueButtonColor);
		// ボタンの文字色を設定
		buttonAddOnTop.setForeground(buttonWordColor);
		buttonAddToTheRight.setForeground(buttonWordColor);
		buttonAddBelow.setForeground(buttonWordColor);
		buttonAddToTheLeft.setForeground(buttonWordColor);
		buttonReduceTop.setForeground(buttonWordColor);
		buttonReduceToTheRight.setForeground(buttonWordColor);
		buttonReduceTheBottom.setForeground(buttonWordColor);
		buttonReduceToTheLeft.setForeground(buttonWordColor);

		// ボタンの中央揃え
		buttonAddToTheRight.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonReduceToTheRight.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonAddToTheLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonReduceToTheLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

		// メニューバーの作成
		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem menuitemOpen = new JMenuItem("Open");
		JMenuItem menuitemSave = new JMenuItem("Save");
		JMenu editMenu = new JMenu("Edit");
		JMenuItem menuitemReverse = new JMenuItem("Reverse");
		JMenuItem menuitemAllAlive = new JMenuItem("All Alive");
		JMenuItem menuitemAllDead = new JMenuItem("All Dead");
		JMenuItem menuitemCheckerAliveTopLeft = new JMenuItem("Checker(左上をAlive)");
		JMenuItem menuitemCheckerDeadTopLeft = new JMenuItem("Checker(左上をDead)");
		JMenuItem menuitemCopy = new JMenuItem("Copy");
		JMenuItem menuitemPaste = new JMenuItem("Paste");

		// メニューバーの表示
		menubar.add(fileMenu);
		menubar.add(editMenu);
		fileMenu.add(menuitemOpen);
		fileMenu.add(menuitemSave);
		editMenu.add(menuitemReverse);
		editMenu.add(menuitemAllAlive);
		editMenu.add(menuitemAllDead);
		editMenu.add(menuitemCheckerAliveTopLeft);
		editMenu.add(menuitemCheckerDeadTopLeft);
		editMenu.add(menuitemCopy);
		editMenu.add(menuitemPaste);
		this.setJMenuBar(menubar);

		base.setLayout(new BorderLayout()); // base 上に配置する GUI 部品のルールを設定
		BoardView view = new BoardView(model, buttonUndo); // BoardViewクラスを生成
		base.add(view, BorderLayout.CENTER); // base の中央に view を配置する

		// ボードサイズ変更ボタンの配置
		JPanel buttonPanelTop = new JPanel();
		JPanel buttonPanelRight = new JPanel();
		JPanel buttonPanelLeft = new JPanel();
		buttonPanelRight.setLayout(new BoxLayout(buttonPanelRight, BoxLayout.Y_AXIS));
		buttonPanelLeft.setLayout(new BoxLayout(buttonPanelLeft, BoxLayout.Y_AXIS));

		base.add(buttonPanelTop, BorderLayout.NORTH); // base の上端に配置する
		base.add(buttonPanelRight, BorderLayout.EAST); // base の右端に配置する
		base.add(buttonPanelLeft, BorderLayout.WEST); // base の左端に配置する

		JPanel buttonPanelBottom = new JPanel();
		buttonUndo.setEnabled(false);
		// ボタンのアクションを記述
		buttonNext.addActionListener(e -> {
			model.next();
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		buttonUndo.addActionListener(e -> {
			model.undo();
			view.repaint();
			if (model.isUndoable() == false) {
				buttonUndo.setEnabled(false);
			}
			sizeButtonStateChange(model);
		});
		buttonNewGame.addActionListener(e -> {
			new WindowFrame();
		});
		buttonAuto.addActionListener(e -> {
			if (taskflag) {
				timer.cancel();
				taskflag = false;
			} else {
				// タイマーの設定
				timer = new Timer(false);
				// タイマーによって実行される命令の設定
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						model.next();
						view.repaint();
						if (buttonUndo.isEnabled() == false) {
							buttonUndo.setEnabled(true);
						}
					}
				};
				timer.schedule(task, 0, 500);
				taskflag = true;
			}
		});
		buttonAddOnTop.addActionListener(e -> {
			model.addcellsTop(1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getCols() == MAXSIZE) {
				buttonAddOnTop.setEnabled(false);
				buttonAddBelow.setEnabled(false);
				buttonAddOnTop.setBackground(falseButtonColor);
				buttonAddBelow.setBackground(falseButtonColor);
			}
			if (buttonReduceTop.isEnabled() == false) {
				buttonReduceTop.setEnabled(true);
				buttonReduceTheBottom.setEnabled(true);
				buttonReduceTop.setBackground(trueButtonColor);
				buttonReduceTheBottom.setBackground(trueButtonColor);
			}
		});
		buttonAddToTheRight.addActionListener(e -> {
			model.addcellsRight(1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getRows() == MAXSIZE) {
				buttonAddToTheRight.setEnabled(false);
				buttonAddToTheLeft.setEnabled(false);
				buttonAddToTheRight.setBackground(falseButtonColor);
				buttonAddToTheLeft.setBackground(falseButtonColor);
			}
			if (buttonReduceToTheRight.isEnabled() == false) {
				buttonReduceToTheRight.setEnabled(true);
				buttonReduceToTheLeft.setEnabled(true);
				buttonReduceToTheRight.setBackground(trueButtonColor);
				buttonReduceToTheLeft.setBackground(trueButtonColor);
			}
		});
		buttonAddBelow.addActionListener(e -> {
			model.addcellsBottom(1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getCols() == MAXSIZE) {
				buttonAddOnTop.setEnabled(false);
				buttonAddBelow.setEnabled(false);
				buttonAddOnTop.setBackground(falseButtonColor);
				buttonAddBelow.setBackground(falseButtonColor);
			}
			if (buttonReduceTop.isEnabled() == false) {
				buttonReduceTop.setEnabled(true);
				buttonReduceTheBottom.setEnabled(true);
				buttonReduceTop.setBackground(trueButtonColor);
				buttonReduceTheBottom.setBackground(trueButtonColor);
			}
		});
		buttonAddToTheLeft.addActionListener(e -> {
			model.addcellsLeft(1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getRows() == MAXSIZE) {
				buttonAddToTheRight.setEnabled(false);
				buttonAddToTheLeft.setEnabled(false);
				buttonAddToTheRight.setBackground(falseButtonColor);
				buttonAddToTheLeft.setBackground(falseButtonColor);
			}
			if (buttonReduceToTheRight.isEnabled() == false) {
				buttonReduceToTheRight.setEnabled(true);
				buttonReduceToTheLeft.setEnabled(true);
				buttonReduceToTheRight.setBackground(trueButtonColor);
				buttonReduceToTheLeft.setBackground(trueButtonColor);
			}
		});
		buttonReduceTop.addActionListener(e -> {
			model.addcellsTop(-1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getCols() == 1) {
				buttonReduceTop.setEnabled(false);
				buttonReduceTheBottom.setEnabled(false);
				buttonReduceTop.setBackground(falseButtonColor);
				buttonReduceTheBottom.setBackground(falseButtonColor);
			}
			if (buttonAddOnTop.isEnabled() == false) {
				buttonAddOnTop.setEnabled(true);
				buttonAddBelow.setEnabled(true);
				buttonAddOnTop.setBackground(trueButtonColor);
				buttonAddBelow.setBackground(trueButtonColor);
			}
		});
		buttonReduceToTheRight.addActionListener(e -> {
			model.addcellsRight(-1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getRows() == 1) {
				buttonReduceToTheRight.setEnabled(false);
				buttonReduceToTheLeft.setEnabled(false);
				buttonReduceToTheRight.setBackground(falseButtonColor);
				buttonReduceToTheLeft.setBackground(falseButtonColor);
			}
			if (buttonAddToTheRight.isEnabled() == false) {
				buttonAddToTheRight.setEnabled(true);
				buttonAddToTheLeft.setEnabled(true);
				buttonAddToTheRight.setBackground(trueButtonColor);
				buttonAddToTheLeft.setBackground(trueButtonColor);
			}
		});
		buttonReduceTheBottom.addActionListener(e -> {
			model.addcellsBottom(-1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getCols() == 1) {
				buttonReduceTop.setEnabled(false);
				buttonReduceTheBottom.setEnabled(false);
				buttonReduceTop.setBackground(falseButtonColor);
				buttonReduceTheBottom.setBackground(falseButtonColor);
			}
			if (buttonAddOnTop.isEnabled() == false) {
				buttonAddOnTop.setEnabled(true);
				buttonAddBelow.setEnabled(true);
				buttonAddOnTop.setBackground(trueButtonColor);
				buttonAddBelow.setBackground(trueButtonColor);
			}
		});
		buttonReduceToTheLeft.addActionListener(e -> {
			model.addcellsLeft(-1);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			if (model.getRows() == 1) {
				buttonReduceToTheRight.setEnabled(false);
				buttonReduceToTheLeft.setEnabled(false);
				buttonReduceToTheRight.setBackground(falseButtonColor);
				buttonReduceToTheLeft.setBackground(falseButtonColor);
			}
			if (buttonAddToTheRight.isEnabled() == false) {
				buttonAddToTheRight.setEnabled(true);
				buttonAddToTheLeft.setEnabled(true);
				buttonAddToTheRight.setBackground(trueButtonColor);
				buttonAddToTheLeft.setBackground(trueButtonColor);
			}
		});

		// メニューバーの動作設定
		// Open操作
		menuitemOpen.addActionListener(e -> {
			JFrame tmpFrame = new JFrame();
			FileDialog fdi = new FileDialog(tmpFrame, "Open", FileDialog.LOAD);
			fdi.setVisible(true);
			if (fdi.getFile() == null) {
				return;
			}
			File file;
			String filename = fdi.getFile();
			if (fdi.getDirectory() == null) {
				file = new File(filename);
			} else {
				file = new File(fdi.getDirectory() + filename);
			}
			try {
				FileReader Fr = new FileReader(file);
				BufferedReader BFr = new BufferedReader(Fr);
				String data;
				try {
					data = BFr.readLine();
					if (data == null) {
						JFrame errFrame = new JFrame();
						JOptionPane.showMessageDialog(errFrame, "ファイルのフォーマットが正しくありません。");
						BFr.close();
						Fr.close();
						return;
					}
					boolean[][] newcells = new boolean[MAXSIZE][MAXSIZE];
					int wid = -1;
					int j = 0;
					while (data != null) {
						if (wid == -1) {
							wid = data.length();
							if (wid > MAXSIZE) {
								JFrame errFrame = new JFrame();
								JOptionPane.showMessageDialog(errFrame, "サイズオーバーです。");
								BFr.close();
								Fr.close();
								return;
							}
						} else if (wid != data.length()) {
							JFrame errFrame = new JFrame();
							JOptionPane.showMessageDialog(errFrame, "ファイルのフォーマットが正しくありません。");
							BFr.close();
							Fr.close();
							return;
						}
						for (int i = 0; i < wid; i++) {
							if (data.charAt(i) != '#' && data.charAt(i) != '.') {
								JFrame errFrame = new JFrame();
								JOptionPane.showMessageDialog(errFrame, "ファイルのフォーマットが正しくありません。");
								BFr.close();
								Fr.close();
								return;
							}
							if (data.charAt(i) == '#') {
								newcells[i][j] = true;
							}
						}
						j++;
						data = BFr.readLine();
						if (data == null) {
							break;
						}
						if (j >= MAXSIZE) {
							JFrame errFrame = new JFrame();
							JOptionPane.showMessageDialog(errFrame, "サイズオーバーです。");
							BFr.close();
							Fr.close();
							return;
						}
					}
					j--;
					model.writeCells(wid, j + 1, newcells);
					BFr.close();
					Fr.close();
					view.repaint();
					if (buttonUndo.isEnabled() == false) {
						buttonUndo.setEnabled(true);
					}
					sizeButtonStateChange(model);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JFrame errFrame = new JFrame();
					JOptionPane.showMessageDialog(errFrame, "ファイルの読み込みに失敗しました。");
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				JFrame errFrame = new JFrame();
				JOptionPane.showMessageDialog(errFrame, "ファイルが存在しません。");
			}
		});
		// Save操作
		menuitemSave.addActionListener(e -> {
			JFrame dialogFrame = new JFrame();
			FileDialog fdi = new FileDialog(dialogFrame, "Save", FileDialog.SAVE);
			fdi.setVisible(true);
			if (fdi.getFile() == null) {
				return;
			}
			String Filepath;
			String filename = fdi.getFile();
			if (fdi.getDirectory() == null) {
				Filepath = filename;
			} else {
				Filepath = fdi.getDirectory() + filename;
			}
			try {
				// FileWriterクラスのオブジェクトを生成する
				FileWriter filewrite = new FileWriter(Filepath, false);

				// ファイルに記述する
				int i = model.getCols();
				int j = model.getRows();
				String cellstate = null;
				for (int a = 0; a < i; a++) {
					for (int b = 0; b < j; b++) {
						if (model.isAlive(b, a)) {
							cellstate = "#";
						} else {
							cellstate = ".";
						}

						filewrite.write(cellstate);

					}
					if (a < i - 1) {
						filewrite.write("\n");
					}
				}

				// ファイルを閉じる
				filewrite.flush();
				filewrite.close();
			} catch (IOException e1) {
				JFrame errFrame = new JFrame();
				JOptionPane.showMessageDialog(errFrame, "ファイルの保存に失敗しました。");
				return;
			}
		});
		menuitemReverse.addActionListener(e -> {
			model.Reverse();
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		menuitemAllDead.addActionListener(e -> {
			model.AllChange(false);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		menuitemAllAlive.addActionListener(e -> {
			model.AllChange(true);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		menuitemCheckerAliveTopLeft.addActionListener(e -> {
			model.Checker(true);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		menuitemCheckerDeadTopLeft.addActionListener(e -> {
			model.Checker(false);
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
		});
		menuitemCopy.addActionListener(e -> {
			model.Copy();
		});
		menuitemPaste.addActionListener(e -> {
			if (model.Paste() == false) {
				JFrame errFrame = new JFrame();
				JOptionPane.showMessageDialog(errFrame, "コピーされている盤面はありません。");
				return;
			}
			view.repaint();
			if (buttonUndo.isEnabled() == false) {
				buttonUndo.setEnabled(true);
			}
			sizeButtonStateChange(model);
		});

		// ボタンの配置
		buttonPanelTop.add(buttonAddOnTop);
		buttonPanelRight.add(buttonAddToTheRight);
		buttonPanelLeft.add(buttonAddToTheLeft);
		buttonPanelTop.add(buttonReduceTop);
		buttonPanelRight.add(buttonReduceToTheRight);
		buttonPanelLeft.add(buttonReduceToTheLeft);
		buttonPanelBottom.add(buttonAddBelow);
		buttonPanelBottom.add(buttonReduceTheBottom);
		buttonPanelBottom.add(buttonAuto);
		buttonPanelBottom.add(buttonNext);
		buttonPanelBottom.add(buttonUndo);
		buttonPanelBottom.add(buttonNewGame);
		base.add(buttonPanelBottom, BorderLayout.SOUTH); // buttonPanelBottom4 の下端に配置する
		buttonPanelBottom.setLayout(new FlowLayout()); // java.awt.FlowLayout を設定
		
		this.pack(); // ウィンドウに乗せたものの配置を確定する
		this.setVisible(true); // ウィンドウを表示する
		this.setTitle("Lifegame");
	}
	public void windowOpened(WindowEvent e) { // 開かれた
	}

	public void windowClosing(WindowEvent e) { // 閉じられている
	}

	public void windowClosed(WindowEvent e) { // 閉じた
		if (taskflag) {
			timer.cancel();
			taskflag = false;
		}
	}

	public void windowIconified(WindowEvent e) { // アイコン化された
	}

	public void windowDeiconified(WindowEvent e) { // 非アイコン化された
	}

	public void windowActivated(WindowEvent e) { // アクティブになった
	}

	public void windowDeactivated(WindowEvent e) { // 非アクティブになった
	}
	private void sizeButtonStateChange(BoardModel model) {
		if (model.getRows() == 1) {
			buttonReduceToTheRight.setEnabled(false);
			buttonReduceToTheLeft.setEnabled(false);
			buttonReduceToTheRight.setBackground(falseButtonColor);
			buttonReduceToTheLeft.setBackground(falseButtonColor);
		} else {
			buttonReduceToTheRight.setEnabled(true);
			buttonReduceToTheLeft.setEnabled(true);
			buttonReduceToTheRight.setBackground(trueButtonColor);
			buttonReduceToTheLeft.setBackground(trueButtonColor);
		}
		if (model.getCols() == 1) {
			buttonReduceTop.setEnabled(false);
			buttonReduceTheBottom.setEnabled(false);
			buttonReduceTop.setBackground(falseButtonColor);
			buttonReduceTheBottom.setBackground(falseButtonColor);
		} else {
			buttonReduceTop.setEnabled(true);
			buttonReduceTheBottom.setEnabled(true);
			buttonReduceTop.setBackground(trueButtonColor);
			buttonReduceTheBottom.setBackground(trueButtonColor);
		}
		if (model.getRows() == MAXSIZE) {
			buttonAddToTheRight.setEnabled(false);
			buttonAddToTheLeft.setEnabled(false);
			buttonAddToTheRight.setBackground(falseButtonColor);
			buttonAddToTheLeft.setBackground(falseButtonColor);
		} else {
			buttonAddToTheRight.setEnabled(true);
			buttonAddToTheLeft.setEnabled(true);
			buttonAddToTheRight.setBackground(trueButtonColor);
			buttonAddToTheLeft.setBackground(trueButtonColor);
		}
		if (model.getCols() == MAXSIZE) {
			buttonAddOnTop.setEnabled(false);
			buttonAddBelow.setEnabled(false);
			buttonAddOnTop.setBackground(falseButtonColor);
			buttonAddBelow.setBackground(falseButtonColor);
		} else {
			buttonAddOnTop.setEnabled(true);
			buttonAddBelow.setEnabled(true);
			buttonAddOnTop.setBackground(trueButtonColor);
			buttonAddBelow.setBackground(trueButtonColor);
		}
	}

}
