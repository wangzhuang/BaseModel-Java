package com.king.basemodel.http;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.king.basemodel.BuildConfig;
import com.king.basemodel.activity.LoginActivity;
import com.king.basemodel.beans.ReportLogEvent;
import com.king.basemodel.common.GlobalKt;
import com.king.basemodel.utils.JsonUtil;
import com.king.basemodel.utils.SharePreferenceUtils;
import com.king.basemodel.utils.StringUtil;
import com.king.basemodel.utils.ToastUtils;
import com.king.basemodel.widget.InfoDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Iterator;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.king.basemodel.common.GlobalKt.getAppCtx;
import static com.king.basemodel.common.GlobalKt.log;
import static com.king.basemodel.http.Interface.AppHead;
import static com.king.basemodel.utils.Judge.n;
import static com.king.basemodel.utils.Judge.p;


public class HttpUtil {

    private static Activity mActivity;
    private static InfoDialog loginInfoDialog;
    private static final int WHAT_LOGIN = 0;
    private static final int WHAT_SHOWTOAST = 1;
    private static final int WHAT_REPORT = 2;
    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case WHAT_LOGIN:
                        if (p(loginInfoDialog) && loginInfoDialog.isShowing()) {
                            return;
                        }
                        loginInfoDialog = new InfoDialog(mActivity);
                        loginInfoDialog.show("很抱歉，当前页面已过期，请重新登录。", "确定", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                if (loginInfoDialog != null && loginInfoDialog.isShowing()) {
                                    loginInfoDialog.dismiss();
                                }
                                Intent intent = new Intent(mActivity, LoginActivity.class);
                                intent.putExtra("tokenErr", true);
                                mActivity.startActivity(intent);
                                mActivity.finish();
                                return null;
                            }
                        });
                        break;
                    case WHAT_SHOWTOAST:
                        ToastUtils.showToasts(String.valueOf(msg.obj));
                        break;
                    case WHAT_REPORT:
                        ReportLogEvent event = (ReportLogEvent) (msg.obj);
                        if (n(event) || !AppHead.equals("https://zcs-app-gw.lunz.cn/")) {
                            return;
                        }
                        event.setPackageName("com.cwsk.twowheeler");
                        event.setAppName("APP名称");
                        event.setAppClient("原生");
                        event.setPhoneModel(Build.BRAND + "  " + Build.MODEL);
                        event.setPhoneOS("android");
                        event.setOsVersion(Build.VERSION.RELEASE);
                        event.setAppVersion(BuildConfig.VERSION_NAME);
                        event.setUserName(SharePreferenceUtils.getString(getAppCtx(), "phone"));
                        event.setUserId(SharePreferenceUtils.getString(getAppCtx(), "id"));
                        event.setTimeStamp(GlobalKt.getSdf3().format(new Date()));
                        String env = null;
                        if (AppHead.equals("http://zcs-app-gw-pre.lunz.cn/")) {
                            env = "预发布环境";
                        } else if (AppHead.equals("https://zcs-app-gw.lunz.cn/")) {
                            env = "生产环境";
                        } else {
                            env = "测试环境";
                        }
                        event.setEnvironment(env);
                        event.setAccessToken(SharePreferenceUtils.getString(getAppCtx(), "token"));
                        log("Http", "[上报错误日志] " + event.toString());
                        JSONObject js = null;
                        try {
                            js = new JSONObject(JsonUtil.objectToJson(event));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        httpPostString(Interface.PostReportErrorLog, js, "Http", new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                log("Http", "[上报错误日志] onError: " + (e != null ? e.getMessage() : "异常信息为空"));
                            }

                            @Override
                            public void onResponse(String response, int id1) {
                                log("Http", "[上报错误日志] onResponse: " + response);
                            }
                        });
                        break;
                }
            } catch (Exception e) {
                log("Http", "Http handler 处理异常: " + e.getMessage());
            }
            super.handleMessage(msg);
        }
    };

    public static void httpGet(@NonNull String url, JSONObject paramsJson, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpGet(url, paramsJson, 0, tag, activity, 1, httpCallback);
    }

    public static void httpGet(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpGet(url, paramsJson, id, tag, activity, 1, httpCallback);
    }

    public static void httpPostString(@NonNull String url, JSONObject paramsJson, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPostString(url, paramsJson, 0, tag, activity, 1, httpCallback);
    }

    public static void httpPostString(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPostString(url, paramsJson, id, tag, activity, 1, httpCallback);
    }

    public static void httpPost(@NonNull String url, JSONObject paramsJson, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPost(url, paramsJson, 0, tag, activity, 1, httpCallback);
    }

    public static void httpPost(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPost(url, paramsJson, id, tag, activity, 1, httpCallback);
    }

    public static void httpPut(@NonNull String url, JSONObject paramsJson, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPut(url, paramsJson, 0, tag, activity, 1, httpCallback);
    }

    public static void httpPut(@NonNull String url, @NonNull JSONObject paramsJson, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpPut(url, paramsJson, id, tag, activity, 1, httpCallback);
    }

    public static void httpDelete(@NonNull String url, JSONObject paramsJson, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpDelete(url, paramsJson, 0, tag, activity, 1, httpCallback);
    }

    public static void httpDelete(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
        httpDelete(url, paramsJson, id, tag, activity, 1, httpCallback);
    }
    //    public static void httpUpload(@NonNull String url, Map<String, String> paramsMap, Map<String, File> fileMap, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
