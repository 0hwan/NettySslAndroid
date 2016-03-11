package com.lhsg.nettyapp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * Created by lhsg on 2016. 3. 11..
 */
public class NettyServer {
    private static final String TAG = NettyServer.class.getSimpleName();

    private SslContext mSslContext;

    private Channel mServerChannel;

    // A flag to control whether the web server should be kept running
    private boolean isRunning = true;

    /**
     * NettyServer constructor.
     */
    public NettyServer(Context ctx) {
        try {
            // Get an SSL context using the TLS protocol
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Get a key manager factory using the default algorithm
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            // Load the PKCS12 key chain
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = ctx.getAssets().openFd(MainActivity.PKCS12_FILENAME).createInputStream();
            ks.load(fis, MainActivity.PKCS12_PASSWORD.toCharArray());
            kmf.init(ks, MainActivity.PKCS12_PASSWORD.toCharArray());

            mSslContext = SslContextBuilder.forServer(kmf).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the base64 image string used in the server response
    }

    protected void start() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "NettyServer is starting up on port "+MainActivity.SERVER_PORT);
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    final ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new HttpHelloWorldServerInitializer(mSslContext));

                    mServerChannel = b.bind(MainActivity.SERVER_PORT).sync().channel();
                    mServerChannel.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }).start();
    }

    /**
     * This method stops the SSL web server
     */
    protected void stop() {
        Log.d(TAG, "NettyServer is stopping");
        if(mServerChannel != null) {
            mServerChannel.close();
        }
    }

}
