package com.stark.parts_storage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.kie.api.runtime.process.ProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartsStorageUtil {
    private static Logger logger = LoggerFactory.getLogger(PartsStorageUtil.class);

    /**
     * Uses the JBPM Process Instance context to assing a JSON request for the web
     * service project: <url here>
     * 
     * @param context
     */
    public static void assignJsonQueryRequest(ProcessContext context) {
        logger.debug("About to assign variable for webService request to process instance {}",
                context.getProcessInstance().getId());
        String partCode = String.valueOf(context.getVariable("partCode"));
        InventoryQueryRequest oRequest = new InventoryQueryRequest();
        oRequest.setPartCode(partCode);

        ObjectMapper obj = new ObjectMapper();
        String result = "{}";
        try {
            result = obj.writeValueAsString(oRequest);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Part Info Request", e);
        }

        context.setVariable("wsJsonRequest", result);
        logger.debug("Json Request set as {}", result);
    }

    /**
     * Uses the JBPM Process Instance context to assing a JSON request for the web
     * service project <url here>
     * 
     * @param context
     */
    public static void jsonQueryRequestForRepairRequest(ProcessContext context) {
        logger.debug("About to assign variable for webService request to process instance {}",
                context.getProcessInstance().getId());
        String partCode = String.valueOf(context.getVariable("partCode"));
        InventoryReservationRequest oRequest = new InventoryReservationRequest();
        oRequest.setPartCode(partCode);

        String branchCode = String.valueOf(context.getVariable("branchCode"));
        oRequest.setBranchCode(branchCode);

        Integer requestedQuantiy = (Integer) context.getVariable("quantity");
        oRequest.setQuantity(requestedQuantiy);

        String repairRequestId = String.valueOf(context.getVariable("repairRequestId"));
        oRequest.setRepairRequestId(repairRequestId);

        ObjectMapper obj = new ObjectMapper();
        String result = "{}";
        try {
            result = obj.writeValueAsString(oRequest);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Part Info Request", e);
        }

        context.setVariable("wsJsonRequest", result);
        logger.debug("Json Request set as {}", result);
    }

    /**
     * Given a response from web service <url here>, set the partsAssigned boolean
     * variable for the given jbpm Process instance kcontext.
     * 
     * @param context
     */
    public static void getRepairAssignmentResponse(ProcessContext context) {
        logger.debug("Parsing response from web service to process instance {}", context.getProcessInstance().getId());
        Pattern p = Pattern.compile("\"reservationId\":\\s*\\\"?([a-zA-Z-\\d]+)\"?");

        String response = (String) context.getVariable("wsJsonResponse");
        if (response == null) {
            logger.debug("No response found in kcontext");
            context.setVariable("partsAssigned", null);
            return;
        }

        Matcher m = p.matcher(response);
        if (m.find()) {
            String reservationId = m.group(1);
            context.setVariable("reservationId", reservationId);
            context.setVariable("partsAssigned", Boolean.TRUE);
            logger.debug("Parts assigned with reservation id {}", reservationId);
        } else {
            logger.debug(
                    "setting inventory available flag with value False (reservationId not found in web service response)");
            context.setVariable("partsAssigned", Boolean.FALSE);
        }
    }

    /**
     * Given a response from web service <url here>, set the partsAvailable boolean
     * variable for the given jbpm Process instance kcontext.
     * 
     * @param context
     */
    public static void getInventoryAvailable(ProcessContext context) {
        logger.debug("Parsing response from web service to process instance {}", context.getProcessInstance().getId());
        Pattern p = Pattern.compile("\"availableQuantity\":\\s*(-?\\d+(\\.\\d+)?)");

        String response = (String) context.getVariable("wsJsonResponse");
        if (response == null) {
            logger.debug("No response found in kcontext");
            context.setVariable("partsAvailable", null);
            return;
        }

        Matcher m = p.matcher(response);
        if (m.find()) {
            String sAvail = m.group(1);
            if (sAvail != null) {

                Boolean bAvail = Boolean.FALSE;
                try {
                    Integer iAvail = Integer.parseInt(sAvail);
                    Integer requestedQuantity = (Integer) context.getVariable("quantity");
                    if (requestedQuantity == null) {
                        logger.warn("Requested quantity not found in process instance");
                        requestedQuantity = 0;
                    }
                    bAvail = iAvail - requestedQuantity >= 0;
                    logger.debug("available {}, requested {}", iAvail, requestedQuantity);
                    logger.debug("setting inventory available flag with value {}", bAvail);
                    context.setVariable("partsAvailable", bAvail);
                } catch (NumberFormatException | ClassCastException e) {
                    logger.error("Unable to parse amounts", e);
                    context.setVariable("partsAvailable", null);
                }
            }
        } else {
            logger.debug(
                    "setting inventory available flag with value False (availableQuantity not found in web service response)");
            context.setVariable("partsAvailable", null);
        }
    }
}