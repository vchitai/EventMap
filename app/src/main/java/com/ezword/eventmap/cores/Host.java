package com.ezword.eventmap.cores;

import java.io.Serializable;

public class Host implements Serializable {
    private String hostId;
    private String name;
    private String logo;
    private String link;
    private String email;
    private String phone;

    public Host() {

    }

    public String getHostId() {
        return hostId;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLogo() {
        return logo;
    }
}
