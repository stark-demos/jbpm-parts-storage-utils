package com.stark.parts_storage.mock;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;

public class MockProcessContext implements ProcessContext {
    private Map<String, Object> variables = new HashMap<>();
    
    @Override
    public Object getVariable(String variableName) {
        return variables.get(variableName);
    }

    @Override
    public void setVariable(String variableName, Object value) {
       variables.put(variableName, value);
    }

    @Override
    public KieRuntime getKieRuntime() {
        return null;
    }

    @Override
    public KieRuntime getKnowledgeRuntime() {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance() {
        return null;
    }

    @Override
    public NodeInstance getNodeInstance() {
        return null;
    }

    

    @Override
    public CaseAssignment getCaseAssignment() {
        return null;
    }

    @Override
    public CaseData getCaseData() {
        return null;
    }
    
}
