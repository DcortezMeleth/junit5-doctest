import com.google.common.reflect.ClassPath
import com.hexagon.doctest.annotations.DocTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

val signs = listOf("==", "<", ">", "=<", ">=", "!=")
val signMap = mapOf<String, (Comparable<Any>, Comparable<Any>) -> Boolean>(
    "==" to ::eq,
    "<" to ::lt,
    ">" to ::gt,
    "<=" to ::lte,
    ">=" to ::gte,
    "!=" to ::not
)

fun <T> eq(a: T, b: T): Boolean where T : Comparable<T> = a == b;
fun <T> gt(a: T, b: T): Boolean where T : Comparable<T> = a > b;
fun <T> lt(a: T, b: T): Boolean where T : Comparable<T> = a < b;
fun <T> gte(a: T, b: T): Boolean where T : Comparable<T> = a >= b;
fun <T> lte(a: T, b: T): Boolean where T : Comparable<T> = a <= b;
fun <T> not(a: T, b: T): Boolean where T : Comparable<T> = a != b;

fun <T> Comparable<T>.eq(clazz: Class<T>, b: Any): Boolean {
    if(b is Pair<*,*>) {
        println("Method eq:")
        println("This class: ${this.javaClass}")
        println("Other class: ${b.first?.javaClass}")
        println("Clazz: ${clazz.javaClass}")
        println("This: $this")
        println("Other: ${b.first}")
        return this == b.first
    }
    return false
}

fun <T> Comparable<T>.gt(clazz: Class<T>, b: Any) = clazz.isInstance(b) && this > b as T
fun <T> Comparable<T>.lt(clazz: Class<T>, b: Any) = clazz.isInstance(b) && this < b as T
fun <T> Comparable<T>.gte(clazz: Class<T>, b: Any) = clazz.isInstance(b) && this >= b as T
fun <T> Comparable<T>.lte(clazz: Class<T>, b: Any) = clazz.isInstance(b) && this <= b as T
fun <T> Comparable<T>.neq(clazz: Class<T>, b: Any) = clazz.isInstance(b) && this != b as T

class Wrapper {

    @TestFactory
    fun doctestGenerator(): Stream<DynamicTest> {
        val classLoader = Thread.currentThread().contextClassLoader

        var sequence: MutableList<DynamicTest> = mutableListOf();

        for (info in ClassPath.from(classLoader).getTopLevelClassesRecursive("com.hexagon")) {
            val clazz = info.load()
            if (clazz.isAnnotationPresent(DocTest::class.java)) {
                println("found class: $info")
                //TODO: use in case of future class type annotations - rather unlikely
            }

            for (method in clazz.methods) {
                if (method.isAnnotationPresent(DocTest::class.java)) {
                    println("found method: $method")

                    val annotation = method.getAnnotation(DocTest::class.java)
                    for (example in annotation.examples) {
                        println("\t example: $example")

                        val trimmedExample = example.replace("\\s".toRegex(), "")

                        println("\t\ttrimmed: $trimmedExample")

                        val sign = signs.firstOrNull() {
                            trimmedExample.contains(it)
                        }

                        if (sign == null) {
                            println("\t\tSkipping example without comparison sign! --- $example")
                            continue
                        }

                        val parts = trimmedExample.split(sign)

                        println("\t\tinvocation: ${parts[0]}")
                        println("\t\tinvocation: $sign")
                        println("\t\tinvocation: ${parts[1]}")

                        val funName = parts[0].replace("(", "").replace(")", "")
                        val compareFunc = signMap[sign]
                        var expectedResult = parts[1].toInt()

                        if (compareFunc == null) {
                            println("\t\tSkipping method because of wrong comparison sign!\n")
                            continue
                        }

                        var result = method.invoke(null)

                        println("\t\tLeft side: $result of type ${result::class.java}")
                        println("\t\tRight side: $expectedResult of type ${expectedResult::class.java}")

                        sequence.add(DynamicTest.dynamicTest(example)
                        { Assertions.assertTrue(expectedResult.eq(expectedResult::class.java, result to Int)) })
//                            { Assertions.assertTrue(compareFunc(expectedResult, method.invoke(null))) });
                    }
                }
            }
        }

//        println(Int(2) == java.lang.Integer(2))

        return sequence.stream()
//        return Stream.of(
//            DynamicTest.dynamicTest(
//                "some name"
//            ) { Assertions.assertEquals(2, 2) }
//        )
    }
}
