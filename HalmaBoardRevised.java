import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Collections;

public class HalmaBoardRevised {

	public HashSet<Coordinate> getBaseCamp(int player) {
		if (player == B) {
			return campLocationsB;
		} else {
			return campLocationsW;
		}
	}

	public HashSet<Coordinate> getEnemyCamp(int player) {
		if (player == W) {
			return campLocationsB;
		} else {
			return campLocationsW;
		}
	}

	public int[][] grid = (new int[16][16]);
	static public int B = 0;
	static public int W = 1;
	static public int E = -1;
	static public int[][] neighborPositions = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 },
			{ 1, 0 }, { 1, 1 } };

	static public HashSet<Coordinate> campLocationsB = new HashSet<Coordinate>();
	static public HashSet<Coordinate> campLocationsW = new HashSet<Coordinate>();

	public HashSet<Coordinate> armyLocationB = new HashSet<Coordinate>();
	public HashSet<Coordinate> armyLocationW = new HashSet<Coordinate>();

	public HalmaBoardRevised() {

		campLocationsB.add(new Coordinate(0, 0));
		campLocationsB.add(new Coordinate(0, 1));
		campLocationsB.add(new Coordinate(1, 0));
		campLocationsB.add(new Coordinate(0, 2));
		campLocationsB.add(new Coordinate(2, 0));
		campLocationsB.add(new Coordinate(1, 1));
		campLocationsB.add(new Coordinate(0, 3));
		campLocationsB.add(new Coordinate(3, 0));
		campLocationsB.add(new Coordinate(2, 1));
		campLocationsB.add(new Coordinate(1, 2));
		campLocationsB.add(new Coordinate(0, 4));
		campLocationsB.add(new Coordinate(4, 0));
		campLocationsB.add(new Coordinate(1, 3));
		campLocationsB.add(new Coordinate(3, 1));
		campLocationsB.add(new Coordinate(2, 2));
		campLocationsB.add(new Coordinate(1, 4));
		campLocationsB.add(new Coordinate(4, 1));
		campLocationsB.add(new Coordinate(2, 3));
		campLocationsB.add(new Coordinate(3, 2));

		campLocationsW.add(new Coordinate(15, 15));
		campLocationsW.add(new Coordinate(15, 14));
		campLocationsW.add(new Coordinate(14, 15));
		campLocationsW.add(new Coordinate(15, 13));
		campLocationsW.add(new Coordinate(13, 15));
		campLocationsW.add(new Coordinate(14, 14));
		campLocationsW.add(new Coordinate(15, 12));
		campLocationsW.add(new Coordinate(12, 15));
		campLocationsW.add(new Coordinate(14, 13));
		campLocationsW.add(new Coordinate(13, 14));
		campLocationsW.add(new Coordinate(15, 11));
		campLocationsW.add(new Coordinate(11, 15));
		campLocationsW.add(new Coordinate(14, 12));
		campLocationsW.add(new Coordinate(12, 14));
		campLocationsW.add(new Coordinate(13, 13));
		campLocationsW.add(new Coordinate(14, 11));
		campLocationsW.add(new Coordinate(11, 14));
		campLocationsW.add(new Coordinate(13, 12));
		campLocationsW.add(new Coordinate(12, 13));
	}

	public int detectWin() {
		if (campLocationsB.containsAll(armyLocationW)) {
			return W;
		}
		if (campLocationsW.containsAll(armyLocationB)) {
			return B;
		}
		return -1;
	}

	public void printBoard() {
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (this.grid[i][j] == E) {
					System.out.print('.');
				} else if (this.grid[i][j] == B) {
					System.out.print('B');
				} else {
					System.out.print('W');
				}
			}
			System.out.println();
		}
	}

	private boolean isValidPosition(Coordinate coordinate) {
		return (0 <= coordinate.x) && (coordinate.x < 16) && (0 <= coordinate.y) && (coordinate.y < 16);
	}

	public ArrayList<Move> getStepsFrom(Coordinate coordinate, int player) {
		ArrayList<Move> steps = new ArrayList<Move>();
		for (int s = 0; s < 8; s++) {
			int newX = coordinate.x + neighborPositions[s][0];
			int newY = coordinate.y + neighborPositions[s][1];
			if (isValidPosition(new Coordinate(newX, newY)) && this.grid[newX][newY] == -1) {
				Coordinate src = new Coordinate(coordinate.x, coordinate.y);
				Coordinate dest = new Coordinate(newX, newY);
				Move move = new Move('S', null, src, dest);
				if (followsRules(move, player)) {
					steps.add(move);
				}
			}
		}
		return steps;
	}

	public ArrayList<Move> getJumpsFrom(Coordinate coordinate, Coordinate baseCoordinate, Move parent, int player,
			HashSet<Coordinate> visited) {

		if (baseCoordinate.x == -1) {
			baseCoordinate.x = coordinate.x;
		}
		if (baseCoordinate.y == -1) {
			baseCoordinate.y = coordinate.y;
		}

		ArrayList<Move> jumps = new ArrayList<Move>();

		visited.add(coordinate);

		for (int s = 0; s < 8; s++) {
			int neighX = coordinate.x + neighborPositions[s][0];
			int neighY = coordinate.y + neighborPositions[s][1];
			int newX = neighX + neighborPositions[s][0];
			int newY = neighY + neighborPositions[s][1];
			Coordinate newCoordinate = new Coordinate(newX, newY);
			if (isValidPosition(newCoordinate)) {
				if (grid[neighX][neighY] >= 0 && grid[newX][newY] == -1) {

					Move newMove = new Move('J', parent, baseCoordinate, newCoordinate);
					if (followsRules(newMove, player) && !(visited.contains(newCoordinate))) {
						jumps.add(newMove);
						jumps.addAll(getJumpsFrom(newCoordinate, baseCoordinate, newMove, player, visited));
					}
				}
			}
		}
		return jumps;
	}

	private boolean followsRules(Move move, int player) {

		HashSet<Coordinate> baseCamp = getBaseCamp(player);
		HashSet<Coordinate> enemyCamp = getEnemyCamp(player);

		// Don't go back inside base camp
		if (baseCamp.contains(move.toCell)) {
			if (!(baseCamp.contains(move.fromCell))) {
				// System.out.println("Don't go back inside base camp");
				return false;
			}
			if (player == B) {
				if (move.fromCell.x + move.fromCell.y >= move.toCell.x + move.toCell.y) {
					// System.out.println("for B - dont go close to base");
					return false;
				}
			} else {
				if (move.fromCell.x + move.fromCell.y <= move.toCell.x + move.toCell.y) {
					return false;
				}
			}

		}

		if (enemyCamp.contains(move.fromCell)) {

			if (!(enemyCamp.contains(move.toCell))) {
				// System.out.println("Don't exit enemy camp once inside");
				return false;
			}

			if (player == W) {
				if (move.fromCell.x + move.fromCell.y <= move.toCell.x + move.toCell.y) {
					// System.out.println("for W - dont go away from goal");
					return false;
				}
			} else {
				if (move.fromCell.x + move.fromCell.y >= move.toCell.x + move.toCell.y) {
					// System.out.println("for B - dont go away from goal");
					return false;
				}
			}
		}
		return true;
	}

	public int getBoardValueByRemoteness(int player) {
		// B is MAX player, W is MIN player
		int BRemoteness = 0, WRemoteness = 0;

		for (Coordinate coordinate : armyLocationB) {
			// if (!getBaseCamp(W).contains(coordinate))
			BRemoteness += Math.pow(coordinate.x - 15, 2) + Math.pow(coordinate.y - 15, 2);
		}

		for (Coordinate coordinate : armyLocationW) {
			// if (!getBaseCamp(B).contains(coordinate))
			WRemoteness += Math.pow(coordinate.x, 2) + Math.pow(coordinate.y, 2);
		}
		return WRemoteness - BRemoteness;
	}

	public void makeMove(Move move) {
		if (grid[move.fromCell.x][move.fromCell.y] == B) {
			armyLocationB.remove(move.fromCell);
			armyLocationB.add(move.toCell);
		} else {
			armyLocationW.remove(move.fromCell);
			armyLocationW.add(move.toCell);
		}
		grid[move.toCell.x][move.toCell.y] = grid[move.fromCell.x][move.fromCell.y];
		grid[move.fromCell.x][move.fromCell.y] = -1;
	}

	public void undoMove(Move move) {
		if (grid[move.toCell.x][move.toCell.y] == B) {
			armyLocationB.add(move.fromCell);
			armyLocationB.remove(move.toCell);
		} else {
			armyLocationW.add(move.fromCell);
			armyLocationW.remove(move.toCell);
		}
		grid[move.fromCell.x][move.fromCell.y] = grid[move.toCell.x][move.toCell.y];
		grid[move.toCell.x][move.toCell.y] = -1;
	}

	public ArrayList<Move> getValidMoves(int player) {
		ArrayList<Move> moves = new ArrayList<Move>();
		HashSet<Coordinate> armyLocations = new HashSet<Coordinate>();
		if (player == B) {
			armyLocations = armyLocationB;
		} else {
			armyLocations = armyLocationW;
		}
		for (Coordinate armyLocation : armyLocations) {
			ArrayList<Move> steps = getStepsFrom(armyLocation, player);
			ArrayList<Move> jumps = getJumpsFrom(armyLocation, new Coordinate(-1, -1), null, player,
					new HashSet<Coordinate>());
			moves.addAll(steps);
			moves.addAll(jumps);
		}

		ArrayList<Move> prioritizedMoves = prioritize(moves, player);

		Collections.sort(prioritizedMoves, new SortByDirection(player).thenComparing(new SortByCorner(player))
				.thenComparing(new SortByJumpCount()));

		int branchingLimit = 35;
		if (prioritizedMoves.size() > branchingLimit + 2) {
			prioritizedMoves.subList(branchingLimit, prioritizedMoves.size() - 1).clear();
		}
		return prioritizedMoves;
	}

	private ArrayList<Move> prioritize(ArrayList<Move> moves, int player) {

		HashSet<Coordinate> baseCamp = getBaseCamp(player);
		HashSet<Coordinate> enemyCamp = getEnemyCamp(player);

		ArrayList<Move> baseToBaseAwayMoves = new ArrayList<Move>();
		ArrayList<Move> baseToOutMoves = new ArrayList<Move>();
		ArrayList<Move> targetToTargetCloserMoves = new ArrayList<Move>();
		ArrayList<Move> outToTargetMoves = new ArrayList<Move>();
		ArrayList<Move> onBoardMoves = new ArrayList<Move>();

		for (Move move : moves) {
			if (baseCamp.contains(move.fromCell) && baseCamp.contains(move.toCell)) {
				baseToBaseAwayMoves.add(move);
			} else if (baseCamp.contains(move.fromCell) && !baseCamp.contains(move.toCell)) {
				baseToOutMoves.add(move);
			} else if (enemyCamp.contains(move.toCell) && !enemyCamp.contains(move.fromCell)) {
				outToTargetMoves.add(move);
			} else if (enemyCamp.contains(move.toCell) && enemyCamp.contains(move.fromCell)) {
				targetToTargetCloserMoves.add(move);
			} else {
				onBoardMoves.add(move);
			}
		}
		if (baseToOutMoves.size() > 0) {
			return baseToOutMoves;
		}
		if (baseToBaseAwayMoves.size() > 0) {
			return baseToBaseAwayMoves;
		}
		if (outToTargetMoves.size() > 0) {
			return outToTargetMoves;
		}
		if (targetToTargetCloserMoves.size() > 0) {
			return targetToTargetCloserMoves;
		}

		return onBoardMoves;
	}

	class SortByJumpCount implements Comparator<Move> {

		@Override
		public int compare(Move o1, Move o2) {
			int cnt1 = -1, cnt2 = -1;
			if (o1.type == 'J') {
				while (o1.parent != null) {
					o1 = o1.parent;
					cnt1 += 1;
				}
			}
			if (o2.type == 'J') {
				while (o2.parent != null) {
					o2 = o2.parent;
					cnt2 += 1;
				}
			}
			return cnt2 - cnt1;
		}
	}

	class SortByDirection implements Comparator<Move> {

		int player;

		public SortByDirection(int player) {
			this.player = player;
		}

		@Override
		public int compare(Move m1, Move m2) {

			int orient1;
			int orient2;

			if (player == B) {
				if ((m1.fromCell.x < m1.toCell.x) && (m1.fromCell.y < m1.toCell.y)) {
					orient1 = 1;
				} else if ((m1.fromCell.x == m1.toCell.x) || (m1.fromCell.y == m1.toCell.y)) {
					orient1 = 2;
				} else {
					orient1 = 3;
				}

				if ((m2.fromCell.x < m2.toCell.x) && (m2.fromCell.y < m2.toCell.y)) {
					orient2 = 1;
				} else if ((m2.fromCell.x == m2.toCell.x) || (m2.fromCell.y == m2.toCell.y)) {
					orient2 = 2;
				} else {
					orient2 = 3;
				}
				return orient1 - orient2;

			} else {
				if ((m1.fromCell.x > m1.toCell.x) && (m1.fromCell.y > m1.toCell.y)) {
					orient1 = 1;
				} else if ((m1.fromCell.x == m1.toCell.x) || (m1.fromCell.y == m1.toCell.y)) {
					orient1 = 2;
				} else {
					orient1 = 3;
				}

				if ((m2.fromCell.x > m2.toCell.x) && (m2.fromCell.y > m2.toCell.y)) {
					orient2 = 1;
				} else if ((m2.fromCell.x == m2.toCell.x) || (m2.fromCell.y == m2.toCell.y)) {
					orient2 = 2;
				} else {
					orient2 = 3;
				}
				return orient1 - orient2;
			}
		}
	}

	class SortByCorner implements Comparator<Move> {

		int target;

		public SortByCorner(int player) {
			target = 15 * (1 - player);
		}

		@Override
		public int compare(Move o1, Move o2) {
			int d1 = (int) (Math.pow(o1.toCell.x - target, 2) + Math.pow(o1.toCell.y - target, 2));
			int d2 = (int) (Math.pow(o2.toCell.x - target, 2) + Math.pow(o2.toCell.y - target, 2));
			return d1 - d2;
		}
	}

	public int getBoardValue(int player) {
		return this.getBoardValueByRemoteness(player);
	}
}
