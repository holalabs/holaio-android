package com.holalabs.holaio;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class HolaIO {
	
	private static final String serverURL = "api.io.holalabs.com";
	protected HolaIOSingleton singleton;
	private final String apikey;
	
	private final HttpClient httpclient = getNewHttpClient();
	private final CookieStore cookieStore = new BasicCookieStore();
    private final HttpContext localContext = new BasicHttpContext();
    private ThreadPoolExecutor threadpool = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    
    // Initialize the class authorizing the developer with our server
    public HolaIO(String key) {
    	// We set the cookiestore so that when authorized, a cookies is saved to do the get requests
    	localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    	singleton = HolaIOSingleton.getInstance();
    	apikey = key;
    }
	
	/* @params
	 * URL: The url without the scheme (http or https)
	 * Content: A CSS3 selector of the content you want
	 * responseHandler: Create a new AsyncResponseHandler and Override the methods onSucceed and onFinish
	 */
	public void get(String URL, String Content, AsyncResponseHandler responseHandler) {
		get(URL, Content, true, false, responseHandler);
	}
	
	/* @params
	 * URL: The url without the scheme (http or https)
	 * Content: A CSS3 selector of the content you want
	 * Cache: Would you like your request to be cached?
	 * responseHandler: Create a new AsyncResponseHandler and Override the methods onSucceed and onFinish
	 */
	public void get(String URL, String Content, Boolean cache, AsyncResponseHandler responseHandler) {
		get(URL, Content, true, cache, responseHandler);
	}

	/* @params
	 * URL: The url without the scheme (http or https)
	 * Content: A CSS3 selector of the content you want
	 * inner: True if you want to get the content's innerHTML and False if you want outerHTML
	 * Cache: Would you like your request to be cached?
	 * responseHandler: Create a new AsyncResponseHandler and Override the methods onSucceed and onFinish
	 */
	public void get(String URL, String Content, Boolean inner, Boolean cache, AsyncResponseHandler responseHandler) {
		String webInner = (inner) ? "inner" : "outer";
		
		try {
			// It prepares the URL
			String path = "/" + URL + "/" + Content + "/" + webInner;
			URI getURI = new URI("https", serverURL, path, null);
			String getURL = getURI.toASCIIString();
			HttpGet httpget = new HttpGet(getURL);
			httpget.setHeader("X-Api-Key", apikey);
			httpget.setHeader("X-Api-Version", "1.0.0");
			threadpool.submit(new AsyncHttpRequest(httpclient, localContext, httpget, cache, getURL, responseHandler));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	// Created a new SSLSocketFactory that trusts websites using SSL even though they don't have a certificate
	public class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}

	// Our own HttpClient that uses the SSLSocketFactory above and modifies a few params
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
}