import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.util.stream.Stream

class JavaParserWrapper {

    @TestFactory
    fun doctestGenerator(): Stream<DynamicTest> {
//        var sequence: MutableList<DynamicTest> = mutableListOf()

        val sourcesDir = File("src/main/kotlin")
        println(sourcesDir.absolutePath)

        DirExplorer(Filter(), FileHandler()).explore(sourcesDir)


        return Stream.of(
            DynamicTest.dynamicTest(
                "some name"
            ) { Assertions.assertEquals(2, 2) }
        )
    }
}

private class Filter() : DirExplorer.Filter {
    override fun interested(level: Int, path: String, file: File): Boolean {
        return path.endsWith(".kt")
    }
}

private class FileHandler() : DirExplorer.FileHandler {
    override fun handle(level: Int, path: String, file: File) {
        Adapter(path).visitA(JavaParser().parse(file).result.get(), null)
    }
}

private class Adapter(val path : String) : VoidVisitorAdapter<Any>() {

    fun visitA(n: CompilationUnit?, arg: Any?) {
        println(n?.comment?.javaClass?.canonicalName)
        super.visit(n, arg)
        if(n?.comment is JavadocComment) {
            val title = "%s %s".format(n.primaryTypeName, path)
            println(title)
            println("===============")
            println(n.comment)
        }
    }
}