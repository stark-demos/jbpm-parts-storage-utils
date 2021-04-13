package com.stark.parts_storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.stark.parts_storage.mock.MockProcessContext;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessContext;

public class PartsStorageUtilTest {

    @Test
    public void testGetInventorySuccess(){
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        PartsStorageUtil.getInventoryAvailable(response, context);
        assertTrue("Should have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotEnoughInventory() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 700);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        PartsStorageUtil.getInventoryAvailable(response, context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotAppropriateQuantityGiven() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", "FAIL");
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        PartsStorageUtil.getInventoryAvailable(response, context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotAppropriateResponseGiven() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        String response = "FAIL";
        PartsStorageUtil.getInventoryAvailable(response, context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNegativeInventory(){
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": -50}";
        PartsStorageUtil.getInventoryAvailable(response, context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testQueryRequest() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        PartsStorageUtil.assignJsonQueryRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }
}
