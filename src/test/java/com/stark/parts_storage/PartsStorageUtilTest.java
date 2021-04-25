package com.stark.parts_storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.stark.parts_storage.mock.MockProcessContext;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessContext;

public class PartsStorageUtilTest {

    @Test
    public void testGetInventorySuccess(){
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getInventoryAvailable(context);
        assertTrue("Should have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotEnoughInventory() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 700);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getInventoryAvailable(context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotAppropriateQuantityGiven() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", "FAIL");
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": 350}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getInventoryAvailable(context);
        assertNull(context.getVariable("partsAvailable"));
    }

    @Test
    public void testNotAppropriateResponseGiven() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        context.setVariable("wsJsonResponse", "FAIL");
        PartsStorageUtil.getInventoryAvailable(context);
        assertNull(context.getVariable("partsAvailable"));
    }

    @Test
    public void testNegativeInventory(){
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 50);
        String response = "{\"partCode\": \"abc-ABC-123\", \"availableQuantity\": -50}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getInventoryAvailable(context);
        assertFalse("Should not have parts available", (Boolean) context.getVariable("partsAvailable"));
    }

    @Test
    public void testNoResponseGiven() {
        ProcessContext context = new MockProcessContext();
        PartsStorageUtil.getInventoryAvailable(context);
        assertNull(context.getVariable("partsAvailable"));
    }

    @Test
    public void testQueryRequest() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        PartsStorageUtil.assignJsonQueryRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssingInventorySuccessStringId() {
        ProcessContext context = new MockProcessContext();
        String response = "{\"reservationId\": \"abc-ABC-123\", \"remainingParts\": 0}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getRepairAssignmentResponse(context);
        assertTrue("Should report success", (Boolean) context.getVariable("partsAssigned"));
        assertEquals("abc-ABC-123", context.getVariable("reservationId"));
    }

    @Test
    public void testAssingInventorySuccessNumericId() {
        ProcessContext context = new MockProcessContext();
        String response = "{\"reservationId\": 123, \"remainingParts\": 0}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getRepairAssignmentResponse(context);
        assertTrue("Should report success", (Boolean) context.getVariable("partsAssigned"));
        assertEquals("123", context.getVariable("reservationId"));
    }

    @Test
    public void testAssignInventoryFail() {
        ProcessContext context = new MockProcessContext();
        String response = "{\"responseCode\": 123, \"message\": \"No parts available\"}";
        context.setVariable("wsJsonResponse", response);
        PartsStorageUtil.getRepairAssignmentResponse(context);
        assertFalse("Should NOT report success", (Boolean) context.getVariable("partsAssigned"));
        assertNull(context.getVariable("reservationId"));
    }

    @Test
    public void testAssignInventoryNoResponseGiven() {
        ProcessContext context = new MockProcessContext();
        
        PartsStorageUtil.getRepairAssignmentResponse(context);
        assertNull(context.getVariable("partsAssigned"));
        assertNull(context.getVariable("reservationId"));
    }

    @Test
    public void testAssignInventoryRequest() {
        // TODO: Implement test
        fail("Not implemented yet");
    }

    @Test
    public void testAssignInventoryRequestNullQuantity() {
        // TODO: Implement test
        fail("Not implemented yet");
    }

    @Test
    public void testAssingInventoryRequestNullPartCode() {
        // TODO: Implement test
        fail("Not implemented yet");
    }


    @Test
    public void testAssignInbentoryRequestNullRequestId() {
        // TODO: Implement test
        fail("Not implemented yet");
    }

}
