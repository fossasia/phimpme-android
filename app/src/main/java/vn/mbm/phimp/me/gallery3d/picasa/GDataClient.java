/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.picasa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.text.TextUtils;
import android.util.Log;

public final class GDataClient {
    private static final String TAG = "GDataClient";
    private static final String USER_AGENT = "Cooliris-GData/1.0; gzip";
    private static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
    private static final String IF_MATCH = "If-Match";
    private static final int CONNECTION_TIMEOUT = 20000; // ms.
    private static final int MIN_GZIP_SIZE = 512;
    public static final HttpParams HTTP_PARAMS;
    public static final ThreadSafeClientConnManager HTTP_CONNECTION_MANAGER;

    private final DefaultHttpClient mHttpClient = new DefaultHttpClient(HTTP_CONNECTION_MANAGER, HTTP_PARAMS);
    private String mAuthToken;

    static {
        // Prepare HTTP parameters.
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
        HttpClientParams.setRedirecting(params, true);
        HttpProtocolParams.setUserAgent(params, USER_AGENT);
        HTTP_PARAMS = params;

        // Register HTTP protocol.
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // Create the connection manager.
        HTTP_CONNECTION_MANAGER = new ThreadSafeClientConnManager(params, schemeRegistry);
    }

    public static final class Operation {
        public String inOutEtag;
        public int outStatus;
        public InputStream outBody;
    }

    public void setAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    public void get(String feedUrl, Operation operation) throws IOException {
        callMethod(new HttpGet(feedUrl), operation);
    }

    public void post(String feedUrl, byte[] data, String contentType, Operation operation) throws IOException {
        ByteArrayEntity entity = getCompressedEntity(data);
        entity.setContentType(contentType);
        HttpPost post = new HttpPost(feedUrl);
        post.setEntity(entity);
        callMethod(post, operation);
    }

    public void put(String feedUrl, byte[] data, String contentType, Operation operation) throws IOException {
        ByteArrayEntity entity = getCompressedEntity(data);
        entity.setContentType(contentType);
        HttpPost post = new HttpPost(feedUrl);
        post.setHeader(X_HTTP_METHOD_OVERRIDE, "PUT");
        post.setEntity(entity);
        callMethod(post, operation);
    }

    public void putStream(String feedUrl, InputStream stream, String contentType, Operation operation) throws IOException {
        InputStreamEntity entity = new InputStreamEntity(stream, -1);
        entity.setContentType(contentType);
        HttpPost post = new HttpPost(feedUrl);
        post.setHeader(X_HTTP_METHOD_OVERRIDE, "PUT");
        post.setEntity(entity);
        callMethod(post, operation);
    }

    public void delete(String feedUrl, Operation operation) throws IOException {
        HttpPost post = new HttpPost(feedUrl);
        String etag = operation.inOutEtag;
        post.setHeader(X_HTTP_METHOD_OVERRIDE, "DELETE");
        post.setHeader(IF_MATCH, etag != null ? etag : "*");
        callMethod(post, operation);
    }

    private void callMethod(HttpUriRequest request, Operation operation) throws IOException {
        // Specify GData protocol version 2.0.
        request.addHeader("GData-Version", "2");

        // Indicate support for gzip-compressed responses.
        request.addHeader("Accept-Encoding", "gzip");

        // Specify authorization token if provided.
        String authToken = mAuthToken;
        if (!TextUtils.isEmpty(authToken)) {
            request.addHeader("Authorization", "GoogleLogin auth=" + authToken);
        }

        // Specify the ETag of a prior response, if available.
        String etag = operation.inOutEtag;
        if (etag != null) {
            request.addHeader("If-None-Match", etag);
        }

        // Execute the HTTP request.
        HttpResponse httpResponse = null;
        try {
            httpResponse = mHttpClient.execute(request);
        } catch (IOException e) {
            Log.w(TAG, "Request failed: " + request.getURI());
            throw e;
        }

        // Get the status code and response body.
        int status = httpResponse.getStatusLine().getStatusCode();
        InputStream stream = null;
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            // Wrap the entity input stream in a GZIP decoder if necessary.
            stream = entity.getContent();
            if (stream != null) {
                Header header = entity.getContentEncoding();
                if (header != null) {
                    if (header.getValue().contains("gzip")) {
                        stream = new GZIPInputStream(stream);
                    }
                }
            }
        }

        // Return the stream if successful.
        Header etagHeader = httpResponse.getFirstHeader("ETag");
        operation.outStatus = status;
        operation.inOutEtag = etagHeader != null ? etagHeader.getValue() : null;
        operation.outBody = stream;
    }

    private ByteArrayEntity getCompressedEntity(byte[] data) throws IOException {
        ByteArrayEntity entity;
        if (data.length >= MIN_GZIP_SIZE) {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(data.length / 2);
            GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput);
            gzipOutput.write(data);
            gzipOutput.close();
            entity = new ByteArrayEntity(byteOutput.toByteArray());
        } else {
            entity = new ByteArrayEntity(data);
        }
        return entity;
    }

    public static String inputStreamToString(InputStream stream) {
        if (stream != null) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    bytes.write(buffer, 0, bytesRead);
                }
                return new String(bytes.toString());
            } catch (IOException e) {
                // Fall through and return null.
            }
        }
        return null;
    }
}
