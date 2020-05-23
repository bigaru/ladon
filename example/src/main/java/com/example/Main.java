package com.example;

public class Main {
    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();
        foo.proVar = 43;
        foo.unVar = -1;

        // TODO not handled
        //foo.proVar = Meow();
    }

    static int Meow() { return -6;}
}
