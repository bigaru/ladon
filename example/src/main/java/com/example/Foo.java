package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive Integer positiveInt = 4;
    @Positive Double positiveFloat = 4d;


    int anyNumber = 4;

    void bar(){
        // TODO handle shadowing
        // float positiveFloat = 3;
        positiveFloat = 14.0;

        this.positiveInt = 133;
    }
}
