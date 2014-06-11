package za.co.entelect.challenge.ai.mcts;

public class UCTPos {
    public byte x;
    public byte y;

    public UCTPos(int x, int y) {
        this((byte)x, (byte)y);
    }

    public UCTPos(byte x, byte y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UCTPos)) return false;

        UCTPos uctPos = (UCTPos) o;

        if (x != uctPos.x) return false;
        if (y != uctPos.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = 31 * result + (int) y;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append('}');
        return sb.toString();
    }
}