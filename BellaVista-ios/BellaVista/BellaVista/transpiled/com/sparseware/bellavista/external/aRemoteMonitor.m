//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/aRemoteMonitor.java
//
//  Created by decoteaud on 3/12/15.
//

#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/ui/UIDimension.h"
#include "com/appnativa/rare/viewer/iContainer.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/external/aRemoteMonitor.h"

@implementation ComSparsewareBellavistaExternalaRemoteMonitor

- (id)init {
  return [super init];
}

- (BOOL)canMonitorPatientWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)createViewerWithRAREUTJSONObject:(RAREUTJSONObject *)patient
                      withRAREiContainer:(id<RAREiContainer>)parent
                     withRAREUIDimension:(RAREUIDimension *)targetSize
               withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)dispose {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)pauseMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)startMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

- (void)stopMonitoringWithRAREUTJSONObject:(RAREUTJSONObject *)patient {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x4, NULL },
    { "canMonitorPatientWithRAREUTJSONObject:", NULL, "Z", 0x401, NULL },
    { "createViewerWithRAREUTJSONObject:withRAREiContainer:withRAREUIDimension:withRAREiFunctionCallback:", NULL, "V", 0x401, NULL },
    { "dispose", NULL, "V", 0x401, NULL },
    { "pauseMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "startMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
    { "stopMonitoringWithRAREUTJSONObject:", NULL, "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _ComSparsewareBellavistaExternalaRemoteMonitor = { "aRemoteMonitor", "com.sparseware.bellavista.external", NULL, 0x401, 7, methods, 0, NULL, 0, NULL};
  return &_ComSparsewareBellavistaExternalaRemoteMonitor;
}

@end
