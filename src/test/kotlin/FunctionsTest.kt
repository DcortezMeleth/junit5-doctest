import com.hexagon.doctest.mul
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("Tests main class")
class FunctionsTest {

    @ParameterizedTest(name = "Testing if com.hexagon.doctest.mul({0}, {1}) == {2}")
    @CsvSource("1,2,2", "2,2,4", "0,0,0", "0,10,0")
    @DisplayName("Testing com.hexagon.doctest.mul function with correct params")
    fun mulTest(x: Int, y: Int, z: Int) {
        Assertions.assertEquals(z, mul(x, y))
    }

    @Test
    @DisplayName("Testing test")
    fun tt() {
        Assertions.assertTrue(true)
    }
}