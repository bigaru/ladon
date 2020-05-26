package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive Integer positiveInt = 4;
    @Positive Double positiveFloat = 4d;


    int anyNumber = 4;

    void bar(){
        positiveFloat = 14.0;
        this.positiveInt = 133;
    }
}
