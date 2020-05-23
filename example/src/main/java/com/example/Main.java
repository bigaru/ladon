package com.example;

public class Main {
    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();
        foo.positiveNumber = 43;
        foo.anyNumber = -1;

        // TODO not handled
        foo.anyNumber = Meow();
    }

    static int Meow() { return -6;}
}
