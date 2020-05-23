package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive Integer positiveInt = 4;
    @Positive Double positiveFloat = 4d;


    int anyNumber = 4;

    void bar(){
        positiveInt = -12;
        this.positiveInt = 133;
    }
}