//        httpUpload(url, paramsMap, fileMap, 0, tag, activity, httpCallback);
//    }
//
//    public static void httpFile(@NonNull String url, Map<String, String> paramsMap, String fileKey, File file, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
//        httpFile(url, paramsMap, fileKey, file, 0, tag, activity, httpCallback);
//    }

    public static void httpGet(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, @NonNull HttpCallback httpCallback) {
        if (p(activity)) {
            mActivity = activity;
        }
        WeakReference referenceActivity = new WeakReference<>(activity);
        GetBuilder builder = OkHttpUtils.get();
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpGet ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        if (p(paramsJson)) {
            Iterator<String> keys = paramsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    builder.addParams(key, String.valueOf(StringUtil.ifNul(paramsJson.get(key))));
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(tag + " httpGet ", "解析入参异常 " + url);
                }
            }
        }
        RequestCall call = builder.url(url).tag(tag).id(id).build();
        call.execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onBefore(id);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onAfter(id);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processError(p(e) ? e.getMessage() : null, id, tag, httpCallback);
            }

            @Override
            public void onResponse(String response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return false;
                }
                return processValidateResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }
        });
    }

    public static void httpPostString(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, @NonNull HttpCallback httpCallback) {
        if (p(activity)) {
            mActivity = activity;
        }
        WeakReference referenceActivity = new WeakReference<>(activity);
        PostStringBuilder builder = OkHttpUtils.postString().id(id).tag(tag).url(url);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpPostString ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        RequestCall call = null;
        if (p(paramsJson)) {
            call = builder.content(paramsJson.toString()).mediaType(MediaType.parse("application/json; charset=utf-8")).build();
        } else {
            call = builder.build();
        }
        call.execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onBefore(id);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onAfter(id);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processError(p(e) ? e.getMessage() : null, id, tag, httpCallback);
            }

            @Override
            public void onResponse(String response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return false;
                }
                return processValidateResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }
        });
    }

    public static void httpPost(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, @NonNull HttpCallback httpCallback) {
        if (p(activity)) {
            mActivity = activity;
        }
        WeakReference referenceActivity = new WeakReference<>(activity);
        PostFormBuilder builder = OkHttpUtils.post().id(id).tag(tag).url(url);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpPost ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        if (p(paramsJson)) {
            Iterator<String> keys = paramsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    builder.addParams(key, String.valueOf(StringUtil.ifNul(paramsJson.get(key))));
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(tag, " 解析入参异常 " + url);
                }
            }
        }
        RequestCall call = builder.build();
        call.execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onBefore(id);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onAfter(id);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processError(p(e) ? e.getMessage() : null, id, tag, httpCallback);
            }

            @Override
            public void onResponse(String response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return false;
                }
                return processValidateResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }
        });


    }

    public static void httpPut(@NonNull String url, @NonNull JSONObject paramsJson, int id, String tag, Activity activity, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, @NonNull HttpCallback httpCallback) {
        if (p(activity)) {
            mActivity = activity;
        }
        WeakReference referenceActivity = new WeakReference<>(activity);
        OtherRequestBuilder builder = OkHttpUtils.put().url(url).id(id).tag(tag);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpPut ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        RequestCall call = null;
        if (p(paramsJson)) {
            call = builder.requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramsJson.toString())).build();
        } else {
            call = builder.build();
        }
        call.execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onBefore(id);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onAfter(id);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processError(p(e) ? e.getMessage() : null, id, tag, httpCallback);
            }

            @Override
            public void onResponse(String response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return false;
                }
                return processValidateResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }
        });
    }

    public static void httpDelete(@NonNull String url, JSONObject paramsJson, int id, String tag, Activity activity, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, @NonNull HttpCallback httpCallback) {
        if (p(activity)) {
            mActivity = activity;
        }
        WeakReference referenceActivity = new WeakReference<>(activity);
        OtherRequestBuilder builder = OkHttpUtils.delete().url(url).id(id).tag(tag);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpDelete ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        RequestCall call = null;
        if (p(paramsJson)) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            Iterator<String> keys = paramsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    formBuilder.add(key, String.valueOf(StringUtil.ifNul(paramsJson.get(key))));
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(tag, " 解析入参异常 " + url);
                }
            }
            call = builder.requestBody(formBuilder.build()).build();
        } else {
            call = builder.build();
        }
        call.execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onBefore(id);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                if (p(httpCallback)) {
                    httpCallback.onAfter(id);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processError(p(e) ? e.getMessage() : null, id, tag, httpCallback);
            }

            @Override
            public void onResponse(String response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return;
                }
                processResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                if (p(referenceActivity.get()) && ((Activity) referenceActivity.get()).isDestroyed()) {
                    return false;
                }
                return processValidateResponse(response, url, paramsJson, id, tag, function, httpCallback);
            }
        });
    }

