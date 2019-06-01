package com.example.mcadapp.Chats.Message;

public class MemberData {

    private String name;
    private String color;
    private String _time;


    public MemberData(String name, String color, String __time) {
        this.name  = name;
        this.color = color;
        this._time = __time;
    }



    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getTime(){
        return this._time;
    }

    public void setTime(String __time){
        this._time = __time;
    }
}
