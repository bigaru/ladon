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

public class Main implements IBar, IFoo{
    final static int NEGV = 33;

    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();

        // TODO MethodInvocation
        //foo.anyNumber = Meow();

        int local = -4;
        final int co = -32;
        foo.positiveInt = local;
    }

    static int Meow() { return -6;}
}

interface IFoo {
    int BAR_VALUE = 2;
}

interface IBar {

}