//    //单个限制10M，总共限制100M
//    public static RequestCall httpUpload(@NonNull String url, Map<String, String> paramsMap, Map<String, File> fileMap, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
//        if (p(activity)) {
//            mActivity = activity;
//        }
//        PostFormBuilder builder = OkHttpUtils.post().url(url).id(id).tag(tag);
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpUpload " + tag, url);
//        if (paramsMap != null) {
//            log("httpUpload", paramsMap.toString());
//            builder.params(paramsMap);
//        }
//        if (fileMap != null) {
//            log("httpUpload", fileMap.toString());
//            builder.files("files", fileMap);
//        }
//        RequestCall call = builder.build();
//        call.execute(new StringCallback() {
//
//            @Override
//            public void onBefore(Request request, int id) {
//                super.onBefore(request, id);
//                if (p(httpCallback)) {
//                    httpCallback.onBefore(id);
//                }
//            }
//
//            @Override
//            public void onAfter(int id) {
//                super.onAfter(id);
//                if (p(httpCallback)) {
//                    httpCallback.onAfter(id);
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                processResponse(response, id, tag, httpCallback);
//            }
//
//            @Override
//            public boolean validateReponse(Response response, int id) {
//                return processValidateResponse(response, id, tag, httpCallback);
//            }
//        });
//        return call;
//    }

