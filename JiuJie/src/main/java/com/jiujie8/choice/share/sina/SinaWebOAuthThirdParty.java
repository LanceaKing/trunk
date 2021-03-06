package com.jiujie8.choice.share.sina;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiujie8.choice.core.CacheRepository;
import com.jiujie8.choice.share.AuthorizeListener;
import com.jiujie8.choice.share.OAuthThirdParty;
import com.jiujie8.choice.share.OAuthToken;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;

/**
 * Created by sunjian on 13-12-26.
 */
public class SinaWebOAuthThirdParty implements OAuthThirdParty, IWeiboHandler.Response {

    protected static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    protected IWeiboShareAPI mWeiboShareAPI;
    protected String appKey;
    protected String appSecret;
    protected String redirectUrl;
    protected WeiboAuth weiboAuth;
    protected Activity context;
    protected AuthorizeListener authorizeListener;


    public SinaWebOAuthThirdParty(IWeiboShareAPI mWeiboShareAPI, Activity context, String appKey, String appSecret, String redirectUrl) {
        this.mWeiboShareAPI = mWeiboShareAPI;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.redirectUrl = redirectUrl;
        this.context = context;
        weiboAuth = new WeiboAuth(context, appKey, redirectUrl, SCOPE);
    }

    @Override
    public void authorize(AuthorizeListener authorizeListener) {
        this.authorizeListener = authorizeListener;
        weiboAuth.anthorize(new WebAuthListener());
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    Toast.makeText(context, "取消新浪客户端下载", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (bundle != null) {
            mWeiboShareAPI.handleWeiboResponse(context.getIntent(), this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //don't happen here, do nothing
    }

    @Override
    public void shareText(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        sendMessage(textObject, null, null, null, null, null);
    }

    @Override
    public void sharePic(ImageView imageView, String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        ImageObject imageObject = new ImageObject();
        Drawable mDrawable = imageView.getDrawable();
        if(mDrawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mDrawable;
            if(bitmapDrawable != null) {
                if(bitmapDrawable.getBitmap() != null) {
                    imageObject.setImageObject(bitmapDrawable.getBitmap());
                    sendMessage(textObject, imageObject, null, null, null, null);
                }
            }
        }
    }

    @Override
    public void sharePic(Bitmap bitmap, String text) {
        if (text != null) {
            TextObject textObject = new TextObject();
            textObject.text = text;
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
            sendMessage(textObject, imageObject, null, null, null, null);
        } else {
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
            sendMessage(null, imageObject, null, null, null, null);
        }
    }

    @Override
    public void sharePage(String text, ImageView imageView, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {
        TextObject textObject = null;
        if(text!=null){
            textObject = new TextObject();
            textObject.text = text;
        }

        ImageObject imageObject = null;
        if(imageView != null){
            imageObject = new ImageObject();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            imageObject.setImageObject(bitmapDrawable.getBitmap());
        }

        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = pageTitle;
        mediaObject.description = pageDesc;

        mediaObject.setThumbImage(pageThumbImage);
        mediaObject.actionUrl = pageLink;
        mediaObject.defaultText = defaultText;
        sendMessage(textObject, imageObject, mediaObject, null, null, null);
    }

    @Override
    public void sharePage(String text, Bitmap bitmap, String pageTitle, String pageDesc, Bitmap pageThumbImage, String pageLink, String defaultText) {

        TextObject textObject = null;
        if(text!=null){
            textObject = new TextObject();
            textObject.text = text;
        }

        ImageObject imageObject = null;
        if(bitmap != null){
            imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
        }

        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = pageTitle;
        mediaObject.description = pageDesc;

        mediaObject.setThumbImage(pageThumbImage);
        mediaObject.actionUrl = pageLink;
        mediaObject.defaultText = defaultText;
        sendMessage(textObject, imageObject, mediaObject, null, null, null);
    }

    private void sendMessage(TextObject textObject, ImageObject imageObject, WebpageObject webpageObject, MusicObject musicObject, VideoObject videoObject, VoiceObject voiceObject) {
        try {
            if (mWeiboShareAPI.checkEnvironment(true)) {
                mWeiboShareAPI.registerApp();
                if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                    int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                    if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                        sendMultiMessage(textObject, imageObject, webpageObject, musicObject, videoObject, voiceObject);
                    } else {
                        sendSingleMessage(textObject, imageObject, webpageObject, musicObject, videoObject);
                    }
                } else {
                    Toast.makeText(context, "新浪API版本过低,无法分享", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (WeiboShareException e) {
            Toast.makeText(context, "新浪分享失败:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage(TextObject textObject, ImageObject imageObject, WebpageObject webpageObject, MusicObject musicObject, VideoObject videoObject, VoiceObject voiceObject) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (textObject != null) {
            weiboMessage.textObject = textObject;
        }

        if (imageObject != null) {
            weiboMessage.imageObject = imageObject;
        }

        if (webpageObject != null) {
            weiboMessage.mediaObject = webpageObject;
        }
        if (musicObject != null) {
            weiboMessage.mediaObject = musicObject;
        }
        if (videoObject != null) {
            weiboMessage.mediaObject = videoObject;
        }
        if (voiceObject != null) {
            weiboMessage.mediaObject = voiceObject;
        }

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    private void sendSingleMessage(TextObject textObject, ImageObject imageObject, WebpageObject webpageObject, MusicObject musicObject, VideoObject videoObject) {

        WeiboMessage weiboMessage = new WeiboMessage();
        if (textObject != null) {
            weiboMessage.mediaObject = textObject;
        }

        if (imageObject != null) {
            weiboMessage.mediaObject = imageObject;
        }

        if (webpageObject != null) {
            weiboMessage.mediaObject = webpageObject;
        }
        if (musicObject != null) {
            weiboMessage.mediaObject = musicObject;
        }
        if (videoObject != null) {
            weiboMessage.mediaObject = videoObject;
        }

        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        mWeiboShareAPI.sendRequest(request);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(context, "新浪分享成功", Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(context, "取消新浪分享", Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(context, "新浪分享失败:" + baseResponse.errMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    class WebAuthListener implements WeiboAuthListener {

        protected Oauth2AccessToken token;
        protected Throwable exception;

        @Override
        public void onComplete(Bundle values) {
            if (null == values) {
                return;
            }
            token =  Oauth2AccessToken.parseAccessToken(values);
            save(token);
            callback(token);
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, "取消了新浪登录", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(context, "新浪登录失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        protected void callback(Oauth2AccessToken token) {
            authorizeListener.onComplete(new OAuthToken(token.getToken(), token.getUid(), token.getExpiresTime() / 1000L));
        }

        protected void save(Oauth2AccessToken token) {
            CacheRepository.getInstance().fromContext(context).putString(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN, token.getToken()).putLong(OAuthThirdParty.SINA, OAuthThirdParty.EXPIRED_TIME, token.getExpiresTime()).putString(OAuthThirdParty.SINA, OAuthThirdParty.UID, token.getUid());
        }
    }
}
