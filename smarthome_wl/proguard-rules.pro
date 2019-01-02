# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidStudio\sdk/tools/proguard/proguard-android.txt
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
-optimizationpasses 5        # 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验，Android不需要preverify，去掉这一步可以加快混淆速度
-verbose               # 混淆时是否记录日志
-dontoptimize  #不优化
-dontshrink #不压缩输入的类文件
-printmapping proguardMapping.txt#-verbose开启，混淆时记录日志映射输出的文件名称
-optimizations !code/simplification/cast,!field/*,!class/merging/*  # 混淆时所采用的算法，后面的参数是一个过滤器，这个过滤器是谷歌推荐的算法，一般不改变
-keepattributes *Annotation*,InnerClasses# 这在JSON实体映射【注解】时非常重要，比如Gosn
-keepattributes Signature# 避免混淆泛型
-keepattributes SourceFile,LineNumberTable# 抛出异常时保留代码行号
-flattenpackagehierarchy#保持packagename 不混淆
-allowaccessmodification#如果一个方法有返回值，在调用的时候没使用到它的返回值，那么可能被忽略。
-dontskipnonpubliclibraryclasses# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclassmembers# 指定不去忽略非公共的库的类的成员
-ignorewarnings#不打印警告信息

#---------------------------------默认保留区---------------------------------
#不混淆需要根据manifest来识别的类
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

#异常不进行混淆
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}


# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

## gson
-dontwarn com.google.gson.**
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.JsonObject { *; }
-keep class com.fbee.smarthome_wl.bean.** { *; }
-keepattributes Signature
# 保留Serializable 序列化的类不被混淆
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

# gson 解析实体类
-keep class com.fbee.smarthome_wl.response.**{*;}
-keep class com.fbee.smarthome_wl.request.**{*;}

## Jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

#glide 支持https
-keep class com.fbee.smarthome_wl.glide.OkHttpGlideModule

## icvss
-keep class  com.eques.icvss.** {*;}
-dontwarn com.eques.icvss.**

-keep class com.fbee.zllctl.** { *; }
-keep class com.tutk.IOTC.** { *; }

## anychat
-keep class com.bairuitech.anychat.**{*;}


## AES256
-keep class org.bouncycastle.**{*;}

# # -------------------------------------------
# #  ######## greenDao混淆  ##########
# # -------------------------------------------
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static Java.lang.String TABLENAME;
}
-keep class com.fbee.smarthome_wl.greendao.**{*;}
-keep class **$Properties

-keepattributes SourceFile,LineNumberTable
##混淆前后的映射
-printmapping mapping.txt

-dontwarn com.ezviz.player.**
-keep class com.ezviz.player.** { *;}

-dontwarn com.ezviz.statistics.**
-keep class com.ezviz.statistics.** { *;}

-dontwarn com.ezviz.stream.**
-keep class com.ezviz.stream.** { *;}

-dontwarn com.ezviz.hcnetsdk.**
-keep class com.ezviz.hcnetsdk.** { *;}


-dontwarn com.ezviz.opensdk.**
-keep class com.ezviz.opensdk.** { *;}

-dontwarn com.hik.**
-keep class com.hik.** { *;}

-dontwarn com.hikvision.**
-keep class com.hikvision.** { *;}

-dontwarn com.videogo.**
-keep class com.videogo.** { *;}

-dontwarn org.MediaPlayer.PlayM4.**
-keep class org.MediaPlayer.PlayM4.** { *;}

-dontwarn com.sun.jna.**
-keep class com.sun.jna.**{*;}

#Gson混淆配置
-keepattributes Annotation
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }

#引用mars的xlog，混淆配置
-keep class com.tencent.mars.** {
 public protected private *;
}