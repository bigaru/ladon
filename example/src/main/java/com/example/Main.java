package com.example;

import com.sun.istack.internal.NotNull;

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
    }

    static int Meow() { return -6;}
}
// TODO subtype's overridden method must have same Signature + ReturnType
class BaseBase{
    void noo(@NotNull Object A, Object str){}
    void foo(Integer A, Object str){}
}

class Base extends BaseBase{
    @Override
    public void noo(@NotNull Object A, Object str) { }
    public void foo(Integer A, Object str) { }
}
