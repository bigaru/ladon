package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive Integer positiveInt = 4;
    @Positive float positiveFloat = 4;


    int anyNumber = 4;

    void bar(){
        positiveInt = -12;
        this.positiveInt = 133;
    }
}
