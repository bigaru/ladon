package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positiveNumber = 4;
    int anyNumber = 4;

    void bar(){
        positiveNumber = -12;
        this.positiveNumber = 133;
    }
}
