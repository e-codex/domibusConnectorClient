package eu.domibus.connector.client.admin.rest.dto;

public class ConfigPropertyDTO {

    private String name;
    private String value;

    public ConfigPropertyDTO() {}

    public ConfigPropertyDTO(String name, String value) {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
