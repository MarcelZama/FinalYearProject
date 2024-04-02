package com.example.yearproject;

public class Node {
    private int id;
    private int floor;
    private int x;
    private int y;
    private int roomNr;
    private String name;

    public Node(int id,int floor, int x, int y, int roomNr, String name) {

        this.id = id;
        this.floor = floor;
        this.x = (int) Math.round(x* 2.75);
        //this.x = x;
        this.y = (int) Math.round(y* 2.75);
        //this.y = y;
        this.roomNr = roomNr;
        this.name = name;
    }

    public int getId() {return id;}
    public int getFloor() {
        return floor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getroomnr() {
        return roomNr;
    }

    public String getname() {
        return name;
    }
}
