public class Move{
	public char type;
	public Move parent;
	public Coordinate fromCell;
	public Coordinate toCell;

	public Move(char type, Move parent, Coordinate fromCell, Coordinate toCell) {
		this.type = type;
		this.parent = parent;
		this.fromCell = fromCell;
		this.toCell = toCell;
	}

	@Override
	public String toString() {
		return new String(this.type+"##"+this.parent+"####"+this.fromCell.x+"::"+this.fromCell.y+"::"+this.toCell.x+"::"+this.toCell.y);
	}
}