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
        fooify("", -23);
    }

    static void fooify(String str, @Positive int positiveNo){ }
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
