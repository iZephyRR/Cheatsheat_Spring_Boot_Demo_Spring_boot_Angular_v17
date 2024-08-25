package com.cheatsheet;

import com.cheatsheet.controller.Calculate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateTest {
    Calculate cal = new Calculate();
    int sum = cal.sum(2,5);
    int testSum=7;

    @Test
    public void testSum() {
        System.out.println("@Test sum(): " + sum + " = " + testSum);
        assertEquals(testSum, sum);
    }


}
