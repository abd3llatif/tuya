package ma.goviral.tuya;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.google.gson.Gson;
import com.tuya.smart.android.ble.builder.BleConnectBuilder;
import com.tuya.smart.android.blemesh.api.ITuyaBlueMeshDevice;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/** TuyaPlugin */
public class TuyaPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "goviral.ma/Tuya");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      TuyaHomeSdk.init(flutterPluginBinding.getApplicationContext());
      result.notImplemented();
    }

    if (call.method.equals("sendVerificationCode")) {
      // Get verification code code by phone or Email
      try {
        TuyaHomeSdk.getUserInstance().sendVerifyCodeWithUserName(
                call.argument("email"), // String
                "",
                call.argument("countryCode"), // String
                1,
                new IResultCallback() {
                  @Override
                  public void onError(String code, String error) {
//                                  Toast.makeText(
//                                          getApplication(),
//                                          "Sending validation code error:" + error + " code : " + code,
//                                          Toast.LENGTH_LONG
//                                  ).show();

                    result.error(code, error, "Sending validation code error:" + error + " code : " + code);
                  }

                  @Override
                  public void onSuccess() {
//                                  Toast.makeText(
//                                          getApplication(),
//                                          "Got validation code",
//                                          Toast.LENGTH_SHORT
//                                  ).show();
                    result.success(true);
                  }
                });
      } catch (Exception ex) {
        result.error("Exception", ex.getMessage(), null);
      }

    }

    if (call.method.equals("registerWithEmail")) {
      IRegisterCallback callback = new IRegisterCallback() {
        @Override
        public void onSuccess(User user) {
          Gson gson = new Gson();
          result.success(gson.toJson(user));
        }

        @Override
        public void onError(String code, String error) {
          result.error(code, error, null);
        }
      };

      TuyaHomeSdk.getUserInstance().registerAccountWithEmail(
              call.argument("countryCode"),
              call.argument("email"),
              call.argument("password"),
              call.argument("verificationCode"),
              callback
      );
    }

    if (call.method.equals("loginWithEmail")) {
      ILoginCallback callback = new ILoginCallback() {
        @Override
        public void onSuccess(User user) {
          Gson gson = new Gson();
          result.success(gson.toJson(user));
        }

        @Override
        public void onError(String code, String error) {
          result.error(code, error, null);
        }
      };

      TuyaHomeSdk.getUserInstance()
              .loginWithEmail(call.argument("countryCode"),
                      call.argument("email"),
                      call.argument("password"),
                      callback
              );
    }

    if (call.method.equals("createNewHome")) {
      TuyaHomeSdk.getHomeManagerInstance().createHome(
              call.argument("name"),
              // Get location by yourself, here just sample as Shanghai's location
              120.52,
              30.40,
              "Shanghai",
              new ArrayList<>(),

              new ITuyaHomeResultCallback() {
                @Override
                public void onSuccess(HomeBean bean) {
                  // SWITCH AND SELECT AS CURRENT HOME
                  TuyaHomeSdk.newHomeInstance(bean.getHomeId());
                  Gson gson = new Gson();
                  result.success(gson.toJson(bean));
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                  result.error(errorCode, errorMsg, null);
                }
              }
      );
    }


    if (call.method.equals("connectEZMode")) {
      // Get Network Configuration Token
      TuyaHomeSdk.getActivatorInstance().getActivatorToken(Long.parseLong(Objects.requireNonNull(call.argument("homeId"))),
              new ITuyaActivatorGetToken() {
                @Override
                public void onSuccess(String token) {
                  // Start network configuration -- EZ mode
                  ActivatorBuilder builder = new ActivatorBuilder()
                          .setSsid(call.argument("ssid"))
                          .setContext(flutterPluginBinding.getApplicationContext())
                          .setPassword(call.argument("password"))
                          .setActivatorModel(ActivatorModelEnum.TY_EZ)
                          .setTimeOut(100)
                          .setToken(token)
                          .setListener(new ITuyaSmartActivatorListener() {
                            @Override
                            public void onError(String errorCode, String errorMsg) {
                              result.error(errorCode, errorMsg, null);
                            }

                            @Override
                            public void onActiveSuccess(DeviceBean devResp) {
                              Gson gson = new Gson();
                              result.success(gson.toJson(devResp));
                            }

                            @Override
                            public void onStep(String step, Object data) {
//                              Toast.makeText(getApplication(),
//                                      step + " --> " + data,
//                                      Toast.LENGTH_LONG
//                              ).show();
                            }
                          });

                  ITuyaActivator mTuyaActivator =
                          TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder);

                  //Start configuration
                  mTuyaActivator.start();

                  //Stop configuration
//                                mTuyaActivator.stop()
                  //Exit the page to destroy some cache data and monitoring data.
//                                mTuyaActivator.onDestroy()
                }

                @Override
                public void onFailure(String errorCode, String errorMsg) {
                  result.error(errorCode, errorMsg, null);
                }
              });
    }

    if (call.method.equals("getDeviceList")) {
      /**
       * The device control must first initialize the data,
       * and call the following method to get the device information in the home.
       * initialization only need when the begin of app lifecycle and switch home.
       */

      IDevListener iDevListener = new IDevListener() {
        @Override
        public void onDpUpdate(String devId, String dpStr) {
          HashMap<String, String> map = new HashMap<>();
          map.put("devId", devId);
          map.put("dpStr", dpStr);

          Gson gson = new Gson();
          channel.invokeMethod("onDpUpdate", gson.toJson(map));
        }

        @Override
        public void onRemoved(String devId) {

          HashMap<String, String> map = new HashMap<>();
          map.put("devId", devId);

          Gson gson = new Gson();
          channel.invokeMethod("onRemoved", gson.toJson(map));
        }

        @Override
        public void onStatusChanged(String devId, boolean online) {

          HashMap<String, Object> map = new HashMap<>();
          map.put("devId", devId);
          map.put("online", online);

          Gson gson = new Gson();
          channel.invokeMethod("onStatusChanged", gson.toJson(map));
        }

        @Override
        public void onNetworkStatusChanged(String devId, boolean status) {

          HashMap<String, Object> map = new HashMap<>();
          map.put("devId", devId);
          map.put("status", status);

          Gson gson = new Gson();
          channel.invokeMethod("onNetworkStatusChanged", gson.toJson(map));

        }

        @Override
        public void onDevInfoUpdate(String devId) {

          HashMap<String, String> map = new HashMap<>();
          map.put("devId", devId);

          Gson gson = new Gson();
          channel.invokeMethod("onDevInfoUpdate", gson.toJson(map));
        }
      };

      TuyaHomeSdk.newHomeInstance(Long.parseLong(Objects.requireNonNull(call.argument("homeId")))).getHomeDetail(new ITuyaHomeResultCallback() {
        @Override
        public void onSuccess(HomeBean homeBean) {

          ArrayList<DeviceBean> deviceList = (ArrayList<DeviceBean>) homeBean.getDeviceList();
          if (deviceList != null && deviceList.size() > 0) {
            for (DeviceBean deviceBean : deviceList) {
              TuyaHomeSdk.newDeviceInstance(deviceBean.devId).registerDevListener(iDevListener);
            }
          }

          Gson gson = new Gson();
          result.success(gson.toJson(deviceList));

        }

        @Override
        public void onError(String errorCode, String errorMsg) {

//          Toast.makeText(getApplication(),
//                  "Activate error-->" + errorMsg,
//                  Toast.LENGTH_LONG
//          ).show();
          result.error(errorCode, errorMsg, null);
        }
      });
    }

    if(call.method.equals("publishDps")) {
      TuyaHomeSdk.newDeviceInstance(call.argument("devId")).publishDps(call.argument("cmd"), new IResultCallback() {
        @Override
        public void onError(String code, String error) {
          result.error(code, error, null);
        }

        @Override
        public void onSuccess() {
          result.success(true);
        }
      });
    }

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
