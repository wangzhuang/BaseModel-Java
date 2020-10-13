package com.king.basemodel.beans;

import java.io.Serializable;

public class ResponseBean implements Serializable {

    private static final long serialVersionUID = 2125579218442633445L;
    /**
     * code : 200
     * msg : 查询成功
     * data : {}
     */

    private String code;
    private String msg;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
