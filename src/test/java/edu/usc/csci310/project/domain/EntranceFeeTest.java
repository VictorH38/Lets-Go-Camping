package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntranceFeeTest {
    @Test
    void testGetterAndSetter() {
        EntranceFee fee = new EntranceFee();
        fee.setId(4L);
        fee.setDescription("Test Fee for Testing Purposes");
        fee.setCost(10.00);
        fee.setTitle("Test Fee");

        Park park = new Park();
        park.setId(2L);
        fee.setPark(park);

        assertEquals(4L, fee.getId());
        assertEquals("Test Fee for Testing Purposes", fee.getDescription());
        assertEquals("Test Fee", fee.getTitle());
        assertEquals(10, fee.getCost());

        assertNotNull(fee.getPark());
        assertEquals(2L, fee.getPark().getId());
    }

    @Test
    void testConstructor() {
        EntranceFee fee = new EntranceFee(2L, "An Unnecessary Fee you Definitely Need", "Unnecessary Fee", 200.00);
        assertEquals(2L,fee.getId());
        assertEquals("An Unnecessary Fee you Definitely Need",fee.getDescription());
        assertEquals("Unnecessary Fee",fee.getTitle());
        assertEquals(200.00,fee.getCost());
    }
}