package com.devops.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private final CalculatorService calculator = new CalculatorService();

    @Test
    @DisplayName("Addition should return correct sum")
    void testAdd() {
        assertEquals(10, calculator.add(7, 3));
        assertEquals(0, calculator.add(-5, 5));
        assertEquals(-8, calculator.add(-3, -5));
    }

    @Test
    @DisplayName("Subtraction should return correct difference")
    void testSubtract() {
        assertEquals(4, calculator.subtract(9, 5));
        assertEquals(-10, calculator.subtract(0, 10));
    }

    @Test
    @DisplayName("Multiply should return correct product")
    void testMultiply() {
        assertEquals(20, calculator.multiply(4, 5));
        assertEquals(0, calculator.multiply(0, 100));
    }

    @Test
    @DisplayName("Divide by zero should throw exception")
    void testDivideByZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(10, 0);
        });
    }

    @Test
    @DisplayName("Division should return correct result")
    void testDivide() {
        assertEquals(2.5, calculator.divide(5, 2));
    }
}
