import `in`.abaddon.ladon.LadonProcessor
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.junit.Ignore
import org.junit.Test

class PositiveSpec {

    @Test
    fun byteNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive byte positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun shortNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive short positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun longNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive long positive = 4L;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42L;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun intPositiveLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
    int anyNumber = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = 42;
        foo.anyNumber = -42;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun intNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun integerNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("com.example.Foo", """
package com.example;

import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive Integer positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("com.example.Main", """
package com.example;

public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun floatNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive float positive = 4f;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42f;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun doubleNegativeLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive double positive = 4.0;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -42d;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun nestedUnary(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive double positive = 4.0;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = -+-+-42d;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun localVariableInitializedOnce(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        int bar = -42;
    
        Foo foo = new Foo();
        foo.positive = bar;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun localVariableNewlyAssigned(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        int bar = 42;
        bar = -42;
        bar = 42;
        bar = -42;
    
        Foo foo = new Foo();
        foo.positive = bar;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun insideClassWithThis(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive double positive = 4.0;
    
    public void bar(){
        this.positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun insideClassWithoutThis(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive double positive = 4.0;
    
    public void bar(){
        positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun typecastedValue(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive short positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = (short) -42;
    }
}
""")

        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Ignore
    @Test
    fun methodReturnsValue(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = getNegative();
    }
    
    static int getNegative(){
        return -42;
    }
}
""")

        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun staticVariable(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positive = 4;
    
    public void bar(){
        positive = Main.NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    static int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            // TODO switch order of files
            .compile(mainFile, fooFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

}
