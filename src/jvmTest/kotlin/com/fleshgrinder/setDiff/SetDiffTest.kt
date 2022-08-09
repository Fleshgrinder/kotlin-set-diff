package com.fleshgrinder.setDiff

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isSameInstanceAs
import strikt.assertions.isTrue
import strikt.assertions.single

@Execution(CONCURRENT)
private class SetDiffTest {
    @Test fun `diff is empty if left and right are empty`() {
        expectThat(SetDiff(emptySet(), emptySet())) {
            get { isEmpty() }.isTrue()
            get { isNotEmpty() }.isFalse()
            get { leftOnly }.isEmpty()
            get { common }.isEmpty()
            get { rightOnly }.isEmpty()
        }
    }

    @Test fun `diff has leftOnly if left has elements but right is empty`() {
        expectThat(SetDiff(setOf(1, 2, 3), emptySet())) {
            get { isEmpty() }.isFalse()
            get { isNotEmpty() }.isTrue()
            get { leftOnly }.isEqualTo(setOf(1, 2, 3))
            get { common }.isEmpty()
            get { rightOnly }.isEmpty()
        }
    }

    @Test fun `diff has rightOnly if left is empty but right has elements`() {
        expectThat(SetDiff(emptySet(), setOf(1, 2, 3))) {
            get { isEmpty() }.isFalse()
            get { isNotEmpty() }.isTrue()
            get { leftOnly }.isEmpty()
            get { common }.isEmpty()
            get { rightOnly }.isEqualTo(setOf(1, 2, 3))
        }
    }

    @Test fun `diff with default compareBy`() {
        expectThat(SetDiff(setOf(1, 2, 3), setOf(2, 3, 4))) {
            get { isEmpty() }.isFalse()
            get { isNotEmpty() }.isTrue()
            get { leftOnly }.isEqualTo(setOf(1))
            get { common }.isEqualTo(setOf(2 to 2, 3 to 3))
            get { rightOnly }.isEqualTo(setOf(4))
        }
    }

    @Test fun `diff with custom compareBy`() {
        val l1 = L(1)
        val l2 = L(2)
        val l3 = L(3)
        val r2 = R(2)
        val r3 = R(3)
        val r4 = R(4)
        val l = setOf(l1, l2, l3)
        val r = setOf(r2, r3, r4)

        val standardDiff = SetDiff(l, r)
        val customDiff = SetDiff(l, r) { it.n }

        expect {
            // The standard diff fails here because T, L, R have no hashCode
            // implementation and each instance is different.
            that(standardDiff) {
                get { isEmpty() }.isFalse()
                get { isNotEmpty() }.isTrue()
                get { leftOnly }.isEqualTo(l)
                get { common }.isEmpty()
                get { rightOnly }.isEqualTo(r)
            }

            that(customDiff) {
                get { isEmpty() }.isFalse()
                get { isNotEmpty() }.isTrue()
                get { leftOnly }.single().isSameInstanceAs(l1)
                get { common }.isEqualTo(setOf(l2 to r2, l3 to r3))
                get { rightOnly }.single().isSameInstanceAs(r4)
            }
        }
    }

    abstract class T(val n: Int)
    class L(n: Int) : T(n)
    class R(n: Int) : T(n)
}
