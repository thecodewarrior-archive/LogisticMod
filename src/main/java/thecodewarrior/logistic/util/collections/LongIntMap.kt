package thecodewarrior.logistic.util.collections

/**
 * Adapted version of [IntIntMap] for float values
 */

class LongIntMap(size: Int,
                  /** Fill factor, must be between (0 and 1)  */
                  private val m_fillFactor: Float) {

    constructor() : this(16, 0.5f)

    /** Keys  */
    private var m_keys: LongArray
    /** Values  */
    private var m_values: IntArray

    /** Do we have 'free' key in the map?  */
    private var m_hasFreeKey: Boolean = false
    /** Value of 'free' key  */
    private var m_freeValue: Int = 0
    /** We will resize a map once it reaches this size  */
    private var m_threshold: Int = 0
    /** Current map size  */
    private var m_size: Int = 0
    /** Mask to calculate the original position  */
    private var m_mask: Int = 0

    init {
        if (m_fillFactor <= 0 || m_fillFactor >= 1)
            throw IllegalArgumentException("FillFactor must be in (0, 1)")
        if (size <= 0)
            throw IllegalArgumentException("Size must be positive!")
        val capacity = Tools.arraySize(size, m_fillFactor)
        m_mask = capacity - 1

        m_keys = LongArray(capacity)
        m_values = IntArray(capacity)
        m_threshold = (capacity * m_fillFactor).toInt()
    }

    operator fun get(key: Long): Int {
        if (key == FREE_KEY)
            return if (m_hasFreeKey) m_freeValue else NO_VALUE

        val idx = getReadIndex(key)
        return if (idx != -1) m_values[idx] else NO_VALUE
    }

    fun put(key: Long, value: Int): Int {
        if (key == FREE_KEY) {
            val ret = m_freeValue
            if (!m_hasFreeKey)
                ++m_size
            m_hasFreeKey = true
            m_freeValue = value
            return ret
        }

        var idx = getPutIndex(key)
        if (idx < 0) { //no insertion point? Should not happen...
            rehash(m_keys.size * 2)
            idx = getPutIndex(key)
        }
        val prev = m_values[idx]
        if (m_keys[idx] != key) {
            m_keys[idx] = key
            m_values[idx] = value
            ++m_size
            if (m_size >= m_threshold)
                rehash(m_keys.size * 2)
        } else
        //it means used cell with our key
        {
            assert(m_keys[idx] == key)
            m_values[idx] = value
        }
        return prev
    }

    fun remove(key: Long): Int {
        if (key == FREE_KEY) {
            if (!m_hasFreeKey)
                return NO_VALUE
            m_hasFreeKey = false
            val ret = m_freeValue
            m_freeValue = NO_VALUE
            --m_size
            return ret
        }

        val idx = getReadIndex(key)
        if (idx == -1)
            return NO_VALUE

        val res = m_values[idx]
        m_values[idx] = NO_VALUE
        shiftKeys(idx)
        --m_size
        return res
    }

    fun size(): Int {
        return m_size
    }

    private fun rehash(newCapacity: Int) {
        m_threshold = (newCapacity * m_fillFactor).toInt()
        m_mask = newCapacity - 1

        val oldCapacity = m_keys.size
        val oldKeys = m_keys
        val oldValues = m_values

        m_keys = LongArray(newCapacity)
        m_values = IntArray(newCapacity)
        m_size = if (m_hasFreeKey) 1 else 0

        var i = oldCapacity
        while (i-- > 0) {
            if (oldKeys[i] != FREE_KEY)
                put(oldKeys[i], oldValues[i])
        }
    }

    private fun shiftKeys(pos: Int): Int {
        var pos = pos
        // Shift entries with the same hash.
        var last: Int
        var slot: Int
        var k: Long
        val keys = this.m_keys
        while (true) {
            last = pos
            pos = getNextIndex(pos)
            while (true) {
                k = keys[pos]
                if (k == FREE_KEY) {
                    keys[last] = FREE_KEY
                    m_values[last] = NO_VALUE
                    return last
                }
                slot = getStartIndex(k) //calculate the starting slot for the current key
                if (if (last <= pos) last >= slot || slot > pos else last >= slot && slot > pos) break
                pos = getNextIndex(pos)
            }
            keys[last] = k
            m_values[last] = m_values[pos]
        }
    }

    /**
     * Find key position in the map.
     * @param key Key to look for
     * *
     * @return Key position or -1 if not found
     */
    private fun getReadIndex(key: Long): Int {
        var idx = getStartIndex(key)
        if (m_keys[idx] == key)
        //we check FREE prior to this call
            return idx
        if (m_keys[idx] == FREE_KEY)
        //end of chain already
            return -1
        val startIdx = idx
        while(true) {
            idx = getNextIndex(idx)
            if(idx == startIdx)
                break
            if (m_keys[idx] == FREE_KEY)
                return -1
            if (m_keys[idx] == key)
                return idx
        }
        return -1
    }

    /**
     * Find an index of a cell which should be updated by 'put' operation.
     * It can be:
     * 1) a cell with a given key
     * 2) first free cell in the chain
     * @param key Key to look for
     * *
     * @return Index of a cell to be updated by a 'put' operation
     */
    private fun getPutIndex(key: Long): Int {
        val readIdx = getReadIndex(key)
        if (readIdx >= 0)
            return readIdx
        //key not found, find insertion point
        val startIdx = getStartIndex(key)
        if (m_keys[startIdx] == FREE_KEY)
            return startIdx
        var idx = startIdx
        while (m_keys[idx] != FREE_KEY) {
            idx = getNextIndex(idx)
            if (idx == startIdx)
                return -1
        }
        return idx
    }


    private fun getStartIndex(key: Long): Int {
        return Tools.phiMix(key).toInt() and m_mask
    }

    private fun getNextIndex(currentIndex: Int): Int {
        return currentIndex + 1 and m_mask
    }

    companion object {
        private val FREE_KEY = 0L

        val NO_VALUE = 0
    }
}
