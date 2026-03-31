import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Opcode Token Tests")
public class OpcodeTokenTest {

    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = new ExecutionContext();
    }

    // OP_0-OP_16 Tests

    @Test
    @DisplayName("OP_0 should push 0 to stack")
    public void testOP0() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_0);
        token.execute(ctx);
        assertFalse(ctx.isEmpty());
        byte[] value = ctx.pop();
        assertEquals(0, value[0]);
    }

    @Test
    @DisplayName("OP_1 should push 1 to stack")
    public void testOP1() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_1);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(1, value[0]);
    }

    @Test
    @DisplayName("OP_2 should push 2 to stack")
    public void testOP2() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_2);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(2, value[0]);
    }

    @Test
    @DisplayName("OP_5 should push 5 to stack")
    public void testOP5() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_5);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(5, value[0]);
    }

    @Test
    @DisplayName("OP_16 should push 16 to stack")
    public void testOP16() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_16);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(16, value[0]);
    }

    // OP_DUP Tests 

    @Test
    @DisplayName("OP_DUP should duplicate top stack value")
    public void testOPDup() {
        ctx.push(new byte[]{5});
        OpcodeToken token = new OpcodeToken(Opcode.OP_DUP);
        token.execute(ctx);
        
        byte[] top = ctx.pop();
        byte[] second = ctx.pop();
        assertEquals(5, top[0]);
        assertEquals(5, second[0]);
    }

    @Test
    @DisplayName("OP_DUP with empty stack should throw RuntimeException")
    public void testOPDupEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_DUP);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    @Test
    @DisplayName("OP_DUP should work with multi-byte values")
    public void testOPDupMultiByteValue() {
        byte[] value = new byte[]{1, 2, 3, 4};
        ctx.push(value);
        OpcodeToken token = new OpcodeToken(Opcode.OP_DUP);
        token.execute(ctx);
        
        byte[] top = ctx.pop();
        byte[] second = ctx.pop();
        assertArrayEquals(value, top);
        assertArrayEquals(value, second);
    }

    // OP_DROP Tests

    @Test
    @DisplayName("OP_DROP should remove top stack value")
    public void testOPDrop() {
        ctx.push(new byte[]{5});
        ctx.push(new byte[]{10});
        OpcodeToken token = new OpcodeToken(Opcode.OP_DROP);
        token.execute(ctx);
        
        byte[] remaining = ctx.pop();
        assertEquals(5, remaining[0]);
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("OP_DROP with empty stack should throw RuntimeException")
    public void testOPDropEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_DROP);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    @Test
    @DisplayName("OP_DROP should not affect other stack values")
    public void testOPDropMultipleValues() {
        ctx.push(new byte[]{1});
        ctx.push(new byte[]{2});
        ctx.push(new byte[]{3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_DROP);
        token.execute(ctx);
        
        assertEquals(3, ctx.pop()[0]);
        assertEquals(2, ctx.pop()[0]);
        assertEquals(1, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
    }

    // OP_EQUAL Tests 

    @Test
    @DisplayName("OP_EQUAL should push true when values are equal")
    public void testOPEqualTrue() {
        ctx.push(new byte[]{5});
        ctx.push(new byte[]{5});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        token.execute(ctx);
        
        byte[] result = ctx.pop();
        assertTrue(ExecutionContext.isTrue(result));
    }

    @Test
    @DisplayName("OP_EQUAL should push false when values are not equal")
    public void testOPEqualFalse() {
        ctx.push(new byte[]{5});
        ctx.push(new byte[]{10});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        token.execute(ctx);
        
        byte[] result = ctx.pop();
        assertFalse(ExecutionContext.isTrue(result));
    }

    @Test
    @DisplayName("OP_EQUAL with empty stack should throw RuntimeException")
    public void testOPEqualEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    @Test
    @DisplayName("OP_EQUAL with only one value should throw RuntimeException")
    public void testOPEqualOneValueThrowsException() {
        ctx.push(new byte[]{5});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when only one value on stack");
    }

    @Test
    @DisplayName("OP_EQUAL should handle multi-byte comparisons")
    public void testOPEqualMultiByte() {
        byte[] value1 = new byte[]{1, 2, 3, 4};
        byte[] value2 = new byte[]{1, 2, 3, 4};
        ctx.push(value1);
        ctx.push(value2);
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        token.execute(ctx);
        
        byte[] result = ctx.pop();
        assertTrue(ExecutionContext.isTrue(result));
    }

    // OP_EQUALVERIFY Tests 

    @Test
    @DisplayName("OP_EQUALVERIFY should not throw when values are equal")
    public void testOPEqualVerifyTrue() {
        ctx.push(new byte[]{7});
        ctx.push(new byte[]{7});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        // Should not throw
        assertDoesNotThrow(() -> token.execute(ctx));
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("OP_EQUALVERIFY should throw when values are not equal")
    public void testOPEqualVerifyFalse() {
        ctx.push(new byte[]{7});
        ctx.push(new byte[]{8});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when values are not equal");
    }

    @Test
    @DisplayName("OP_EQUALVERIFY with empty stack should throw RuntimeException")
    public void testOPEqualVerifyEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    // OP_HASH160 Tests

    @Test
    @DisplayName("OP_HASH160 should hash data on stack")
    public void testOPHash160() {
        ctx.push(new byte[]{1, 2, 3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_HASH160);
        token.execute(ctx);
        
        assertFalse(ctx.isEmpty());
        byte[] result = ctx.pop();
        assertNotNull(result);
        // HASH160 output is 20 bytes long
        assertEquals(20, result.length);
    }

    @Test
    @DisplayName("OP_HASH160 with empty stack should throw RuntimeException")
    public void testOPHash160EmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_HASH160);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    @Test
    @DisplayName("OP_HASH160 should produce consistent results")
    public void testOPHash160Consistency() {
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        
        ctx.push(data);
        OpcodeToken token1 = new OpcodeToken(Opcode.OP_HASH160);
        token1.execute(ctx);
        byte[] result1 = ctx.pop();
        
        ctx.push(data);
        OpcodeToken token2 = new OpcodeToken(Opcode.OP_HASH160);
        token2.execute(ctx);
        byte[] result2 = ctx.pop();
        
        assertArrayEquals(result1, result2);
    }

    // OP_CHECKSIG Tests

    @Test
    @DisplayName("OP_CHECKSIG with empty stack should throw RuntimeException")
    public void testOPCheckSigEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_CHECKSIG);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when stack is empty");
    }

    @Test
    @DisplayName("OP_CHECKSIG with one value should throw RuntimeException")
    public void testOPCheckSigOneValueThrowsException() {
        ctx.push(new byte[]{1, 2, 3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_CHECKSIG);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Should throw RuntimeException when only one value on stack");
    }

    // Empty Stack Tests 

    @Test
    @DisplayName("popping from empty stack should throw RuntimeException")
    public void testEmptyStackPop() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertThrows(RuntimeException.class, emptyCtx::pop, 
            "Should throw RuntimeException on empty stack pop");
    }

    @Test
    @DisplayName("peek on empty stack should return null")
    public void testEmptyStackPeek() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertNull(emptyCtx.peek());
    }

    @Test
    @DisplayName("isEmpty should return true for new context")
    public void testEmptyStackIsEmpty() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertTrue(emptyCtx.isEmpty());
    }

    // Stack State Tests 

    @Test
    @DisplayName("multiple operations should maintain stack correctly")
    public void testMultipleOpcodes() {
        OpcodeToken op1 = new OpcodeToken(Opcode.OP_5);
        OpcodeToken op2 = new OpcodeToken(Opcode.OP_3);
        OpcodeToken opDup = new OpcodeToken(Opcode.OP_DUP);
        
        op1.execute(ctx);
        op2.execute(ctx);
        opDup.execute(ctx);
        
        assertEquals(3, ctx.pop()[0]);
        assertEquals(3, ctx.pop()[0]);
        assertEquals(5, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("push and pop cycle should work correctly")
    public void testPushPopCycle() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] popped = ctx.pop();
        
        assertArrayEquals(data, popped);
        assertTrue(ctx.isEmpty());
    }
}
