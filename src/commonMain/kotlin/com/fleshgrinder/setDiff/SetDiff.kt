package com.fleshgrinder.setDiff

/**
 * The result of diffing two sets.
 *
 * @param L is the type of the elements on the left side of the diff.
 * @param R is the type of the elements on the right side of the diff
 * @param leftOnly contains all elements that were present on the left side.
 * @param common contains all elements that were present in both sides.
 * @param rightOnly contains all elements that were present on the right side.
 */
public data class SetDiff<L : Any, R : Any>(
    /**
     * Gets all elements that were present in the “left” side of the diff. This
     * is the relative complement between “left” (`A`) and “right” (`B`):
     *
     *     A \ B
     *
     * @see <a href="https://en.wikipedia.org/wiki/Complement_(set_theory)#Relative_complement">Wikipedia: Complement (set theory)</a>
     */
    public val leftOnly: Set<L>,

    /**
     * Gets all elements that were present in both sides of the diff. This is
     * the intersection between the “left” (`A`) and “right” (`B`) sets that
     * were diffed:
     *
     *     A ∩ B
     *
     * @see <a href="https://en.wikipedia.org/wiki/Intersection_(set_theory)">Wikipedia: Intersection (set theory)</a>
     */
    public val common: Set<Pair<L, R>>,

    /**
     * Gets all elements that were present in the “right” side of the diff. This
     * is the relative complement between “right” (`B`) and “left” (`A`):
     *
     *     B \ A
     *
     * @see <a href="https://en.wikipedia.org/wiki/Complement_(set_theory)#Relative_complement">Wikipedia: Complement (set theory)</a>
     */
    public val rightOnly: Set<R>,
) {
    /**
     * Returns `true` if this diff contains no elements.
     */
    public fun isEmpty(): Boolean =
        (leftOnly.size or common.size or rightOnly.size) == 0

    /**
     * Returns `true` if this diff contains elements.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun isNotEmpty(): Boolean =
        !isEmpty()

    public companion object
}


/**
 * Diffs the [left] with the [right] side.
 *
 * @param T supertype of both [L] and [R].
 * @param L is the type of the elements in the [left] set.
 * @param R is the type of the elements in the [right] set.
 * @param left side of the diff comparison.
 * @param right side of the diff comparison.
 * @param compareBy provides the value to compare entries from either
 *   side by. This can be anything but **MUST** produce a unique value
 *   for each entry. Behavior is undefined if this condition is not met.
 */
@Suppress("FunctionName")
public fun <T : Any, L : T, R : T> SetDiff(
    left: Set<L>,
    right: Set<R>,
    compareBy: (T) -> Any = Any::hashCode
): SetDiff<L, R> {
    val leftIndex = left.associateByTo(HashMap(), compareBy)
    val rightIndex = right.associateByTo(HashMap(), compareBy)

    val leftOnly = HashSet<L>()
    val common = HashSet<Pair<L, R>>()

    for ((key, leftValue) in leftIndex) {
        val rightValue = rightIndex.remove(key)
        if (rightValue == null) {
            leftOnly += leftValue
        } else {
            common += leftValue to rightValue
        }
    }

    return SetDiff(leftOnly.toSet(), common.toSet(), HashSet(rightIndex.values).toSet())
}
