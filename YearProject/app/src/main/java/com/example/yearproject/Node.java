package com.example.yearproject;

public class Node {
    private int id;
    private int floor;
    private int x;
    private int y;
    private String roomNr;
    private String name;

    public Node(int id,int floor, int x, int y, String roomNr, String name) {

        this.id = id;
        this.floor = floor;

        this.x = x;

        this.y = y;

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

    public String getroomnr() {
        return roomNr;
    }

    public String getname() {
        return name;
    }
}
