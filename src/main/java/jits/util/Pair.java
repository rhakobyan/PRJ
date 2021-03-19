package jits.util;

public class Pair<L, R> {
    private L firstElement;
    private R secondElement;

    public Pair(L firstElement, R secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    public L getFirstElement() {
        return firstElement;
    }

    public R getSecondElement() {
        return secondElement;
    }
}
