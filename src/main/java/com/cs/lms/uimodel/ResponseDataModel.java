package com.cs.lms.uimodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResponseDataModel<RESPONSE, ERROR> {
    RESPONSE data;
    String status;
    ERROR error;

    public ResponseDataModel(RESPONSE data, ERROR error, String status){
        this.data = data;
        this.error = error;
        this.status = status;
    }

    public RESPONSE getData() {
        return data;
    }

    public void setData(RESPONSE data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ERROR getError() {
        return error;
    }

    public void setError(ERROR error) {
        this.error = error;
    }

    public static <RESPONSE, E extends Throwable> ResponseDataModel withResponse(RESPONSE response, E error){
        if(Objects.nonNull(error)){
            var errorMap = new HashMap<>();
            errorMap.put("errorCode", "server_error");
            errorMap.put("errorMsg", error.getMessage());
            return new ResponseDataModel(null, error, "error");
        }else{
            return new ResponseDataModel(response, null, "success");
        }
    }
}
