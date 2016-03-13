package com.martin.product.tuple;

/**
 * 包含两个元素的元组
 */
public class Tuple2Unit<P1, P2> {

    private P1 p1;
    private P2 p2;

    public Tuple2Unit(P1 p1, P2 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public P1 getP1() {
        return p1;
    }

    public void setP1(P1 p1) {
        this.p1 = p1;
    }

    public P2 getP2() {
        return p2;
    }

    public void setP2(P2 p2) {
        this.p2 = p2;
    }

    @Override
    public String toString() {
        return "Tuple2Unit{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                '}';
    }

}
