#import "SipguyCompassPlugin.h"
#import <sipguy_compass/sipguy_compass-Swift.h>

@implementation SipguyCompassPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterCompassPlugin registerWithRegistrar:registrar];
}
@end
