package com.example;

/*
 * foo.no           = 3
 * this.no          = 3
 * JCFieldAccess    = JCLiteral
 *
 * foo.no
 * Expression.Identifier
 *
 * foo.no           = +3
 * foo.no           = -3f
 * JCFieldAccess    = JCUnary
 */

public class Main {
    final static int NEGV = 33;

    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();

        // TODO MethodInvocation
        //foo.anyNumber = Meow();

        // DONE Ident
        int a = 0;
        // TODO what?
        //foo.positiveInt = null;
        //int NEGV = 3;
        foo.positiveInt = NEGV;
    }

    static int Meow() { return -6;}
}

interface IBar{
    int BAR_VALUE = -1;
}

class Bar{
    final static int BAR_VALUE = -1;
}
