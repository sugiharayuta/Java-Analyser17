package org.posl.test;

public class NewStatements{
    
    public static void main(String[] args){
        Date today = new Date(2022, 4, 28);
        DateRecord todayRecord = new DateRecord(2022, 4, 28);

        System.out.println(today.toString());
        System.out.println(todayRecord.toString());

        System.out.println(today.day());
        System.out.println(todayRecord.day());
    }

}

class Date{

    private final int year;
    private final int month;
    private final int day;

    public Date(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String toString(){
        return year + "/" + month + "/" + day;
    }

    public int year(){
        return year;
    }

    public int month(){
        return month;
    }

    public int day(){
        return day;
    }

}

record DateRecord(int year, int month, int day){

    public String toString(){
        return year + "/" + month + "/" + day;
    }

}
