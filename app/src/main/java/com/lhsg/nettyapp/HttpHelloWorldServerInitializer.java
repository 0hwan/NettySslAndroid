package com.lhsg.nettyapp;

import android.util.Log;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * Created by lhsg on 2016. 3. 4..
 */
public class HttpHelloWorldServerInitializer extends ChannelInitializer<SocketChannel> {
    private final static String TAG = HttpHelloWorldServerInitializer.class.getSimpleName();
    private  final SslContext mSslContext;

    public HttpHelloWorldServerInitializer(SslContext sslContext) {
        this.mSslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        Log.d(TAG, "initChannel");
        ChannelPipeline p = ch.pipeline();

        if( this.mSslContext != null ){
            SSLEngine sslEngine = mSslContext.newEngine(ch.alloc());
            sslEngine.setUseClientMode(false);
//            sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
//            sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
            sslEngine.setEnableSessionCreation(true);
            p.addLast("ssl", new SslHandler(sslEngine));
        }

        p.addLast(new HttpServerCodec());
        p.addLast(new HttpHelloWorldServerHandler());
    }
}
