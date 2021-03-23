package com.example;

import in.abaddon.ladon.Positive;

public class Main {
    final static int NEGV = 33;

    public static void main(String[] args){
        System.out.println("Pika Example");

        Foo foo = new Foo();

        // TODO MethodInvocation
        //foo.anyNumber = Meow();

        int local = -4;
        final int co = -32;
        foo.positiveInt = local;
        fooify("", new Integer(3));

        fooify(4.0, new Integer(3));
        fooify(new Double(4.0), 3);
        fooify(4.0, 3);
    }

    // TODO Method overload resolution
    //  1. qualified name
    //  2. type length
    //  3. type matches
    //  4. type with if a subtype of b
    static void fooify(String str, @Positive int positiveNo){ }
    static void fooify(String str, @Positive Object positiveNo){ }
    static void fooify(String str, @Positive Integer positiveNo){ }

    static void fooify(Double floati, @Positive int positiveNo){ }
    static void fooify(double floati, @Positive Integer positiveNo){ }

    static void fooify(@Positive int positiveNo){ }
    static void fooify( Integer positiveNo){ }

    static int Meow() { return -6;}
}
// TODO subtype's overridden method must have same Signature + ReturnType
class BaseBase{
    void noo(@Positive Object A, Object str){}
    void foo(Integer A, Object str){}
}

class Base extends BaseBase{
    @Override
    public void noo(@Positive Object A, Object str) { }
    public void foo(Integer A, Object str) { }
}
