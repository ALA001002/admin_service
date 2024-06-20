package com.bigo.project.bigo.api.domain;

import lombok.Data;

/**
 * @author wenxm
 */
@Data
public class BigoVersion {
    /**
     * 最新版本号
     */
    private String version;

    private String url;

    private String remark;


    public BigoVersion() {
    }

    public BigoVersion(String version, String url, String remark) {
        this.version = version;
        this.url = url;
        this.remark = remark;
    }
}
