package com.stark.parts_storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.stark.test.MockProcessContext;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessContext;

public class PartsStorageUtilTest {

    @Test
    public void testGetInventorySuccess() {
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
    public void testNegativeInventory() {
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
    public void testQueryRequestNullPartCode() {
        ProcessContext context = new MockProcessContext();
        PartsStorageUtil.assignJsonQueryRequest(context);
        String wsJsonRequest = "{\"partCode\":\"null\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testQueryRequestNumericPartCode() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", 1);
        PartsStorageUtil.assignJsonQueryRequest(context);
        String wsJsonRequest = "{\"partCode\":\"1\"}";
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

        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        context.setVariable("quantity", 1);
        context.setVariable("repairRequestId", "ABC-123-1");
        context.setVariable("branchCode", "ABC-456-a");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\",\"quantity\":1,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"ABC-456-a\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssignInventoryRequestNullQuantity() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        context.setVariable("repairRequestId", "ABC-123-1");
        context.setVariable("branchCode", "ABC-456-a");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\",\"quantity\":null,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"ABC-456-a\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssingInventoryRequestNullPartCode() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("quantity", 1);
        context.setVariable("repairRequestId", "ABC-123-1");
        context.setVariable("branchCode", "ABC-456-a");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"null\",\"quantity\":1,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"ABC-456-a\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssignInventoryRequestNullRequestId() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        context.setVariable("quantity", 1);
        context.setVariable("branchCode", "ABC-456-a");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\",\"quantity\":1,\"repairRequestId\":\"null\",\"branchCode\":\"ABC-456-a\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssingInventoryRequestIntegerPartCode() {
        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", 1);
        context.setVariable("quantity", 1);
        context.setVariable("repairRequestId", "ABC-123-1");
        context.setVariable("branchCode", "ABC-456-a");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"1\",\"quantity\":1,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"ABC-456-a\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssignInventoryRequestNullBranchCode() {

        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        context.setVariable("quantity", 1);
        context.setVariable("repairRequestId", "ABC-123-1");

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\",\"quantity\":1,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"null\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

    @Test
    public void testAssignInventoryRequestIntegerBranchCode() {

        ProcessContext context = new MockProcessContext();
        context.setVariable("partCode", "ABC-123");
        context.setVariable("quantity", 1);
        context.setVariable("repairRequestId", "ABC-123-1");
        context.setVariable("branchCode", 1);

        PartsStorageUtil.jsonQueryRequestForRepairRequest(context);
        String wsJsonRequest = "{\"partCode\":\"ABC-123\",\"quantity\":1,\"repairRequestId\":\"ABC-123-1\",\"branchCode\":\"1\"}";
        assertEquals(wsJsonRequest, context.getVariable("wsJsonRequest"));
    }

}
