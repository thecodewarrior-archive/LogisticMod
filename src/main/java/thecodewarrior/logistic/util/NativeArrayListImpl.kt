package thecodewarrior.logistic.util

import java.util.*
import kotlin.jvm.internal.NonVolatileRef

/**
 * An implementation of ArrayList methods designed to be used on separately stored arrays
 */
object NativeArrayListImpl {
    // region Objects
    fun <T> getO(array: NonVolatileRef.ObjectRef<Array<T?>>, length: NonVolatileRef.IntRef, index: Int): T {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index] as T // should never be wrong. Famous last words.
    }

    fun <T> setO(array: NonVolatileRef.ObjectRef<Array<T?>>, length: NonVolatileRef.IntRef, index: Int, value: T) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun <T> removeO(array: NonVolatileRef.ObjectRef<Array<T?>>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = null // clear to let GC do its work
    }

    fun <T> addO(array: NonVolatileRef.ObjectRef<Array<T?>>, length: NonVolatileRef.IntRef, value: T) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }
    // endregion

    // region Long
    fun getL(array: NonVolatileRef.ObjectRef<LongArray>, length: NonVolatileRef.IntRef, index: Int): Long {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setL(array: NonVolatileRef.ObjectRef<LongArray>, length: NonVolatileRef.IntRef, index: Int, value: Long) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeL(array: NonVolatileRef.ObjectRef<LongArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0 // clear to default value
    }

    fun addL(array: NonVolatileRef.ObjectRef<LongArray>, length: NonVolatileRef.IntRef, value: Long) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion

    // region Ints
    fun getI(array: NonVolatileRef.ObjectRef<IntArray>, length: NonVolatileRef.IntRef, index: Int): Int {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setI(array: NonVolatileRef.ObjectRef<IntArray>, length: NonVolatileRef.IntRef, index: Int, value: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeI(array: NonVolatileRef.ObjectRef<IntArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0 // clear to default value
    }

    fun addI(array: NonVolatileRef.ObjectRef<IntArray>, length: NonVolatileRef.IntRef, value: Int) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion

    // region Shorts
    fun getS(array: NonVolatileRef.ObjectRef<ShortArray>, length: NonVolatileRef.IntRef, index: Int): Short {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setS(array: NonVolatileRef.ObjectRef<ShortArray>, length: NonVolatileRef.IntRef, index: Int, value: Short) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeS(array: NonVolatileRef.ObjectRef<ShortArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0 // clear to default value
    }

    fun addS(array: NonVolatileRef.ObjectRef<ShortArray>, length: NonVolatileRef.IntRef, value: Short) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion

    // region Bytes
    fun getB(array: NonVolatileRef.ObjectRef<ByteArray>, length: NonVolatileRef.IntRef, index: Int): Byte {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setB(array: NonVolatileRef.ObjectRef<ByteArray>, length: NonVolatileRef.IntRef, index: Int, value: Byte) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeB(array: NonVolatileRef.ObjectRef<ByteArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0 // clear to default value
    }

    fun addB(array: NonVolatileRef.ObjectRef<ByteArray>, length: NonVolatileRef.IntRef, value: Byte) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion

    // region Floats
    fun getF(array: NonVolatileRef.ObjectRef<FloatArray>, length: NonVolatileRef.IntRef, index: Int): Float {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setF(array: NonVolatileRef.ObjectRef<FloatArray>, length: NonVolatileRef.IntRef, index: Int, value: Float) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeF(array: NonVolatileRef.ObjectRef<FloatArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0f // clear to default value
    }

    fun addF(array: NonVolatileRef.ObjectRef<FloatArray>, length: NonVolatileRef.IntRef, value: Float) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion

    // region Double
    fun getD(array: NonVolatileRef.ObjectRef<DoubleArray>, length: NonVolatileRef.IntRef, index: Int): Double {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        return array.element[index]
    }

    fun setD(array: NonVolatileRef.ObjectRef<DoubleArray>, length: NonVolatileRef.IntRef, index: Int, value: Double) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        array.element[index] = value
    }

    fun removeD(array: NonVolatileRef.ObjectRef<DoubleArray>, length: NonVolatileRef.IntRef, index: Int) {
        if(index < 0 || index >= length.element)
            throw IndexOutOfBoundsException("Index: $index, Size: ${length.element}")
        // yanked from ArrayList.fastRemove
        val numMoved = length.element - index - 1
        if (numMoved > 0)
            System.arraycopy(array.element, index+1, array.element, index, numMoved)
        array.element[--length.element] = 0.0 // clear to default value
    }

    fun addD(array: NonVolatileRef.ObjectRef<DoubleArray>, length: NonVolatileRef.IntRef, value: Double) {
        val minCapacity = length.element + 5 // avoid growing every single time

        // yanked from ArrayList.grow
        val oldCapacity = array.element.size

        if(minCapacity > oldCapacity) {

            var newCapacity = oldCapacity + (oldCapacity shr 1)
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity
            // minCapacity is usually close to size, so this is a win:
            array.element = Arrays.copyOf(array.element, newCapacity)
        }

        array.element[length.element++] = value
    }

    // endregion
}
