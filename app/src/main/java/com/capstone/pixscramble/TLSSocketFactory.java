package com.capstone.pixscramble;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author santosh
 */
public class TLSSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory internalSSLSocketFactory;

    private static final TrustManager[] INSECURE_TRUST_MANAGER = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
    };



    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, INSECURE_TRUST_MANAGER, null);
        internalSSLSocketFactory = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return internalSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return internalSSLSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        Socket soc = internalSSLSocketFactory.createSocket(s, host, port, autoClose);
        return enableTLSOnSocket(soc);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket soc = internalSSLSocketFactory.createSocket(host, port);
       return enableTLSOnSocket(soc);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        Socket soc = internalSSLSocketFactory.createSocket(host, port, localHost, localPort);
        return enableTLSOnSocket(soc);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket soc = internalSSLSocketFactory.createSocket(host, port);
        return enableTLSOnSocket(soc);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket soc = internalSSLSocketFactory.createSocket(address, port, localAddress, localPort);
        return enableTLSOnSocket(soc);
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            try {
                Log.v("SSLSocket","SSLSocket");
                ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
            }catch(Exception e)
            {e.printStackTrace();}
        }

        return socket;
    }
}