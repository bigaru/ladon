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

        // TODO Ident
        char a = 3;
        foo.anyNumber = -a;

        // TODO TypeCast
        foo.anyNumber = (short) 3;

        // DONE Unary
        foo.positiveFloat = -+-34.0;
        foo.positiveFloat = +-+-34.0d;

        // DONE Inside class
        // positiveFloat = -34f;
    }

    static int Meow() { return -6;}
}
