package thecodewarrior.logistic.util

/**
 * Stores objects by an integer ID. IDs are pseudorandom, not sequential.
 */
class IDStore<V: Identifiable> {
    
}

interface Identifiable {
    /**
     * The ID of this object in the IDStore. This should be initialized to -1, and is set in the IDStore class
     */
    var id: Int
}
