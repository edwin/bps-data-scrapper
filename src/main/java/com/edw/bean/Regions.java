package com.edw.bean;

import java.io.Serializable;

/**
 * <pre>
 *     com.edw.bean.Regions
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 14 Des 2021 12:18
 */
public class Regions implements Serializable {

    private Long id;
    private String regionCode;
    private String regionName;
    private String parentCode;

    public Regions() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
