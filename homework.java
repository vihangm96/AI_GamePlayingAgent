import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class homework {

	static String mode;
	static int player;
	static float time;
	static int B = 0;
	static int W = 1;
	static int E = -1;
	static int pInf = 1000000;
	static int nInf = -1000000;
	// TODO set
	static int limit = 3;

	public static void main(String[] args) {

		HalmaBoardRevised halmaBoardRevised = new HalmaBoardRevised();
		initialize(halmaBoardRevised);
		// halmaBoardRevised.printBoard();
		if (mode.equalsIgnoreCase("SINGLE")) {

			Move move = halmaBoardRevised.getValidMoves(player).get(0);
			// System.out.println(move);
			writeMoveToFile(move);
		} else {
			/*
			//////////////////////////////////////
			long start = System.currentTimeMillis();
			int k = 0;
			while (halmaBoardRevised.detectWin() == -1) {
			//while(true) {
				k += 1;
				if(k>=100) {
					System.out.println("FAIL");
					break;
				}
				int player1 = player;
				halmaBoardRevised.makeMove(alphaBetaSearch(halmaBoardRevised, player1));
				int player2 = 1 - player;
				halmaBoardRevised.makeMove(alphaBetaSearch(halmaBoardRevised, player2));
				System.out.println("----------------------");
				halmaBoardRevised.printBoard();
			}
			System.out.println(" GAME COMPLETED IN MOVES - " + k + " WINNER - " + halmaBoardRevised.detectWin());
			System.out.println("TIME - " + (System.currentTimeMillis() - start)); 
			if(halmaBoardRevised.detectWin()!=player) {
				System.out.println("FAIL!!!");
			}
			//////////////////////////////
			*/
			writeMoveToFile(alphaBetaSearch(halmaBoardRevised, player));
		}
	}

	private static void writeMoveToFile(Move move) {
		String fileContent = "";
		if (move.type == 'S') {
			fileContent += "E " + move.fromCell.y + "," + move.fromCell.x + " " + move.toCell.y + "," + move.toCell.x;
		} else {

			ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

			while (move.parent != null) {
				coordinates.add(move.toCell);
				move = move.parent;
			}
			coordinates.add(move.toCell);
			coordinates.add(move.fromCell);
			Collections.reverse(coordinates);
			for (int k = 0; k < coordinates.size() - 1; k++) {
				Coordinate src = coordinates.get(k);
				Coordinate dest = coordinates.get(k + 1);
				fileContent += "J ";
				fileContent += src.y + "," + src.x + " " + dest.y + "," + dest.x + "\n";
			}
		}
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("output.txt"));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Move alphaBetaSearch(HalmaBoardRevised halmaBoardRevised, int player) {
		Move bestMoveYet = null;
		int bestValYet;

		if (player == B) {
			bestValYet = nInf;
			ArrayList<Move> allMoves = halmaBoardRevised.getValidMoves(player);
			for (Move move : allMoves) {
				halmaBoardRevised.makeMove(move);
				int val = maxValueAB(halmaBoardRevised, nInf, pInf, 0);
				if (bestValYet < val) {
					bestMoveYet = move;
					bestValYet = val;
				}
				halmaBoardRevised.undoMove(move);
			}

		} else {
			bestValYet = pInf;
			ArrayList<Move> allMoves = halmaBoardRevised.getValidMoves(player);
			for (Move move : allMoves) {
				halmaBoardRevised.makeMove(move);
				int val = minValueAB(halmaBoardRevised, nInf, pInf, 0);
				if (bestValYet > val) {
					bestMoveYet = move;
					bestValYet = val;
				}
				halmaBoardRevised.undoMove(move);
			}
		}
		return bestMoveYet;
	}

	private static int maxValueAB(HalmaBoardRevised halmaBoardRevised, int alpha, int beta, int depth) {
		int temp_player = B;
		if (halmaBoardRevised.detectWin() != -1 || depth >= limit) {
			return halmaBoardRevised.getBoardValue(temp_player);
		}
		int val = nInf;
		for (Move move : halmaBoardRevised.getValidMoves(temp_player)) {
			halmaBoardRevised.makeMove(move);
			int testVal = minValueAB(halmaBoardRevised, alpha, beta, depth + 1);
			if (val < testVal) {
				val = testVal;
			}
			if (val >= beta) {
				halmaBoardRevised.undoMove(move);
				return val;
			}
			alpha = Math.max(alpha, val);
			halmaBoardRevised.undoMove(move);
		}
		return val;
	}

	private static int minValueAB(HalmaBoardRevised halmaBoardRevised, int alpha, int beta, int depth) {
		int temp_player = W;
		if (halmaBoardRevised.detectWin() != -1 || depth >= limit) {
			return halmaBoardRevised.getBoardValue(temp_player);
		}
		int val = pInf;
		for (Move move : halmaBoardRevised.getValidMoves(temp_player)) {
			halmaBoardRevised.makeMove(move);
			int testVal = maxValueAB(halmaBoardRevised, alpha, beta, depth + 1);
			if (val > testVal) {
				val = testVal;
			}
			if (val <= alpha) {
				// System.out.println("Pruned");
				halmaBoardRevised.undoMove(move);
				return val;
			}
			beta = Math.min(beta, val);

			halmaBoardRevised.undoMove(move);
		}

		return val;
	}

	private static void initialize(HalmaBoardRevised halmaBoardRevised) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("input.txt")));
			mode = br.readLine();
			if (br.readLine().equalsIgnoreCase("WHITE")) {
				player = W;
			} else {
				player = B;
			}
			time = Float.parseFloat(br.readLine());
			String st;
			for (int i = 0; i < 16; i++) {
				st = br.readLine();
				for (int j = 0; j < 16; j++) {
					if (st.charAt(j) == '.') {
						halmaBoardRevised.grid[i][j] = E;
					} else if (st.charAt(j) == 'B') {
						halmaBoardRevised.grid[i][j] = B;
						halmaBoardRevised.armyLocationB.add(new Coordinate(i, j));
					} else {
						halmaBoardRevised.grid[i][j] = W;
						halmaBoardRevised.armyLocationW.add(new Coordinate(i, j));
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


