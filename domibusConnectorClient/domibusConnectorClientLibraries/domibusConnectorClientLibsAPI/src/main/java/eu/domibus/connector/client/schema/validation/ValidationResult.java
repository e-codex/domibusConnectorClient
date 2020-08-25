package eu.domibus.connector.client.schema.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ValidationResult {

    private final Set<SingleValidationResult> validationResults = new HashSet<SingleValidationResult>();

    public ValidationResult() {
    }
    
    public boolean isOkay() {
    	return validationResults.isEmpty();
    }

    public boolean isFatal() {
        return iterate(SeverityLevel.FATAL_ERROR);
    }

    public boolean isError() {
        return iterate(SeverityLevel.ERROR);
    }

    public boolean isWarning() {
        return iterate(SeverityLevel.WARNING);
    }

    public SeverityLevel maxSeverityLevel() {
    	if(iterate(SeverityLevel.FATAL_ERROR)) {
    		return SeverityLevel.FATAL_ERROR;
    	}
    	if(iterate(SeverityLevel.ERROR)) {
    		return SeverityLevel.ERROR;
    	}
    	if(iterate(SeverityLevel.WARNING)) {
    		return SeverityLevel.WARNING;
    	}
    	return null;
    }
    
    private boolean iterate(SeverityLevel level) {
        Iterator<SingleValidationResult> it1 = validationResults.iterator();
        while (it1.hasNext()) {
            if (it1.next().getLevel() == level)
                return true;
        }

        return false;
    }

    public Set<SingleValidationResult> getValidationResults() {
        return validationResults;
    }

}
