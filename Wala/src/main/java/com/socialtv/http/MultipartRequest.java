package com.socialtv.http;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.GsonUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.socialtv.util.IConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;

public class MultipartRequest<T> extends BaseRequest<T> {

	private MultipartEntity entity = new MultipartEntity();
//	private final Gson gson;
//	private Class<T> clazz;

	public MultipartRequest(String url) {
		super(Method.POST, IConstant.DOMAIN + url, null);
//		gson = GsonUtils.createGson();
		setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

//    public void setClazz(Class<T> clazz) {
//        this.clazz = clazz;
//    }

	public MultipartRequest<T> addMultipartFileEntity(String key, File mFilePart) {
		entity.addPart(key, new FileBody(mFilePart));
		return this;
	}

	public MultipartRequest<T> addMultipartStringEntity(String key, String mStringPart) {
		try {
			entity.addPart(key, new StringBody(mStringPart, Charset.forName("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			VolleyLog.e("UnsupportedEncodingException");
		}
		return this;
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

//	@Override
//	protected Response<T> parseNetworkResponse(NetworkResponse response) {
//		try {
//			String json = new String(response.data,
//					HttpHeaderParser.parseCharset(response.headers));
//			return Response.success(gson.fromJson(json, clazz),
//					HttpHeaderParser.parseCacheHeaders(response));
//		} catch (UnsupportedEncodingException e) {
//			return Response.error(new ParseError(e));
//		} catch (JsonSyntaxException e) {
//			return Response.error(new ParseError(e));
//		}
//	}
//
//	@Override
//	protected void deliverResponse(T response) {
////		mListener.onResponse(response);
//	}
}