//    public static RequestCall httpFile(@NonNull String url, Map<String, String> paramsMap, String fileKey, File file, int id, String tag, Activity activity, @NonNull HttpCallback httpCallback) {
//        if (p(activity)) {
//            mActivity = activity;
//        }
//        PostFormBuilder builder = OkHttpUtils.post().url(url).id(id).tag(tag);
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpFile " + tag, url);
//        if (paramsMap != null) {
//            log("httpFile", paramsMap.toString());
//            builder.params(paramsMap);
//        }
//        builder.addFile(fileKey, file.getName(), file);
//        RequestCall call = builder.build();
//        call.execute(new StringCallback() {
//
//            @Override
//            public void onBefore(Request request, int id) {
//                super.onBefore(request, id);
//                if (p(httpCallback)) {
//                    httpCallback.onBefore(id);
//                }
//            }
//
//            @Override
//            public void onAfter(int id) {
//                super.onAfter(id);
//                if (p(httpCallback)) {
//                    httpCallback.onAfter(id);
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                processResponse(response, id, tag, httpCallback);
//            }
//
//            @Override
//            public boolean validateReponse(Response response, int id) {
//                return processValidateResponse(response, id, tag, httpCallback);
//            }
//        });
//        return call;
//    }
//    private static String getUrlWithParams(String url, JSONObject paramsJson) {
//        if (n(paramsJson)) {
//            return url;
//        }
//        if (url.contains("?")) {
//            url += "&";
//        } else {
//            url += "?";
//        }
//        StringBuilder sb = new StringBuilder(url);
//        Iterator<String> keys = paramsJson.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            try {
//                sb.append(key + "=" + String.valueOf(paramsJson.get(key)) + "&");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        return sb.toString();
//    }

    private static boolean processValidateResponse(Response response, String url, JSONObject paramsJson, int id, String tag, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, HttpCallback httpCallback) {
        ReportLogEvent reportLogEvent = new ReportLogEvent();
        reportLogEvent.setRequestUrl(url);
        reportLogEvent.setRequestParams(p(paramsJson) ? paramsJson.toString() : "");
        if (response != null) {
            try {
                if (response.code() != 200) {
                    String errorMesage = null;
                    switch (response.code()) {// 抛出异常后，okgo会直接回调onError
                        case 404:
                            errorMesage = "很抱歉，您访问的内容不存在。";
                            break;
                        case 408:
                            errorMesage = "已超时，请刷新或重新打开页面。";
                            break;
                        case 429:
                            errorMesage = "操作过于频繁，请稍后再试。";
                            break;
                        case 500:
                        case 501:
                        case 502:
                        case 503:
                        case 504:
                        case 505:
                        case 506:
                        case 507:
                        case 509:
                        case 510:
                            errorMesage = "服务器错误，请稍后重试。";
                            break;
                        case 401:
                            //弹窗token过期提示，引导用户登录
                            handler.sendEmptyMessage(WHAT_LOGIN);
                            break;
                        case 400:
                        case 402:
                        case 403:
                        case 405:
                        case 406:
                        case 407:
                        case 409:
                        case 410:
                        case 411:
                        case 412:
                        case 413:
                        case 414:
                        case 415:
                        case 416:
                        case 417:
                        case 418:
                        case 421:
                        case 422:
                        case 423:
                        case 424:
                        case 425:
                        case 426:
                        case 449:
                        case 451:
                            errorMesage = "发生错误，请稍后重试。";
                            break;
                    }
                    if (p(errorMesage) && function == 1) {
                        Message msg = handler.obtainMessage();
                        msg.what = WHAT_SHOWTOAST;
                        msg.obj = errorMesage;
                        handler.sendMessage(msg);
                    }
                    log(tag, "报错 code：" + response.code() + "  errMsg:" + errorMesage);
                    reportLogEvent.setReturnCode(response.code() + "");
                    reportLogEvent.setReturnData(response.body().string().toString());
//                    EventBus.getDefault().post(reportLogEvent);
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_REPORT;
                    msg.obj = reportLogEvent;
                    handler.sendMessage(msg);
                    String finalErrorMesage = errorMesage;
                    handler.post(() -> {
                        if (p(httpCallback)) {
                            httpCallback.onError(finalErrorMesage, id);
                        }
                    });
                    return false;
                } else {
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log(tag, "[processValidateResponse异常]：" + ex.getMessage());
                if (p(httpCallback)) {
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_SHOWTOAST;
                    msg.obj = ex.getMessage();
                    handler.sendMessage(msg);
                }
            }
        }
        return false;
    }

    private static void processResponse(String response, String url, JSONObject paramsJson, int id, String tag, int function/*0:不提示ret=1时的msg   1:提示ret=1时的msg*/, HttpCallback httpCallback) {
        try {
            log(tag, "成功返回：" + response);
            JSONObject result = new JSONObject(response);
            int ret = result.optInt("ret");
            if (ret != 0) {
                if (function == 1) {
                    if (p(result.optString("message"))) {
                        Message msg = handler.obtainMessage();
                        msg.what = WHAT_SHOWTOAST;
                        msg.obj = result.optString("message");
                        handler.sendMessage(msg);
                    }
                }
                if (p(httpCallback)) {
                    handler.post(() -> {
                        try {
                            httpCallback.onResponse(response, id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                if (p(httpCallback)) {
                    handler.post(() -> {
                        try {
                            httpCallback.onResponse(response, id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (JSONException e) {
            try {
                JSONArray ja = new JSONArray(response);
                if (p(httpCallback)) {
                    handler.post(() -> {
                        try {
                            httpCallback.onResponse(response, id);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return;
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            log(tag, "[processResponse异常]：" + e.getMessage());
            if (p(httpCallback)) {
                Message msg = handler.obtainMessage();
                msg.what = WHAT_SHOWTOAST;
                msg.obj = e.getMessage();
                handler.sendMessage(msg);
                httpCallback.onError(e.getMessage(), id);
            }
        }
    }

    private static void processError(String message, int id, String tag, HttpCallback httpCallback) {
        log(tag, "[onError]" + message);
        try {
            if (p(message) && message.contains("No address associated with hostname")) {
                //断网的情况在这里回调
                Message msg = handler.obtainMessage();
                msg.what = WHAT_SHOWTOAST;
                msg.obj = "网络出走了，稍后再试";
                handler.sendMessage(msg);
                if (p(httpCallback)) {
                    httpCallback.onError("网络出走了，稍后再试", id);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
//    public static RequestCall httpGet(@NonNull String url, JSONObject paramsJson, String tag, @NonNull StringCallback callback) {
//        GetBuilder builder = OkHttpUtils.get();
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpGet " + tag, url);
//        if (paramsJson != null) {
//            log("httpGet " + tag, paramsJson.toString());
//        }
//        if (p(paramsJson)) {
////            url = getUrlWithParams(url, paramsJson);
//            Iterator<String> keys = paramsJson.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                try {
//                    builder.addParams(key, String.valueOf(paramsJson.get(key)));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    log(tag, "httpGet 解析入参异常 " + url);
//                }
//            }
//        }
//        RequestCall call = builder.url(url).tag(tag).build();
//        call.execute(callback);
//        return call;
//    }

    public static void httpPostString(@NonNull String url, JSONObject paramsJson, String tag, @NonNull StringCallback callback) {
        PostStringBuilder builder = OkHttpUtils.postString().url(url).tag(tag);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpPostString ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        RequestCall call = null;
        if (p(paramsJson)) {
            call = builder.content(paramsJson.toString()).mediaType(MediaType.parse("application/json; charset=utf-8")).build();
        } else {
            call = builder.build();
        }
        call.execute(callback);
    }

    public static void httpPost(@NonNull String url, JSONObject paramsJson, String tag, @NonNull StringCallback callback) {
        PostFormBuilder builder = OkHttpUtils.post().url(url).tag(tag);
        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
        }
        log(tag + " httpPost ", url);
        if (paramsJson != null) {
            log(tag + " 入参：", paramsJson.toString());
        }
        log(tag, SharePreferenceUtils.getString(getAppCtx(), "token"));
        if (p(paramsJson)) {
            Iterator<String> keys = paramsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    builder.addParams(key, String.valueOf(paramsJson.get(key)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(tag, " 解析入参异常 " + url);
                }
            }
        }
        RequestCall call = builder.build();
        call.execute(callback);
    }

//    public static RequestCall httpPut(@NonNull String url, @NonNull JSONObject paramsJson, String tag, @NonNull StringCallback callback) {
//        OtherRequestBuilder builder = OkHttpUtils.put().url(url);
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpPut " + tag, url);
//        if (paramsJson != null) {
//            log("httpPut " + tag, paramsJson.toString());
//        }
//        RequestCall call = null;
//        if (p(paramsJson)) {
//            call = builder.requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramsJson.toString())).build();
//        } else {
//            call = builder.build();
//        }
//        call.execute(callback);
//        return call;
//    }
//
//    public static RequestCall httpDelete(@NonNull String url, JSONObject paramsJson, String tag, @NonNull StringCallback callback) {
//        OtherRequestBuilder builder = OkHttpUtils.delete().url(url);
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpDelete " + tag, url);
//        if (paramsJson != null) {
//            log("httpDelete", paramsJson.toString());
//        }
//        RequestCall call = null;
//        if (p(paramsJson)) {
//            FormBody.Builder formBuilder = new FormBody.Builder();
//            Iterator<String> keys = paramsJson.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                try {
//                    formBuilder.add(key, String.valueOf(paramsJson.get(key)));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    log(tag, "httpDelete 解析入参异常 " + url);
//                }
//            }
//            call = builder.requestBody(formBuilder.build()).build();
//        } else {
//            call = builder.build();
//        }
//        call.execute(callback);
//        return call;
//    }
//
//    //单个限制10M，总共限制100M
//    public static RequestCall httpUpload(@NonNull String url, Map<String, String> paramsMap, Map<String, File> fileMap, String tag, @NonNull StringCallback callback) {
//        PostFormBuilder builder = OkHttpUtils.post().url(url);
//        if (p(SharePreferenceUtils.getString(getAppCtx(), "token"))) {
//            builder.addHeader("Authorization", "Bearer " + SharePreferenceUtils.getString(getAppCtx(), "token"));
//        }
//        log("httpUpload " + tag, url);
//        if (paramsMap != null) {
//            log("httpUpload", paramsMap.toString());
//        }
//        if (fileMap != null) {
//            log("httpUpload", fileMap.toString());
//        }
//        if (paramsMap != null) {
//            builder.params(paramsMap);
//        }
//        if (fileMap != null) {
//            builder.files("files", fileMap);
//        }
//        RequestCall call = builder.build();
//        call.execute(callback);
//        return call;
//    }

//    private static String getUrlWithParams(String url, JSONObject paramsJson) {
//        if (n(paramsJson)) {
//            return url;
//        }
//        if (url.contains("?")) {
//            url += "&";
//        } else {
//            url += "?";
//        }
//        StringBuilder sb = new StringBuilder(url);
//        Iterator<String> keys = paramsJson.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            try {
//                sb.append(key + "=" + String.valueOf(paramsJson.get(key)) + "&");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        return sb.toString();
//    }

}
