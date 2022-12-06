#import "TuyaPlugin.h"
#if __has_include(<tuya/tuya-Swift.h>)
#import <tuya/tuya-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "tuya-Swift.h"
#endif

@implementation TuyaPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTuyaPlugin registerWithRegistrar:registrar];
}
@end
