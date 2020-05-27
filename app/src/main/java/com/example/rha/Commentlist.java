package com.example.rha;

public class Commentlist
{
    public String Name;
    public String Time;
    public String Date;
    public String Comment;
  public  Commentlist()
  {

  }
    public Commentlist(String name, String time, String date, String comment) {
        Name = name;
        Time = time;
        Date = date;
        Comment = comment;
    }

    public String getName1() {
        return Name;
    }

    public void setName1(String name) {
        Name = name;
    }

    public String getTime1() {
        return Time;
    }

    public void setTime1(String time) {
        Time = time;
    }

    public String getDate1() {
        return Date;
    }

    public void setDate1(String date) {
        Date = date;
    }

    public String getComment1() {
        return Comment;
    }

    public void setComment1(String comment) {
        Comment = comment;
    }
}
