public class Coordinate {
		public int x;
		public int y;

		public Coordinate() {
			x = -1;
			y = -1;
		}

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			Coordinate coordinate = (Coordinate) obj;
			if (this.x == coordinate.x && this.y == coordinate.y)
				return true;
			return false;
		}
		
		@Override
		public int hashCode() {
			return 7*x+5*y;
		}
		@Override
		public String toString() {
		
		return " "+x+","+y+" ";
		}
	}