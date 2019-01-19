# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lody/Desktop/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ��������ʹ�õ��Ĵ�������Զ����Application�ȵ���Щ�಻������
# ��Ϊ��Щ���඼�п��ܱ��ⲿ����
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService


# ����support�µ������༰���ڲ���
-keep class android.support.** {*;}

# �����̳е�
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# ����R�������Դ
-keep class **.R$* {*;}

# ��������native������������
-keepclasseswithmembernames class * {
    native <methods>;
}

# ������Activity�еķ���������view�ķ�����
# ��������������layout��д��onClick�Ͳ��ᱻӰ��
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# ����ö���಻������
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ���������Զ���ؼ����̳���View����������
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ����Parcelable���л��಻������
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ����Serializable���л����಻������
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ���ڴ��лص�������onXXEvent��**On*Listener�ģ����ܱ�����
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView��������Ŀ��û��ʹ�õ�webView���Լ���
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
-dontwarn android.**
-keep class android.** {*;}

-dontwarn com.nibiru.**
-keep class com.nibiru.** {*;}

-dontwarn com.google.**
-keep class com.google.** {*;}

-dontwarn de.robv.**
-keep class de.robv.** {*;}

-dontwarn com.android.**
-keep class com.android.** {*;}

-dontwarn com.taobao.**
-keep class com.taobao.** {*;}

-dontwarn org.slf4j.**
-keep class org.slf4j.** {*;}

-dontwarn com.hmct.**
-keep class com.hmct.** {*;}

-dontwarn com.lody.virtual.**
-keep class com.lody.virtual.** {*;}

-dontwarn mirror.**
-keep class mirror.** {*;}



-dontwarn com.alibaba.**
-keep class com.alibaba.** {*;}

-dontwarn com.app360.app360.wxapi.**
-keep class com.app360.app360.wxapi.** {*;}

-dontwarn com.example.android.ndk.**
-keep class com.example.android.ndk.** {*;}


-dontwarn okio.**
-keep class okio.** {*;}


-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
