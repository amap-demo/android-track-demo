# AMapTrackDemo
HT 轨迹跟踪sdk的demo

## 前述 ##
- [高德官网申请Key](http://lbs.amap.com/dev/#/).
- 阅读[参考手册](http://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html).
- 工程基于高德地图Android地图SDK实现

## 配置搭建AndroidSDK工程 ##
- [Android Studio工程搭建方法](http://lbs.amap.com/api/android-sdk/guide/creat-project/android-studio-creat-project/#add-jars).
- [通过maven库引入SDK方法](http://lbsbbs.amap.com/forum.php?mod=viewthread&tid=18786).

## 工程配置方法 ##
配置工程时，如果需要轨迹跟踪sdk对服务进行保活，需要额外增加配置。具体见下。
### 基本配置 ###
### AndroidManifest配置 ###
```java
<application
        android:name=".App" <!-- 如果需要保活，则.App必须继承AMapTrackAPP -->
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name">

        <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="您的key" <!-- AMap的key -->
            />

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <!-- 定位服务 -->
        <service
            android:name="com.amap.api.location.APSService"
            android:process=":process1"/>

        <!-- 轨迹跟踪服务 -->
        <service
            android:name="com.amap.locmonitorsdk.AMapTrackService"
            android:process=":process1">
            <intent-filter>
                <action android:name="com.amap.amlib.DestService"/>
            </intent-filter>
        </service>
  
        <!-- 保活相关声明, 如果不需要sdk进行保活，则此处的配置可忽略 -->
        <!-- 轨迹跟踪服务服务和receiver -->
        <receiver
            android:name="com.amap.locmonitorsdk.daemons.AMapTrackReceiver"
            android:process=":process1"/>
      
        <service
            android:name="com.amap.locmonitorsdk.daemons.AMapTrackService2"
            android:process=":process2"/>

        <receiver
            android:name="com.amap.locmonitorsdk.daemons.AMapTrackReceiver2"
            android:process=":process2"/>


        <!-- AccountManager Declaration -->
        <provider
            android:name="com.amap.amlib.AMContentProvider"
            android:authorities="@string/account_auth_provider"
            android:exported="false"
            android:process=":process1"
            android:syncable="true"/>

        <service
            android:name="com.amap.amlib.AMSyncService"
            android:process=":process1"
            android:exported="true">
            <intent-filter>
                <action
                    android:name="android.content.SyncAdapter"/>
            </intent-filter>
            <meta-data
                android:name="android.content.SyncAdapter"
                android:resource="@xml/sync_adapter"/>
        </service>

        <service
            android:name="com.amap.amlib.AMAuthService"
            android:process=":process1"
            android:exported="true">
            <intent-filter>
                <action
                    android:name="android.accounts.AccountAuthenticator"/>
            </intent-filter>
            <meta-data
                android:name="android.accounts.AccountAuthenticator"
                android:resource="@xml/authenticator"/>
        </service>
        <!-- end of AccountManager Declaration-->
        <!-- end of 保活相关声明 -->
    </application>
```

### jar包和so包依赖 ###
必须包含 libs/AMapTrack_v0.1_20170911.jar。
如果需要保活，需要引入libs/XXX/libdaemon_api20.so 和libs/XXX/libdaemon_api21.so ，同时引入assets/XXX/daemon

## 使用方法如下 ##
开启轨迹跟踪时，除了需要车牌和和电话号码等参数外，如果需要sdk对服务进行保活，需要传入相应参数。具体使用方法如下：

### 非保活的启动方法 ###
启动轨迹跟踪的代码如下：
```java
// 生成AMapTrackOption
AMapTrackOption option = new AMapTrackOption("电话号码", "车牌号");

// 生成AMapTrackManager
AMapTrackManager mTrackManager = AMapTrackManager.newInstance(option);

// 注册监听器
mTrackManager.addTrackListener(new AMapTrackListener() {
            @Override
            public void onTrackStart(int errCode , String msg){
                //定位上报开始
            }

            @Override
            public void onFail(int errCode, String msg) {
                //上报过程中的错误信息。不影响后续的定位上报
            }

            @Override
            public void onTrackFinish(int errCode, String msg) {
                //定位上报结束。一般为保单结束时，告知用户
            }
        });

//开始轨迹上报
mTrackManager.startTrack(context)

//停止轨迹上报
mTrackManager.stopTrack(context)

```

### 保活的启动方法 ###
启动时，代码如下

```java
// 生成AMapTrackOption ， 第三个参数需要设置为true。其余均相同
AMapTrackOption option = new AMapTrackOption("电话号码", "车牌号", true);
......
```
