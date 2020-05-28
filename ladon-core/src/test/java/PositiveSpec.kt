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

public class Foo {
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

public class Foo {
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

public class Foo {
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

public class Foo {
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

public class Foo {
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

public class Foo {
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

public class Foo {
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

public class Foo {
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
    fun integerDoNotAcceptNull(){
        val fooFile = JavaFileObjects.forSourceString("com.example.Foo", """
package com.example;

import in.abaddon.ladon.Positive;

public class Foo {
    @Positive Integer positive = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("com.example.Main", """
package com.example;

public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positive = null;
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

public class Foo {
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
    fun localScopeVarInitializedOnce(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
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
    fun localScopeVarMultipleAssigned(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
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

public class Foo {
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

public class Foo {
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
    fun classVarShadowedByLocalScopeVar(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
    @Positive int positive = 4;
    
    public void bar(){
        int positive = 0;
        positive = -42;
    }
}
""")

        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun typecastedValue(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
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
    fun staticMethodReturnsValue(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
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
    fun qualifiedConstantVariableFromClass(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
    @Positive int positive = 4;
    
    public void bar(){
        positive = Main.NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    final static int NEGATIVE_NO = -42;
}
""")
        // Different order of file compilation must not matter
        val compilation1 = Compiler.javac().withProcessors(LadonProcessor()).compile(fooFile, mainFile)
        val compilation2 = Compiler.javac().withProcessors(LadonProcessor()).compile(mainFile, fooFile)

        CompilationSubject.assertThat(compilation1).hadErrorContaining("must be positive")
        CompilationSubject.assertThat(compilation2).hadErrorContaining("must be positive")
    }

    @Test
    fun qualifiedConstantVariableFromInterface(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo {
    @Positive int positive = 4;
    
    public void bar(){
        positive = IMain.NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("IMain", """
public interface IMain {
    int NEGATIVE_NO = -42;
}
""")
        // Different order of file compilation must not matter
        val compilation1 = Compiler.javac().withProcessors(LadonProcessor()).compile(fooFile, mainFile)
        val compilation2 = Compiler.javac().withProcessors(LadonProcessor()).compile(mainFile, fooFile)

        CompilationSubject.assertThat(compilation1).hadErrorContaining("must be positive")
        CompilationSubject.assertThat(compilation2).hadErrorContaining("must be positive")
    }

    @Test
    fun localClassConst(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    final static int NEGATIVE_NO = -42;

    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun localClassConstShadowedByLocalScopeVar(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    final static int NEGATIVE_NO = -42;

    @Positive int positive = 4;
    
    public void bar(){
        int NEGATIVE_NO = 42;
        positive = NEGATIVE_NO;
    }
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun inheritedConstVarFromBase(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo extends Bar {
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public class Bar {
    final static int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun inheritedConstVarFromBaseBase(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo extends Bar {
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val barFile = JavaFileObjects.forSourceString("Bar", """
public class Bar extends AbstractBar {}
""")
        val abstractBarFile = JavaFileObjects.forSourceString("AbstractBar", """
public class AbstractBar {
    final static int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, barFile, abstractBarFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun inheritedConstVarShadowedByLocalScopeVar(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo extends Bar {
    @Positive int positive = 4;
    
    public void bar(){
        int NEGATIVE_NO = 8;
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public class Bar {
    final static int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun inheritedConstVarShadowedByLocalClassConst(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo extends Bar {
    final static int NEGATIVE_NO = 8;
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public class Bar {
    final static int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun implementedConstVarFromInterface(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo implements Bar {
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public interface Bar {
    int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun implementedConstVarFromInterfaceInterface(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo implements Bar {
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val barFile = JavaFileObjects.forSourceString("Bar", """
public interface Bar extends AbstractBar {}
""")
        val abstractBarFile = JavaFileObjects.forSourceString("AbstractBar", """
public interface AbstractBar {
    int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, barFile, abstractBarFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun implementedConstVarFromComplicatedNestedInterface(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo implements I, B {
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val i = JavaFileObjects.forSourceString("I", """public interface I {}""")
        val b = JavaFileObjects.forSourceString("B", """public interface B extends B1, B2, B3 {}""")
        val b1 = JavaFileObjects.forSourceString("B1", """public interface B1 {}""")
        val b2 = JavaFileObjects.forSourceString("B2", """public interface B2 {}""")
        val b3 = JavaFileObjects.forSourceString("B3", """public interface B3 extends C {}""")
        val c = JavaFileObjects.forSourceString("C", """
public interface C {
    int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, i, b, b1, b2, b3, c)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }

    @Test
    fun implementedConstVarShadowedByLocalScopeVar(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo implements Bar {
    @Positive int positive = 4;
    
    public void bar(){
        int NEGATIVE_NO = 8;
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public interface Bar {
    int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

    @Test
    fun implementedConstVarShadowedByLocalClassConst(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class Foo implements Bar {
    final static int NEGATIVE_NO = 8;
    @Positive int positive = 4;
    
    public void bar(){
        positive = NEGATIVE_NO;
    }
}
""")
        val mainFile = JavaFileObjects.forSourceString("Bar", """
public interface Bar {
    int NEGATIVE_NO = -42;
}
""")
        val compilation = Compiler.javac()
            .withProcessors(LadonProcessor())
            .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).succeeded()
    }

}
