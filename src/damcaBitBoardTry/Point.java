package damcaBitBoardTry;

public class Point {
	private int x;
	private int y;

	public Point() {
	}

	public Point(int y, int x) {
		this.x = x;
		this.y = y;
	}
	public Point(Point p){
		this.x = p.x;
		this.y = p.y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return (((Point) obj).getX() == this.x && ((Point) obj).getY() == this.y);
	}

	@Override
	public String toString() {
		return "Y: " + this.y + " X:" + this.x;
	}
	//0-10000
	// 12 - hash: 745
	// 15- 4002
	// Node<key,value> arr[20]
	//
	// arr[5].append(new Node<hash,value>)

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
	}
}
