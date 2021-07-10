package lazeb.swgoh.mods.simulator;

import java.util.Iterator;

public class RangeIterator implements Iterator<Integer> {
    private final int to;
    private int current;

    RangeIterator(int from, int to) {
        this.current = from;
        this.to = to;
    }

    @Override
    public boolean hasNext() {
        return current <= to;
    }

    @Override
    public Integer next() {
        int next = current;
        current++;
        return next;
    }
}
