import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de opcodes")
public class OpcodeTokenTest {

    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = new ExecutionContext();
    }

    @Test
    @DisplayName("OP_0 mete 0 en la pila")
    public void testOP0() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_0);
        token.execute(ctx);
        assertFalse(ctx.isEmpty());
        byte[] value = ctx.pop();
        assertEquals(0, value[0]);
    }

    @Test
    @DisplayName("OP_1 mete 1 en la pila")
    public void testOP1() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_1);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(1, value[0]);
    }

    @Test
    @DisplayName("OP_2 mete 2 en la pila")
    public void testOP2() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_2);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(2, value[0]);
    }

    @Test
    @DisplayName("OP_5 mete 5 en la pila")
    public void testOP5() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_5);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(5, value[0]);
    }

    @Test
    @DisplayName("OP_16 mete 16 en la pila")
    public void testOP16() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_16);
        token.execute(ctx);
        byte[] value = ctx.pop();
        assertEquals(16, value[0]);
    }

    @Test
    @DisplayName("OP_DUP duplica el valor de arriba")
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
    @DisplayName("OP_DUP en pila vacia lanza error")
    public void testOPDupEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_DUP);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_DUP funciona con varios bytes")
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

    @Test
    @DisplayName("OP_DROP quita el valor de arriba")
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
    @DisplayName("OP_DROP en pila vacia lanza error")
    public void testOPDropEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_DROP);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_DROP no cambia los otros valores")
    public void testOPDropMultipleValues() {
        ctx.push(new byte[]{1});
        ctx.push(new byte[]{2});
        ctx.push(new byte[]{3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_DROP);
        token.execute(ctx);
        
        assertEquals(2, ctx.pop()[0]);
        assertEquals(1, ctx.pop()[0]);
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("OP_EQUAL mete verdadero si los valores son iguales")
    public void testOPEqualTrue() {
        ctx.push(new byte[]{5});
        ctx.push(new byte[]{5});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        token.execute(ctx);
        
        byte[] result = ctx.pop();
        assertTrue(ExecutionContext.isTrue(result));
    }

    @Test
    @DisplayName("OP_EQUAL mete falso si los valores son distintos")
    public void testOPEqualFalse() {
        ctx.push(new byte[]{5});
        ctx.push(new byte[]{10});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        token.execute(ctx);
        
        byte[] result = ctx.pop();
        assertFalse(ExecutionContext.isTrue(result));
    }

    @Test
    @DisplayName("OP_EQUAL en pila vacia lanza error")
    public void testOPEqualEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_EQUAL con un solo valor lanza error")
    public void testOPEqualOneValueThrowsException() {
        ctx.push(new byte[]{5});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUAL);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si solo hay un valor en la pila");
    }

    @Test
    @DisplayName("OP_EQUAL funciona con varios bytes")
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

    @Test
    @DisplayName("OP_EQUALVERIFY no lanza error si son iguales")
    public void testOPEqualVerifyTrue() {
        ctx.push(new byte[]{7});
        ctx.push(new byte[]{7});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        assertDoesNotThrow(() -> token.execute(ctx));
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("OP_EQUALVERIFY lanza error si son distintos")
    public void testOPEqualVerifyFalse() {
        ctx.push(new byte[]{7});
        ctx.push(new byte[]{8});
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si los valores son distintos");
    }

    @Test
    @DisplayName("OP_EQUALVERIFY en pila vacia lanza error")
    public void testOPEqualVerifyEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_EQUALVERIFY);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_HASH160 aplica hash al dato de la pila")
    public void testOPHash160() {
        ctx.push(new byte[]{1, 2, 3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_HASH160);
        token.execute(ctx);
        
        assertFalse(ctx.isEmpty());
        byte[] result = ctx.pop();
        assertNotNull(result);
        assertEquals(20, result.length);
    }

    @Test
    @DisplayName("OP_HASH160 en pila vacia lanza error")
    public void testOPHash160EmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_HASH160);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_HASH160 da siempre el mismo resultado")
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

    @Test
    @DisplayName("OP_CHECKSIG en pila vacia lanza error")
    public void testOPCheckSigEmptyStackThrowsException() {
        OpcodeToken token = new OpcodeToken(Opcode.OP_CHECKSIG);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("OP_CHECKSIG con un solo valor lanza error")
    public void testOPCheckSigOneValueThrowsException() {
        ctx.push(new byte[]{1, 2, 3});
        OpcodeToken token = new OpcodeToken(Opcode.OP_CHECKSIG);
        assertThrows(RuntimeException.class, () -> token.execute(ctx), 
            "Debe lanzar error si solo hay un valor en la pila");
    }

    @Test
    @DisplayName("quitar en pila vacia lanza error")
    public void testEmptyStackPop() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertThrows(RuntimeException.class, emptyCtx::pop, 
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("ver una pila vacia regresa null")
    public void testEmptyStackPeek() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertNull(emptyCtx.peek());
    }

    @Test
    @DisplayName("una pila nueva esta vacia")
    public void testEmptyStackIsEmpty() {
        ExecutionContext emptyCtx = new ExecutionContext();
        assertTrue(emptyCtx.isEmpty());
    }

    @Test
    @DisplayName("varias operaciones mantienen bien la pila")
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
    @DisplayName("un ciclo de agregar y quitar funciona")
    public void testPushPopCycle() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] popped = ctx.pop();
        
        assertArrayEquals(data, popped);
        assertTrue(ctx.isEmpty());
    }
}
