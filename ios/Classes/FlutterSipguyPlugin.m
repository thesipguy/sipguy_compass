#import "SipguyCompassPlugin.h"
#import <sipguy_compass/sipguy_compass-Swift.h>

@implementation SipguyCompassPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSipguyCompassPlugin registerWithRegistrar:registrar];
}
@end
