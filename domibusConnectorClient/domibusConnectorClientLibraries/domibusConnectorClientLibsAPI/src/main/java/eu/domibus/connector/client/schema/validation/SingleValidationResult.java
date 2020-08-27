package eu.domibus.connector.client.schema.validation;

public class SingleValidationResult {

    private final SeverityLevel level;

    private final String result;

    public SingleValidationResult(SeverityLevel level, String result) {
        super();
        this.level = level;
        this.result = result;
    }


    public SeverityLevel getLevel() {
        return level;
    }

    public String getResult() {
        return result;
    }

    public String getSingleResultString() {
        return level.name() + ": " + result;
    }

}
