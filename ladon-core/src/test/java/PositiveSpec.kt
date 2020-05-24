import `in`.abaddon.ladon.LadonProcessor
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class PositiveSpec {

    @Test
    fun intPositiveLiteral(){
        val fooFile = JavaFileObjects.forSourceString("Foo", """
import in.abaddon.ladon.Positive;

public class  Foo {
    @Positive int positiveInt = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positiveInt = 43;
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
    @Positive int positiveInt = 4;
}
""")

        val mainFile = JavaFileObjects.forSourceString("Main", """
public class Main {
    public static void main(String[] args){
        Foo foo = new Foo();
        foo.positiveInt = -43;
    }
}
""")

        val compilation = Compiler.javac()
                                  .withProcessors(LadonProcessor())
                                  .compile(fooFile, mainFile)

        CompilationSubject.assertThat(compilation).hadErrorContaining("must be positive")
    }


}
