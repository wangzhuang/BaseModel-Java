package com.king.basemodel.beans;

public class ReportLogEvent {
    private String packageName;//应用包名
    private String appName;//应用名称
    private String appClient;//终端：原生APP、vue或小程序
    private String phoneModel;//手机型号
    private String phoneOS;//操作系统：Android或iOS
    private String osVersion;//操作系统版本号
    private String appVersion;//APP版本号
    private String userName;//已登录的用户名
    private String userId;//已登录的用户id
    private String requestUrl;//请求的url
    private String requestParams;//请求的参数
    private String returnCode;//出错时返回的code
    private String returnData;//出错时返回的data
    private String timeStamp;//接口发生错误时候的时间戳
    private String environment;//环境   测试、预发布、生产调试、生产
    private String accessToken;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppClient() {
        return appClient;
    }

    public void setAppClient(String appClient) {
        this.appClient = appClient;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneOS() {
        return phoneOS;
    }

    public void setPhoneOS(String phoneOS) {
        this.phoneOS = phoneOS;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String returnData) {
        this.returnData = returnData;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "ReportLogEvent{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", appClient='" + appClient + '\'' +
                ", phoneModel='" + phoneModel + '\'' +
                ", phoneOS='" + phoneOS + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", returnData='" + returnData + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", environment='" + environment + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
