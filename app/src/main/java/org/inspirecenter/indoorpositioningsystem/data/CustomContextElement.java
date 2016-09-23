package org.inspirecenter.indoorpositioningsystem.data;

import java.io.Serializable;

/**
 * @author Nearchos
 *         Created: 23-Sep-16
 */

public class CustomContextElement implements Serializable
{
    private String uuid;
    private String locationUuid;
    private String name;
    private String value;
    private boolean checked;

    public CustomContextElement(final String uuid, final String locationUuid, final String name, final String value, final boolean checked) {
        this.uuid = uuid;
        this.locationUuid = locationUuid;
        this.name = name;
        this.value = value;
        this.checked = checked;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isChecked() {
        return checked;
    }
}