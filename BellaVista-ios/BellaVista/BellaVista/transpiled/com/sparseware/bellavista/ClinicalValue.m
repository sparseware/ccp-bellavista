//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ClinicalValue.java
//
//  Created by decoteaud on 3/14/16.
//

#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/iPlatformAppContext.h"
#include "com/sparseware/bellavista/ClinicalValue.h"
#include "java/util/Date.h"

@implementation CCPBVClinicalValue

- (id)initWithNSString:(NSString *)id_
          withNSString:(NSString *)name {
  if (self = [super init]) {
    self->id__ = id_;
    self->name_ = name;
  }
  return self;
}

- (id)initWithNSString:(NSString *)id_
          withNSString:(NSString *)name
      withJavaUtilDate:(JavaUtilDate *)date
          withNSString:(NSString *)value {
  if (self = [super init]) {
    self->id__ = id_;
    self->name_ = name;
    self->date_ = date;
    self->value_ = value;
  }
  return self;
}

- (JavaUtilDate *)getDate {
  return date_;
}

- (NSString *)getID {
  return id__;
}

- (NSString *)getName {
  return name_;
}

- (NSString *)getValue {
  return value_;
}

- (void)updateWithJavaUtilDate:(JavaUtilDate *)date
                  withNSString:(NSString *)value {
  if ((self->date_ == nil) || [((JavaUtilDate *) nil_chk(date)) afterWithJavaUtilDate:self->date_] || [date isEqual:self->date_]) {
    self->value_ = value;
    self->date_ = date;
    if (attributName_ != nil) {
      (void) [((id<RAREiPlatformAppContext>) nil_chk([RAREPlatform getAppContext])) putDataWithId:attributName_ withId:value];
    }
  }
}

- (NSString *)getAttributName {
  return attributName_;
}

- (void)setAttributNameWithNSString:(NSString *)attributName {
  self->attributName_ = attributName;
}

- (void)copyAllFieldsTo:(CCPBVClinicalValue *)other {
  [super copyAllFieldsTo:other];
  other->attributName_ = attributName_;
  other->date_ = date_;
  other->id__ = id__;
  other->name_ = name_;
  other->value_ = value_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "getDate", NULL, "LJavaUtilDate", 0x1, NULL },
    { "getID", NULL, "LNSString", 0x1, NULL },
    { "getName", NULL, "LNSString", 0x1, NULL },
    { "getValue", NULL, "LNSString", 0x1, NULL },
    { "getAttributName", NULL, "LNSString", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "id__", "id", 0x4, "LNSString" },
    { "name_", NULL, 0x4, "LNSString" },
    { "date_", NULL, 0x4, "LJavaUtilDate" },
    { "value_", NULL, 0x4, "LNSString" },
  };
  static J2ObjcClassInfo _CCPBVClinicalValue = { "ClinicalValue", "com.sparseware.bellavista", NULL, 0x1, 5, methods, 4, fields, 0, NULL};
  return &_CCPBVClinicalValue;
}

@end
