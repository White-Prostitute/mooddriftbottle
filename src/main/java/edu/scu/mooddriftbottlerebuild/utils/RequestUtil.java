package edu.scu.mooddriftbottlerebuild.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class RequestUtil {

    public static String doGet(String url, String charset) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpGet httpGet = new HttpGet(url);
        HttpGet httpGet = new HttpGet();
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000)
                .setConnectTimeout(6000).setConnectionRequestTimeout(6000).build();
        httpGet.setConfig(requestConfig);
        httpGet.setURI(URI.create(url));
//        httpGet.setHeader("Accept", DEFAULT_HEADER_ACCEPT);
//        httpGet.setHeader("Content-Type", DEFAULT_GET_HEADER_CONTENT_TYPE);
//        httpGet.setHeader("User-Agent", DEFAULT_HEADER_USER_AGENT);
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String result = null;
        if(entity != null){
            result = EntityUtils.toString(entity,charset);
        }
        httpGet.abort();
        return result;
    }

}
