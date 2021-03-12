package com;

public class Pair { //Pair class, that is used as key for all the maps
    private int value;
    private char ch;

    Pair (int value, char ch) {
        this.value=value;
        this.ch=ch;
    }
    Pair () {
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public int getValue() {
        return value;
    }

    public char getCh() {
        return ch;
    }

    @Override
    public int hashCode() { //i had to rewrite those methods just because standard methods are so lame and did
        // not worked as I wanted
        return this.value*100+this.getCh()-'a';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if ((ch!=other.getCh())&&(value!=other.getValue()))
            return false;
        return true;
    }
}
