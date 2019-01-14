package com.example.administrator.baofen.base;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class OrangeWebViewClient extends WebViewClient {

    private Activity activity;
    private WebView webView;
    private IntentUtils intentUtils;
    public void setContent(Activity activity, WebView webView){
        this.activity = activity;
        this.webView = webView;
        this.intentUtils = new IntentUtils(activity);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        if (mAdBlock.isAd(url)) {
//            ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
//            return new WebResourceResponse("text/plain", "utf-8", EMPTY);
//        }
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("WebViewUrl",url);
        if (url.startsWith("about:")) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        if (url.contains("mailto:")) {
            MailTo mailTo = MailTo.parse(url);
            Intent i = Utils.newEmailIntent(activity, mailTo.getTo(), mailTo.getSubject(),
                    mailTo.getBody(), mailTo.getCc());
            activity.startActivity(i);
            view.reload();
            return true;
        } else if (url.startsWith("intent://")) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException ex) {
                return false;
            }
            if (intent != null) {
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("Exception",e.getMessage());
                }
                return true;
            }
        }

        boolean ret= intentUtils.startActivityForUrl(webView, url);
        if(!ret){
				/*
				mWebView.onPause();
				mWebView.pauseTimers();
				if(lastWebView!=null) {
					lastWebView.destroy();
				}

				lastWebView=mWebView;
				cachedWebView.add(mWebView);
				openNewView(url);
				return true;
				*/
        }

        if (url.endsWith(".mp3")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "audio/*");
            view.getContext().startActivity(intent);
            return true;
        } else if (url.endsWith(".mp4") || url.endsWith(".3gp")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");
            view.getContext().startActivity(intent);
            return true;
        }

        return ret;
//        return super.shouldOverrideUrlLoading(view, url);
    }
}
