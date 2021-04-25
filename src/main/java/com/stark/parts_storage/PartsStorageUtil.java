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

    public static void assignJsonQueryRequest(ProcessContext context) {
        logger.debug("About to assign variable for webService request to process instance {}", context.getProcessInstance().getId());
        String partCode = (String) context.getVariable("partCode");
        InventoryQueryRequest oResult = new InventoryQueryRequest();
        oResult.setPartCode(partCode);

        ObjectMapper obj = new ObjectMapper();
        String result = "{}";
        try {
            result = obj.writeValueAsString(oResult);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing Part Info Request", e);
        }

        context.setVariable("wsJsonRequest", result);
        logger.debug("Json Request set as {}", result);
    }

    public static void getInventoryAvailable(String response, ProcessContext context) {
        logger.debug("Parsing response from web service to process instance {}", context.getProcessInstance().getId());
        Pattern p = Pattern.compile("\"availableQuantity\":\\s*(-?\\d+(\\.\\d+)?)");
        Matcher m = p.matcher(response);
        if(m.find()) {
            String sAvail = m.group(1);
            if(sAvail != null) {
                
                Boolean bAvail = Boolean.FALSE;
                try {
                    Integer iAvail = Integer.parseInt(sAvail);
                    Integer requestedQuantiy = (Integer) context.getVariable("quantity");
                    bAvail = iAvail - requestedQuantiy >= 0;
                } catch(NumberFormatException | ClassCastException e) {
                    bAvail = Boolean.FALSE;
                }
                
                logger.debug("setting inventory available flag with value {}", bAvail);
                context.setVariable("partsAvailable", bAvail);
            }
        } else {
            logger.debug("setting inventory available flag with value False (availableQuantity not found in web service response)");
            context.setVariable("partsAvailable", Boolean.FALSE);
        }
    }
}