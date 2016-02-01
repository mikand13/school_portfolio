package no.wsact.mikand.pg4100.assignment2.views;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.controllers
 * <p>
 * This class checks the calculation business logic in the Calculator. Havent focused on this as
 * its not part of the assignment scope.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class CalculatorTest {
    private Calculator calculatorTest;

    /**
     * Initializes an offline Calculator for testing of business logic.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        calculatorTest = new Calculator(null, null);
    }

    /**
     * Kills the offline Calculator.
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        calculatorTest = null;
    }

    /**
     * Checks calculation with all basic operators.
     *
     * @throws Exception Exception
     */
    @Test
    public void testCalculateNumbers() throws Exception {
        Method method = calculatorTest.getClass().getDeclaredMethod("calculateNumbers",
                char.class, double.class, double.class);
        method.setAccessible(true);

        assertEquals(2.0, method.invoke(calculatorTest, '+', 1.0, 1.0));
        assertEquals(1.0, method.invoke(calculatorTest, '-', 2.0, 1.0));
        assertEquals(2.0, method.invoke(calculatorTest, '/', 10.0, 5.0));
        assertEquals(10.0, method.invoke(calculatorTest, '*', 5.0, 2.0));
    }
}