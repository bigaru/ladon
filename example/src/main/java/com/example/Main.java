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
    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();
        foo.positiveInt = 43;
        foo.anyNumber = -1;

        // TODO MethodInvocation
        foo.anyNumber = Meow();

        // DONE Ident
        int a = -3;
        foo.positiveInt = -a;
    }

    static int Meow() { return -6;}
}
