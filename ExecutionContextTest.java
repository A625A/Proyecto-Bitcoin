import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de contexto")
public class ExecutionContextTest {

    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = new ExecutionContext();
    }

    @Test
    @DisplayName("agregar un valor lo deja en la pila")
    public void testPush() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        assertFalse(ctx.isEmpty());
        assertEquals(data, ctx.peek());
    }

    @Test
    @DisplayName("quitar regresa el valor de arriba")
    public void testPop() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("ver regresa el valor de arriba sin quitarlo")
    public void testPeek() {
        byte[] data = new byte[]{42};
        ctx.push(data);
        byte[] peeked = ctx.peek();
        assertArrayEquals(data, peeked);
        assertFalse(ctx.isEmpty());
    }

    @Test
    @DisplayName("una pila nueva esta vacia")
    public void testIsEmptyNew() {
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("despues de agregar un valor la pila no esta vacia")
    public void testIsEmptyAfterPush() {
        ctx.push(new byte[]{1});
        assertFalse(ctx.isEmpty());
    }

    @Test
    @DisplayName("despues de quitar el valor la pila queda vacia")
    public void testIsEmptyAfterPop() {
        ctx.push(new byte[]{1});
        ctx.pop();
        assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("quitar en una pila vacia lanza error")
    public void testPopEmptyStackThrowsException() {
        assertThrows(RuntimeException.class, () -> ctx.pop(),
            "Debe lanzar error si la pila esta vacia");
    }

    @Test
    @DisplayName("ver una pila vacia regresa null")
    public void testPeekEmptyStackReturnsNull() {
        assertNull(ctx.peek());
    }

    @Test
    @DisplayName("la pila soporta muchos valores")
    public void testLargePushSequence() {
        for (int i = 0; i < 1000; i++) {
            ctx.push(new byte[]{(byte) (i % 256)});
        }
        assertFalse(ctx.isEmpty());
        assertEquals(1000, getStackSize());
    }

    @Test
    @DisplayName("la pila mantiene el orden LIFO")
    public void testLIFOOrder() {
        ctx.push(new byte[]{1});
        ctx.push(new byte[]{2});
        ctx.push(new byte[]{3});
        
        assertEquals(3, ctx.pop()[0]);
        assertEquals(2, ctx.pop()[0]);
        assertEquals(1, ctx.pop()[0]);
    }

    @Test
    @DisplayName("isTrue regresa falso si todos son cero")
    public void testIsTrueAllZeros() {
        byte[] value = new byte[]{0, 0, 0, 0};
        assertFalse(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero con un byte distinto de cero")
    public void testIsTrueSingleNonZero() {
        byte[] value = new byte[]{1};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero si algun byte no es cero")
    public void testIsTrueAnyNonZero() {
        byte[] value = new byte[]{0, 0, 1, 0};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero con un byte negativo")
    public void testIsTrueNegativeByte() {
        byte[] value = new byte[]{-1};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero con un arreglo que tiene 1")
    public void testIsTrueOne() {
        byte[] value = new byte[]{1};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa falso con un arreglo vacio")
    public void testIsTrueEmptyArray() {
        byte[] value = new byte[]{};
        assertFalse(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero con ceros y un valor distinto de cero")
    public void testIsTrueMixed() {
        byte[] value = new byte[]{0, 0, 0, 5, 0, 0};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa verdadero con valores altos")
    public void testIsTrueHighValues() {
        byte[] value = new byte[]{127};
        assertTrue(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("isTrue regresa falso con varios bytes en cero")
    public void testIsTrueMultiByteFalse() {
        byte[] value = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        assertFalse(ExecutionContext.isTrue(value));
    }

    @Test
    @DisplayName("la pila mantiene varios valores")
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
    @DisplayName("varios agregar y quitar funcionan bien")
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
    @DisplayName("alternar agregar y quitar funciona")
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

    @Test
    @DisplayName("agregar y quitar varios bytes funciona")
    public void testMultiByteValue() {
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
    }

    @Test
    @DisplayName("un arreglo grande funciona")
    public void testLargeByteArray() {
        byte[] data = new byte[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        ctx.push(data);
        byte[] popped = ctx.pop();
        assertArrayEquals(data, popped);
    }

    @Test
    @DisplayName("agregar un solo byte funciona")
    public void testSingleByteValue() {
        ctx.push(new byte[]{42});
        byte[] value = ctx.pop();
        assertEquals(42, value[0]);
    }

    @Test
    @DisplayName("agregar un arreglo de ceros funciona")
    public void testZeroByteArray() {
        byte[] data = new byte[]{0};
        ctx.push(data);
        assertFalse(ExecutionContext.isTrue(ctx.peek()));
    }

    @Test
    @DisplayName("varios quitar en pila vacia siguen lanzando error")
    public void testMultiplePopsEmptyStack() {
        assertThrows(RuntimeException.class, () -> ctx.pop());
        assertThrows(RuntimeException.class, () -> ctx.pop());
    }

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
