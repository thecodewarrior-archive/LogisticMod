package thecodewarrior.logistic.util.collections

/**
 * Source: https://github.com/mikvor/hashmapTest/blob/master/src/main/java/map/intint/Tools.java
 */

/**
 * Common methods
 */
object Tools {

    /** Taken from FastUtil implementation  */

    /** Return the least power of two greater than or equal to the specified value.
     *
     * Note that this function will return 1 when the argument is 0.
     *
     * @param x a long integer smaller than or equal to 2<sup>62</sup>.
     *
     * @return the least power of two greater than or equal to the specified value.
     */
    fun nextPowerOfTwo(x: Long): Long {
        var x = x
        if (x == 0L) return 1
        x--
        x = x or (x shr 1)
        x = x or (x shr 2)
        x = x or (x shr 4)
        x = x or (x shr 8)
        x = x or (x shr 16)
        return (x or (x shr 32)) + 1
    }

    /** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to `Math.ceil( expected / f )`.
     * @param expected the expected number of elements in a hash table.
     *
     * @param f the load factor.
     *
     * @return the minimum possible size for a backing array.
     *
     * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
     */
    fun arraySize(expected: Int, f: Float): Int {
        val s = Math.max(2, nextPowerOfTwo(Math.ceil((expected / f).toDouble()).toLong()))
        if (s > 1 shl 30) throw IllegalArgumentException("Too large ($expected expected elements with load factor $f)")
        return s.toInt()
    }

    //taken from FastUtil
    private val INT_PHI = 0x9E3779B9.toInt()

    fun phiMix(x: Int): Int {
        val h = x * INT_PHI
        return h xor (h shr 16)
    }


    private val LONG_PHI: Long = -0x1E3779B97F4A7C15L

    fun phiMix(x: Long): Long {
        var h = x * LONG_PHI
        h = x xor (h ushr 32)
        return h xor (h ushr 16)
    }

}
