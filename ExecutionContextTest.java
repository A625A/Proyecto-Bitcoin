import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Execution Context Tests")
public class ExecutionContextTest {

    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = new ExecutionContext();
    }

    // ========== Basic Push/Pop Tests ==========

    @Test
    @DisplayName("push should add value to stack")
    public void testPush() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        assertFalse(ctx.isEmpty());
        assertEquals(data, ctx.peek());
    }

    @Test
    @DisplayName("pop should remove and return top value")
    public void testPop() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("peek should return top value without removing it")
    public void testPeek() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] peeked = ctx.peek();
        assertArrayEquals(data, peeked);
        assertFalse(ctx.isEmpty());
    }

    // Empty Stack Tests

    @Test
    @DisplayName("isEmpty should return true for new context")
    public void testIsEmptyNew() {
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("isEmpty should return false after push")
    public void testIsEmptyAfterPush() {
        ctx.push(new byte[]{1});
        assertFalse(ctx.isEmpty());
    }

    @Test
    @DisplayName("isEmpty should return true after pop")
    public void testIsEmptyAfterPop() {
        ctx.push(new byte[]{1});
        ctx.pop();
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("pop on empty stack should throw RuntimeException")
    public void testPopEmptyStackThrowsException() {
        assertThrows(RuntimeException.class, () -> ctx.pop(),
            "Should throw RuntimeException on pop from empty stack");
    }

    @Test
    @DisplayName("peek on empty stack should return null")
    public void testPeekEmptyStackReturnsNull() {
        assertNull(ctx.peek());
    }

    // Stack Overflow Prevention Tests

    @Test
    @DisplayName("stack should handle large number of pushes")
    public void testLargePushSequence() {
        for (int i = 0; i < 1000; i++) {
            ctx.push(new byte[]{(byte) (i % 256)});
        }
        assertFalse(ctx.isEmpty());
        assertEquals(1000, getStackSize());
    }

    @Test
    @DisplayName("stack should maintain LIFO order")
    public void testLIFOOrder() {
        ctx.push(new byte[]{1});
        ctx.push(new byte[]{2});
        ctx.push(new byte[]{3});
        
        assertEquals(3, ctx.pop()[0]);
        assertEquals(2, ctx.pop()[0]);
        assertEquals(1, ctx.pop()[0]);
    }

    // isTrue Tests 

    @Test
    @DisplayName("isTrue should return false for all zero bytes")
    public void testIsTrueAllZeros() {
        byte[] value = new byte[]{0, 0, 0, 0};
        assertFalse(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true for single non-zero byte")
    public void testIsTrueSingleNonZero() {
        byte[] value = new byte[]{1};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true if any byte is non-zero")
    public void testIsTrueAnyNonZero() {
        byte[] value = new byte[]{0, 0, 1, 0};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true for negative byte (non-zero in two's complement)")
    public void testIsTrueNegativeByte() {
        byte[] value = new byte[]{-1}; // 0xFF
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true for single element byte array with 1")
    public void testIsTrueOne() {
        byte[] value = new byte[]{1};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return false for empty byte array")
    public void testIsTrueEmptyArray() {
        byte[] value = new byte[]{};
        assertFalse(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true for mixed zero and non-zero bytes")
    public void testIsTrueMixed() {
        byte[] value = new byte[]{0, 0, 0, 5, 0, 0};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should return true for high values")
    public void testIsTrueHighValues() {
        byte[] value = new byte[]{127};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue should handle multi-byte false value")
    public void testIsTrueMultiByteFalse() {
        byte[] value = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        assertFalse(ExecutionContext.isTrue(value));
    }

    // Multiple Values Tests 

    @Test
    @DisplayName("stack should maintain multiple values")
    public void testMultipleValues() {
        ctx.push(new byte[]{1});
        ctx.push(new byte[]{2});
        ctx.push(new byte[]{3});
        
        assertFalse(ctx.isEmpty());
        ctx.pop();
        ctx.pop();
        ctx.pop();
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("push and pop cycles should work correctly")
    public void testPushPopCycles() {
        byte[] data1 = new byte[]{10};
        byte[] data2 = new byte[]{20};
        
        ctx.push(data1);
        ctx.push(data2);
        
        assertArrayEquals(data2, ctx.pop());
        assertArrayEquals(data1, ctx.pop());
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("alternating push and pop should work")
    public void testAlternatingPushPop() {
        ctx.push(new byte[]{1});
        assertEquals(1, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
        
        ctx.push(new byte[]{2});
        assertEquals(2, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
        
        ctx.push(new byte[]{3});
        assertEquals(3, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
    }

    // Multi-byte Value Tests 

    @Test
    @DisplayName("push and pop multi-byte values")
    public void testMultiByteValue() {
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
    }

    @Test
    @DisplayName("large byte array should work")
    public void testLargeByteArray() {
        byte[] data = new byte[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
    }

    // Edge Cases 

    @Test
    @DisplayName("push single byte value")
    public void testSingleByteValue() {
        ctx.push(new byte[]{42});
        byte[] value = ctx.pop();
        assertEquals(42, value[0]);
    }

    @Test
    @DisplayName("push null-like byte array (all zeros)")
    public void testZeroByteArray() {
        byte[] data = new byte[]{0};
        ctx.push(data);
        assertFalse(ExecutionContext.isTrue(ctx.peek()));
    }

    // Error Handling Tests 

    @Test
    @DisplayName("multiple pops from empty stack should throw on first attempt")
    public void testMultiplePopsEmptyStack() {
        assertThrows(RuntimeException.class, () -> ctx.pop());
        // Second pop should also throw
        assertThrows(RuntimeException.class, () -> ctx.pop());
    }

    // Helper Methods 

    private int getStackSize() {
        int count = 0;
        int maxIterations = 10000;
        while (!ctx.isEmpty() && count < maxIterations) {
            ctx.pop();
            count++;
        }
        return count;
    }
}
