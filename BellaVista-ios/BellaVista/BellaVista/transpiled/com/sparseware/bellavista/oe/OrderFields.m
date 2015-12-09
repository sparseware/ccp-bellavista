//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/oe/OrderFields.java
//
//  Created by decoteaud on 11/18/15.
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/appnativa/rare/Platform.h"
#include "com/appnativa/rare/aWorkerTask.h"
#include "com/appnativa/rare/exception/ApplicationException.h"
#include "com/appnativa/rare/iCancelableFuture.h"
#include "com/appnativa/rare/iFunctionCallback.h"
#include "com/appnativa/rare/net/ActionLink.h"
#include "com/appnativa/rare/scripting/Functions.h"
#include "com/appnativa/rare/ui/RenderableDataItem.h"
#include "com/appnativa/rare/ui/UIFont.h"
#include "com/appnativa/rare/viewer/WindowViewer.h"
#include "com/appnativa/util/ObjectCache.h"
#include "com/appnativa/util/RegularExpressionFilter.h"
#include "com/appnativa/util/SNumber.h"
#include "com/appnativa/util/json/JSONArray.h"
#include "com/appnativa/util/json/JSONObject.h"
#include "com/sparseware/bellavista/Orders.h"
#include "com/sparseware/bellavista/Utils.h"
#include "com/sparseware/bellavista/oe/FieldValue.h"
#include "com/sparseware/bellavista/oe/Order.h"
#include "com/sparseware/bellavista/oe/OrderFields.h"
#include "java/lang/Double.h"
#include "java/lang/Exception.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/lang/RuntimeException.h"
#include "java/lang/Throwable.h"
#include "java/util/ArrayList.h"
#include "java/util/Collections.h"
#include "java/util/HashMap.h"
#include "java/util/List.h"
#include "java/util/Locale.h"
#include "java/util/Map.h"

@implementation CCPBVOrderFields

static NSString * CCPBVOrderFields_ID_ = @"id";
static NSString * CCPBVOrderFields_MIN_VALUE_ = @"min_value";
static NSString * CCPBVOrderFields_MAX_VALUE_ = @"max_value";
static NSString * CCPBVOrderFields_DEFAULT_VALUE_ = @"default_value";
static NSString * CCPBVOrderFields_DEFAULT_VALUE_DISPLAY_ = @"default_value_display";
static NSString * CCPBVOrderFields_INCREMENT_ = @"increment";
static NSString * CCPBVOrderFields_EDITABLE_ = @"editable";
static NSString * CCPBVOrderFields_ENABLED_ = @"enabled";
static NSString * CCPBVOrderFields_VISIBLE_ = @"visible";
static NSString * CCPBVOrderFields_DECIMAL_PLACES_ = @"decimal_places";
static NSString * CCPBVOrderFields_INPUT_MASK_ = @"input_mask";
static NSString * CCPBVOrderFields_TYPE_ = @"type";
static NSString * CCPBVOrderFields_REQUIRED_ = @"required";
static NSString * CCPBVOrderFields_INPREVIEW_ = @"in_preview";
static NSString * CCPBVOrderFields_PREVIEW_PREFIX_ = @"preview_prefix";
static NSString * CCPBVOrderFields_PREVIEW_SUFFIX_ = @"preview_suffix";
static NSString * CCPBVOrderFields_SEQUENCE_NUMBER_ = @"seq_number";
static NSString * CCPBVOrderFields_MAX_OCCURRENCES_ = @"max_occurrences";
static NSString * CCPBVOrderFields_PROMPT_ = @"prompt";
static NSString * CCPBVOrderFields_DESCRIPTION_ = @"description";
static NSString * CCPBVOrderFields_DATA_URL_ = @"data_url";
static NSString * CCPBVOrderFields_FIELDS_ = @"fields";
static NSString * CCPBVOrderFields_FIELD_TYPE_ = @"_ftype";
static NSString * CCPBVOrderFields_WIDGET_COUNT_ = @"_widgetCount";
static NSString * CCPBVOrderFields_INPUT_FILTER_ = @"_inputFilter";
static RAREUTObjectCache * CCPBVOrderFields_orderingFields_;
static RAREUTObjectCache * CCPBVOrderFields_cachedLists_;

+ (NSString *)ID {
  return CCPBVOrderFields_ID_;
}

+ (void)setID:(NSString *)ID {
  CCPBVOrderFields_ID_ = ID;
}

+ (NSString *)MIN_VALUE {
  return CCPBVOrderFields_MIN_VALUE_;
}

+ (void)setMIN_VALUE:(NSString *)MIN_VALUE {
  CCPBVOrderFields_MIN_VALUE_ = MIN_VALUE;
}

+ (NSString *)MAX_VALUE {
  return CCPBVOrderFields_MAX_VALUE_;
}

+ (void)setMAX_VALUE:(NSString *)MAX_VALUE {
  CCPBVOrderFields_MAX_VALUE_ = MAX_VALUE;
}

+ (NSString *)DEFAULT_VALUE {
  return CCPBVOrderFields_DEFAULT_VALUE_;
}

+ (void)setDEFAULT_VALUE:(NSString *)DEFAULT_VALUE {
  CCPBVOrderFields_DEFAULT_VALUE_ = DEFAULT_VALUE;
}

+ (NSString *)DEFAULT_VALUE_DISPLAY {
  return CCPBVOrderFields_DEFAULT_VALUE_DISPLAY_;
}

+ (void)setDEFAULT_VALUE_DISPLAY:(NSString *)DEFAULT_VALUE_DISPLAY {
  CCPBVOrderFields_DEFAULT_VALUE_DISPLAY_ = DEFAULT_VALUE_DISPLAY;
}

+ (NSString *)INCREMENT {
  return CCPBVOrderFields_INCREMENT_;
}

+ (void)setINCREMENT:(NSString *)INCREMENT {
  CCPBVOrderFields_INCREMENT_ = INCREMENT;
}

+ (NSString *)EDITABLE {
  return CCPBVOrderFields_EDITABLE_;
}

+ (void)setEDITABLE:(NSString *)EDITABLE {
  CCPBVOrderFields_EDITABLE_ = EDITABLE;
}

+ (NSString *)ENABLED {
  return CCPBVOrderFields_ENABLED_;
}

+ (void)setENABLED:(NSString *)ENABLED {
  CCPBVOrderFields_ENABLED_ = ENABLED;
}

+ (NSString *)VISIBLE {
  return CCPBVOrderFields_VISIBLE_;
}

+ (void)setVISIBLE:(NSString *)VISIBLE {
  CCPBVOrderFields_VISIBLE_ = VISIBLE;
}

+ (NSString *)DECIMAL_PLACES {
  return CCPBVOrderFields_DECIMAL_PLACES_;
}

+ (void)setDECIMAL_PLACES:(NSString *)DECIMAL_PLACES {
  CCPBVOrderFields_DECIMAL_PLACES_ = DECIMAL_PLACES;
}

+ (NSString *)INPUT_MASK {
  return CCPBVOrderFields_INPUT_MASK_;
}

+ (void)setINPUT_MASK:(NSString *)INPUT_MASK {
  CCPBVOrderFields_INPUT_MASK_ = INPUT_MASK;
}

+ (NSString *)TYPE {
  return CCPBVOrderFields_TYPE_;
}

+ (void)setTYPE:(NSString *)TYPE {
  CCPBVOrderFields_TYPE_ = TYPE;
}

+ (NSString *)REQUIRED {
  return CCPBVOrderFields_REQUIRED_;
}

+ (void)setREQUIRED:(NSString *)REQUIRED {
  CCPBVOrderFields_REQUIRED_ = REQUIRED;
}

+ (NSString *)INPREVIEW {
  return CCPBVOrderFields_INPREVIEW_;
}

+ (void)setINPREVIEW:(NSString *)INPREVIEW {
  CCPBVOrderFields_INPREVIEW_ = INPREVIEW;
}

+ (NSString *)PREVIEW_PREFIX {
  return CCPBVOrderFields_PREVIEW_PREFIX_;
}

+ (void)setPREVIEW_PREFIX:(NSString *)PREVIEW_PREFIX {
  CCPBVOrderFields_PREVIEW_PREFIX_ = PREVIEW_PREFIX;
}

+ (NSString *)PREVIEW_SUFFIX {
  return CCPBVOrderFields_PREVIEW_SUFFIX_;
}

+ (void)setPREVIEW_SUFFIX:(NSString *)PREVIEW_SUFFIX {
  CCPBVOrderFields_PREVIEW_SUFFIX_ = PREVIEW_SUFFIX;
}

+ (NSString *)SEQUENCE_NUMBER {
  return CCPBVOrderFields_SEQUENCE_NUMBER_;
}

+ (void)setSEQUENCE_NUMBER:(NSString *)SEQUENCE_NUMBER {
  CCPBVOrderFields_SEQUENCE_NUMBER_ = SEQUENCE_NUMBER;
}

+ (NSString *)MAX_OCCURRENCES {
  return CCPBVOrderFields_MAX_OCCURRENCES_;
}

+ (void)setMAX_OCCURRENCES:(NSString *)MAX_OCCURRENCES {
  CCPBVOrderFields_MAX_OCCURRENCES_ = MAX_OCCURRENCES;
}

+ (NSString *)PROMPT {
  return CCPBVOrderFields_PROMPT_;
}

+ (void)setPROMPT:(NSString *)PROMPT {
  CCPBVOrderFields_PROMPT_ = PROMPT;
}

+ (NSString *)DESCRIPTION {
  return CCPBVOrderFields_DESCRIPTION_;
}

+ (void)setDESCRIPTION:(NSString *)DESCRIPTION {
  CCPBVOrderFields_DESCRIPTION_ = DESCRIPTION;
}

+ (NSString *)DATA_URL {
  return CCPBVOrderFields_DATA_URL_;
}

+ (void)setDATA_URL:(NSString *)DATA_URL {
  CCPBVOrderFields_DATA_URL_ = DATA_URL;
}

+ (NSString *)FIELDS {
  return CCPBVOrderFields_FIELDS_;
}

+ (void)setFIELDS:(NSString *)FIELDS {
  CCPBVOrderFields_FIELDS_ = FIELDS;
}

+ (NSString *)FIELD_TYPE {
  return CCPBVOrderFields_FIELD_TYPE_;
}

+ (void)setFIELD_TYPE:(NSString *)FIELD_TYPE {
  CCPBVOrderFields_FIELD_TYPE_ = FIELD_TYPE;
}

+ (NSString *)WIDGET_COUNT {
  return CCPBVOrderFields_WIDGET_COUNT_;
}

+ (void)setWIDGET_COUNT:(NSString *)WIDGET_COUNT {
  CCPBVOrderFields_WIDGET_COUNT_ = WIDGET_COUNT;
}

+ (NSString *)INPUT_FILTER {
  return CCPBVOrderFields_INPUT_FILTER_;
}

+ (void)setINPUT_FILTER:(NSString *)INPUT_FILTER {
  CCPBVOrderFields_INPUT_FILTER_ = INPUT_FILTER;
}

+ (RAREUTObjectCache *)orderingFields {
  return CCPBVOrderFields_orderingFields_;
}

+ (void)setOrderingFields:(RAREUTObjectCache *)orderingFields {
  CCPBVOrderFields_orderingFields_ = orderingFields;
}

+ (RAREUTObjectCache *)cachedLists {
  return CCPBVOrderFields_cachedLists_;
}

+ (void)setCachedLists:(RAREUTObjectCache *)cachedLists {
  CCPBVOrderFields_cachedLists_ = cachedLists;
}

- (id)initWithNSString:(NSString *)id_
          withNSString:(NSString *)href {
  if (self = [super init]) {
    widgetCount_ = -1;
    self->id__ = id_;
    dataHref_ = href;
  }
  return self;
}

- (id)initWithNSString:(NSString *)id_
      withJavaUtilList:(id<JavaUtilList>)fields {
  if (self = [super init]) {
    widgetCount_ = -1;
    self->id__ = id_;
    self->fields_ = fields;
  }
  return self;
}

- (id)initWithNSString:(NSString *)id_
          withNSString:(NSString *)href
           withBoolean:(BOOL)load_ {
  if (self = [super init]) {
    widgetCount_ = -1;
    self->id__ = id_;
    dataHref_ = href;
    if (load_) {
      [self load__WithRAREiFunctionCallback:nil];
    }
  }
  return self;
}

- (id<JavaUtilList>)createTableValuesWithCCPBVOrder:(CCPBVOrder *)order
                                     withRAREUIFont:(RAREUIFont *)requiredFieldFont {
  id<JavaUtilMap> values = ((CCPBVOrder *) nil_chk(order))->orderValues_;
  int len = [((id<JavaUtilList>) nil_chk(fields_)) size];
  id<JavaUtilList> rows = [[JavaUtilArrayList alloc] initWithInt:len];
  id<JavaUtilList> list = fields_;
  NSString *s;
  if (values == nil) {
    values = order->orderValues_ = [[JavaUtilHashMap alloc] initWithInt:len];
  }
  RAREWindowViewer *w = [RAREPlatform getWindowViewer];
  for (int i = 0; i < len; i++) {
    RAREUTJSONObject *field = [list getWithInt:i];
    NSString *id_ = [((RAREUTJSONObject *) nil_chk(field)) optStringWithNSString:CCPBVOrderFields_ID_ withNSString:nil];
    if (id_ == nil) {
      continue;
    }
    CCPBVOrderFields_FieldTypeEnum *type = (CCPBVOrderFields_FieldTypeEnum *) [field optWithNSString:CCPBVOrderFields_FIELD_TYPE_];
    if (type == nil) {
      s = [((NSString *) nil_chk([field getStringWithNSString:CCPBVOrderFields_TYPE_])) uppercaseStringWithJRELocale:[JavaUtilLocale US]];
      type = [CCPBVOrderFields_FieldTypeEnum valueOfWithNSString:s];
      (void) [field putWithNSString:CCPBVOrderFields_FIELD_TYPE_ withId:type];
    }
    int itype = RARERenderableDataItem_TYPE_STRUCT;
    CCPBVFieldValue *value = [((id<JavaUtilMap>) nil_chk(values)) getWithId:id_];
    if (value == nil) {
      value = [[CCPBVFieldValue alloc] initWithRAREUTJSONObject:field];
      (void) [values putWithId:id_ withId:value];
    }
    else if ((value->field_ == nil) && (value->value_ != nil)) {
      RAREUTJSONObject *ff = [[RAREUTJSONObject alloc] init];
      [ff putAllWithJavaUtilMap:field];
      (void) [ff putWithNSString:CCPBVOrderFields_DEFAULT_VALUE_ withId:value->value_];
      (void) [ff putWithNSString:CCPBVOrderFields_DEFAULT_VALUE_DISPLAY_ withId:value->displayValue_];
      field = ff;
    }
    ((CCPBVFieldValue *) nil_chk(value))->field_ = field;
    if ([CCPBVOrderFields isHiddenWithRAREUTJSONObject:field]) {
      continue;
    }
    RARERenderableDataItem *valueItem = [[RARERenderableDataItem alloc] initWithId:value withInt:itype withId:nil];
    RARERenderableDataItem *nameItem = [[RARERenderableDataItem alloc] initWithId:[((RAREWindowViewer *) nil_chk(w)) expandStringWithNSString:[field getStringWithNSString:CCPBVOrderFields_PROMPT_]]];
    RARERenderableDataItem *row = [[RARERenderableDataItem alloc] init];
    [row addWithId:nameItem];
    [row addWithId:valueItem];
    if ([CCPBVOrderFields isRequiredWithRAREUTJSONObject:field]) {
      [row setUserStateFlagWithByte:CCPBVOrders_REQUIRED_FLAG];
      [row setFontWithRAREUIFont:requiredFieldFont];
    }
    if (![CCPBVOrderFields isEditableWithRAREUTJSONObject:field]) {
      [row setSelectableWithBoolean:NO];
    }
    [row setLinkedDataWithId:value];
    [rows addWithId:row];
  }
  return rows;
}

- (id<JavaUtilList>)getFields {
  return fields_;
}

- (NSString *)getID {
  return id__;
}

- (BOOL)isLoaded {
  return loaded_;
}

- (void)load__WithRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  if ((cb == nil) && ![RAREPlatform isUIThread]) {
    @try {
      [self loadEx];
    }
    @catch (JavaLangException *e) {
      @throw [RAREApplicationException runtimeExceptionWithJavaLangThrowable:e];
    }
  }
  RAREaWorkerTask *task = [[CCPBVOrderFields_$1 alloc] initWithCCPBVOrderFields:self withRAREiFunctionCallback:cb];
  (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ task } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
}

- (void)loadEx {
  @try {
    RAREActionLink *l = [[RAREActionLink alloc] initWithNSString:dataHref_];
    RAREUTJSONObject *o = [[RAREUTJSONObject alloc] initWithNSString:[l getContentAsString]];
    RAREUTJSONArray *a = [o getJSONArrayWithNSString:@"rows"];
    if (a == nil) {
      @throw [[RAREApplicationException alloc] initWithNSString:[NSString stringWithFormat:@"%@ is not a valid fields definition", dataHref_]];
    }
    fields_ = (id<JavaUtilList>) check_protocol_cast([((RAREUTJSONArray *) nil_chk(a)) getObjectList], @protocol(JavaUtilList));
    [JavaUtilCollections sortWithJavaUtilList:fields_ withJavaUtilComparator:[[CCPBVOrderFields_$2 alloc] init]];
  }
  @finally {
    loaded_ = YES;
  }
}

+ (NSString *)getDescriptionWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return [((RAREUTJSONObject *) nil_chk(field)) getStringWithNSString:CCPBVOrderFields_DESCRIPTION_];
}

- (int)getWidgetCount {
  int count = widgetCount_;
  if (count < 0) {
    count = 0;
    for (RAREUTJSONObject * __strong field in nil_chk(fields_)) {
      if ([CCPBVOrderFields isEditableWithRAREUTJSONObject:field] || ![CCPBVOrderFields isHiddenWithRAREUTJSONObject:field]) {
        count++;
      }
    }
    widgetCount_ = count;
  }
  return count;
}

+ (int)getWidgetCountWithRAREUTJSONObject:(RAREUTJSONObject *)info {
  int count = [((RAREUTJSONObject *) nil_chk(info)) optIntWithNSString:CCPBVOrderFields_WIDGET_COUNT_ withInt:-1];
  if (count < 0) {
    RAREUTJSONArray *a = [info getJSONArrayWithNSString:@"fields"];
    count = 0;
    for (RAREUTJSONObject * __strong field in nil_chk([((RAREUTJSONArray *) nil_chk(a)) getObjectList])) {
      if ([CCPBVOrderFields isEditableWithRAREUTJSONObject:field] || ![CCPBVOrderFields isHiddenWithRAREUTJSONObject:field]) {
        count++;
      }
    }
    (void) [info putWithNSString:CCPBVOrderFields_WIDGET_COUNT_ withInt:count];
  }
  return count;
}

+ (CCPBVOrderFields_FieldTypeEnum *)getFieldTypeWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  CCPBVOrderFields_FieldTypeEnum *type = (CCPBVOrderFields_FieldTypeEnum *) [((RAREUTJSONObject *) nil_chk(field)) optWithNSString:CCPBVOrderFields_FIELD_TYPE_];
  if (type == nil) {
    NSString *s = [((NSString *) nil_chk([field optStringWithNSString:CCPBVOrderFields_TYPE_ withNSString:@"group"])) uppercaseStringWithJRELocale:[JavaUtilLocale US]];
    type = [CCPBVOrderFields_FieldTypeEnum valueOfWithNSString:s];
    (void) [field putWithNSString:CCPBVOrderFields_FIELD_TYPE_ withId:type];
  }
  return type;
}

+ (NSString *)getIDWithRAREUTJSONObject:(RAREUTJSONObject *)field
                            withBoolean:(BOOL)onlyIfInPreview {
  if (onlyIfInPreview && ![((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_INPREVIEW_]) {
    return nil;
  }
  return [((RAREUTJSONObject *) nil_chk(field)) optStringWithNSString:CCPBVOrderFields_ID_ withNSString:nil];
}

+ (void)getItemDiscontinueFieldsWithNSString:(NSString *)itemID
                   withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  RAREaWorkerTask *task = [[CCPBVOrderFields_$3 alloc] initWithNSString:itemID withRAREiFunctionCallback:cb];
  (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ task } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
}

+ (CCPBVOrderFields *)getOrderFieldsWithNSString:(NSString *)fieldsID {
  if ([RAREPlatform isUIThread]) {
    @throw [[RAREApplicationException alloc] initWithNSString:@"MUST be called from a background thread"];
  }
  CCPBVOrderFields *fields = nil;
  if (CCPBVOrderFields_orderingFields_ != nil) {
    fields = (CCPBVOrderFields *) check_class_cast([CCPBVOrderFields_orderingFields_ getWithId:fieldsID], [CCPBVOrderFields class]);
  }
  if (fields == nil) {
    fields = [[CCPBVOrderFields alloc] initWithNSString:fieldsID withNSString:[NSString stringWithFormat:@"/hub/main/util/ordering/fields/%@.json", fieldsID]];
    [fields load__WithRAREiFunctionCallback:nil];
    if (CCPBVOrderFields_orderingFields_ == nil) {
      CCPBVOrderFields_orderingFields_ = [[RAREUTObjectCache alloc] init];
      [CCPBVOrderFields_orderingFields_ setBufferSizeWithInt:10];
      [CCPBVOrderFields_orderingFields_ setPurgeInlineWithBoolean:YES];
      [CCPBVOrderFields_orderingFields_ setStrongReferencesWithBoolean:YES];
    }
    (void) [((RAREUTObjectCache *) nil_chk(CCPBVOrderFields_orderingFields_)) putWithId:[fields getID] withId:fields];
  }
  return fields;
}

+ (void)getOrderSentencesWithNSString:(NSString *)itemID
            withRAREiFunctionCallback:(id<RAREiFunctionCallback>)cb {
  RAREaWorkerTask *task = [[CCPBVOrderFields_$4 alloc] initWithNSString:itemID withRAREiFunctionCallback:cb];
  (void) [((RAREWindowViewer *) nil_chk([RAREPlatform getWindowViewer])) spawnWithNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ task } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
}

+ (id<JavaUtilMap>)getFieldValuesWithNSString:(NSString *)url {
  RAREUTJSONObject *o = [CCPBVUtils getContentAsJSONWithNSString:url withJavaUtilMap:nil withBoolean:NO];
  RAREUTJSONArray *a = (o == nil) ? nil : [o getJSONArrayWithNSString:@"rows"];
  JavaUtilHashMap *values = nil;
  int len = (a == nil) ? 0 : [a size];
  if (len > 0) {
    values = [[JavaUtilHashMap alloc] initWithInt:len];
    for (int i = 0; i < len; i++) {
      RAREUTJSONObject *value = [a getJSONObjectWithInt:i];
      NSString *id_ = [((RAREUTJSONObject *) nil_chk(value)) getStringWithNSString:CCPBVOrderFields_ID_];
      (void) [values putWithId:id_ withId:[[CCPBVFieldValue alloc] initWithId:[value getStringWithNSString:CCPBVOrderFields_DEFAULT_VALUE_] withNSString:[value optStringWithNSString:CCPBVOrderFields_DEFAULT_VALUE_DISPLAY_ withNSString:nil]]];
    }
  }
  return values;
}

+ (BOOL)isEditableWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return [((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_EDITABLE_ withBoolean:YES];
}

+ (BOOL)isEnabledWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return [((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_ENABLED_ withBoolean:YES];
}

+ (BOOL)isHiddenWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return ![((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_VISIBLE_ withBoolean:YES];
}

+ (BOOL)isValidWithRAREUTJSONObject:(RAREUTJSONObject *)field
                             withId:(id)value {
  {
    NSString *s;
    switch ([[CCPBVOrderFields getFieldTypeWithRAREUTJSONObject:field] ordinal]) {
      case CCPBVOrderFields_FieldType_INTEGER:
      case CCPBVOrderFields_FieldType_DECIMAL:
      return [CCPBVOrderFields isInRangeValueWithRAREUTJSONObject:field withRAREUTSNumber:(RAREUTSNumber *) check_class_cast(value, [RAREUTSNumber class])];
      case CCPBVOrderFields_FieldType_TEXTFIELD:
      s = (NSString *) check_class_cast(value, [NSString class]);
      if (![CCPBVOrderFields isInRangeValueWithRAREUTJSONObject:field withInt:[((NSString *) nil_chk(s)) sequenceLength]]) {
        return NO;
      }
      return [CCPBVOrderFields isValidPatternWithRAREUTJSONObject:field withNSString:s];
      default:
      break;
    }
  }
  return YES;
}

+ (BOOL)isInRangeValueWithRAREUTJSONObject:(RAREUTJSONObject *)field
                         withRAREUTSNumber:(RAREUTSNumber *)value {
  RAREUTSNumber *min = [CCPBVOrderFields getNumberWithRAREUTJSONObject:field withBoolean:YES];
  RAREUTSNumber *max = [CCPBVOrderFields getNumberWithRAREUTJSONObject:field withBoolean:YES];
  if ((min != nil) && [((RAREUTSNumber *) nil_chk(value)) ltWithRAREUTSNumber:min]) {
    return NO;
  }
  if ((min != nil) && [((RAREUTSNumber *) nil_chk(value)) gtWithRAREUTSNumber:max]) {
    return NO;
  }
  return YES;
}

+ (BOOL)isValidPatternWithRAREUTJSONObject:(RAREUTJSONObject *)field
                              withNSString:(NSString *)value {
  RAREUTRegularExpressionFilter *f = (RAREUTRegularExpressionFilter *) check_class_cast([((RAREUTJSONObject *) nil_chk(field)) optWithNSString:CCPBVOrderFields_INPUT_FILTER_], [RAREUTRegularExpressionFilter class]);
  if (f == nil) {
    NSString *mask = [field optStringWithNSString:CCPBVOrderFields_INPUT_MASK_ withNSString:nil];
    if (mask != nil) {
      f = [RAREFunctions createRegExFilterWithNSString:mask withBoolean:NO];
      (void) [field putWithNSString:CCPBVOrderFields_INPUT_FILTER_ withId:f];
    }
  }
  return (f == nil) ? YES : [f passesWithId:value withRAREUTiStringConverter:nil];
}

+ (BOOL)isInRangeValueWithRAREUTJSONObject:(RAREUTJSONObject *)field
                                   withInt:(int)value {
  RAREUTSNumber *min = [CCPBVOrderFields getNumberWithRAREUTJSONObject:field withBoolean:YES];
  RAREUTSNumber *max = [CCPBVOrderFields getNumberWithRAREUTJSONObject:field withBoolean:YES];
  if ((min != nil) && (value < [min intValue])) {
    return NO;
  }
  if ((min != nil) && (value > [((RAREUTSNumber *) nil_chk(max)) intValue])) {
    return NO;
  }
  return YES;
}

+ (RAREUTSNumber *)getNumberWithRAREUTJSONObject:(RAREUTJSONObject *)field
                                     withBoolean:(BOOL)min {
  id o = [((RAREUTJSONObject *) nil_chk(field)) optWithNSString:min ? CCPBVOrderFields_MIN_VALUE_ : CCPBVOrderFields_MAX_VALUE_];
  if ([o isKindOfClass:[RAREUTSNumber class]]) {
    return (RAREUTSNumber *) check_class_cast(o, [RAREUTSNumber class]);
  }
  else if ([o isKindOfClass:[NSString class]]) {
    RAREUTSNumber *num = [[RAREUTSNumber alloc] initWithNSString:(NSString *) check_class_cast(o, [NSString class])];
    (void) [field putWithNSString:min ? CCPBVOrderFields_MIN_VALUE_ : CCPBVOrderFields_MAX_VALUE_ withId:num];
    return num;
  }
  else if ([o isKindOfClass:[NSNumber class]]) {
    RAREUTSNumber *num = [[RAREUTSNumber alloc] initWithDouble:[((NSNumber *) check_class_cast(o, [NSNumber class])) doubleValue]];
    (void) [field putWithNSString:min ? CCPBVOrderFields_MIN_VALUE_ : CCPBVOrderFields_MAX_VALUE_ withId:num];
    return num;
  }
  return nil;
}

+ (BOOL)isInPreviewWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return [((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_INPREVIEW_];
}

+ (BOOL)isRequiredWithRAREUTJSONObject:(RAREUTJSONObject *)field {
  return [((RAREUTJSONObject *) nil_chk(field)) optBooleanWithNSString:CCPBVOrderFields_REQUIRED_];
}

- (void)copyAllFieldsTo:(CCPBVOrderFields *)other {
  [super copyAllFieldsTo:other];
  other->dataHref_ = dataHref_;
  other->fields_ = fields_;
  other->id__ = id__;
  other->loaded_ = loaded_;
  other->orderType_ = orderType_;
  other->widgetCount_ = widgetCount_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "createTableValuesWithCCPBVOrder:withRAREUIFont:", NULL, "LJavaUtilList", 0x1, NULL },
    { "getFields", NULL, "LJavaUtilList", 0x1, NULL },
    { "getID", NULL, "LNSString", 0x1, NULL },
    { "isLoaded", NULL, "Z", 0x1, NULL },
    { "loadEx", NULL, "V", 0x4, "JavaLangException" },
    { "getDescriptionWithRAREUTJSONObject:", NULL, "LNSString", 0x9, NULL },
    { "getFieldTypeWithRAREUTJSONObject:", NULL, "LCCPBVOrderFields_FieldTypeEnum", 0x9, NULL },
    { "getIDWithRAREUTJSONObject:withBoolean:", NULL, "LNSString", 0x9, NULL },
    { "getOrderFieldsWithNSString:", NULL, "LCCPBVOrderFields", 0x9, NULL },
    { "getFieldValuesWithNSString:", NULL, "LJavaUtilMap", 0x9, "JavaLangException" },
    { "isEditableWithRAREUTJSONObject:", NULL, "Z", 0x9, NULL },
    { "isEnabledWithRAREUTJSONObject:", NULL, "Z", 0x9, NULL },
    { "isHiddenWithRAREUTJSONObject:", NULL, "Z", 0x9, NULL },
    { "isValidWithRAREUTJSONObject:withId:", NULL, "Z", 0x9, NULL },
    { "isInRangeValueWithRAREUTJSONObject:withRAREUTSNumber:", NULL, "Z", 0x9, NULL },
    { "isValidPatternWithRAREUTJSONObject:withNSString:", NULL, "Z", 0x9, NULL },
    { "isInRangeValueWithRAREUTJSONObject:withInt:", NULL, "Z", 0x9, NULL },
    { "getNumberWithRAREUTJSONObject:withBoolean:", NULL, "LRAREUTSNumber", 0x9, NULL },
    { "isInPreviewWithRAREUTJSONObject:", NULL, "Z", 0x9, NULL },
    { "isRequiredWithRAREUTJSONObject:", NULL, "Z", 0x9, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "ID_", NULL, 0x9, "LNSString" },
    { "MIN_VALUE_", NULL, 0x9, "LNSString" },
    { "MAX_VALUE_", NULL, 0x9, "LNSString" },
    { "DEFAULT_VALUE_", NULL, 0x9, "LNSString" },
    { "DEFAULT_VALUE_DISPLAY_", NULL, 0x9, "LNSString" },
    { "INCREMENT_", NULL, 0x9, "LNSString" },
    { "EDITABLE_", NULL, 0x9, "LNSString" },
    { "ENABLED_", NULL, 0x9, "LNSString" },
    { "VISIBLE_", NULL, 0x9, "LNSString" },
    { "DECIMAL_PLACES_", NULL, 0x9, "LNSString" },
    { "INPUT_MASK_", NULL, 0x9, "LNSString" },
    { "TYPE_", NULL, 0x9, "LNSString" },
    { "REQUIRED_", NULL, 0x9, "LNSString" },
    { "INPREVIEW_", NULL, 0x9, "LNSString" },
    { "PREVIEW_PREFIX_", NULL, 0x9, "LNSString" },
    { "PREVIEW_SUFFIX_", NULL, 0x9, "LNSString" },
    { "SEQUENCE_NUMBER_", NULL, 0x9, "LNSString" },
    { "MAX_OCCURRENCES_", NULL, 0x9, "LNSString" },
    { "PROMPT_", NULL, 0x9, "LNSString" },
    { "DESCRIPTION_", NULL, 0x9, "LNSString" },
    { "DATA_URL_", NULL, 0x9, "LNSString" },
    { "FIELDS_", NULL, 0x9, "LNSString" },
    { "FIELD_TYPE_", NULL, 0x9, "LNSString" },
    { "WIDGET_COUNT_", NULL, 0x9, "LNSString" },
    { "INPUT_FILTER_", NULL, 0x9, "LNSString" },
    { "orderingFields_", NULL, 0x8, "LRAREUTObjectCache" },
    { "cachedLists_", NULL, 0x8, "LRAREUTObjectCache" },
    { "id__", "id", 0x0, "LNSString" },
    { "fields_", NULL, 0x0, "LJavaUtilList" },
    { "orderType_", NULL, 0x0, "LCCPBVOrder_ActionTypeEnum" },
    { "dataHref_", NULL, 0x0, "LNSString" },
    { "loaded_", NULL, 0x0, "Z" },
  };
  static J2ObjcClassInfo _CCPBVOrderFields = { "OrderFields", "com.sparseware.bellavista.oe", NULL, 0x1, 20, methods, 32, fields, 0, NULL};
  return &_CCPBVOrderFields;
}

@end

static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_BOOLEAN;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_TEXTFIELD;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_TEXTAREA;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_RICHTEXT;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_PASSWORD;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_INTEGER;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_DECIMAL;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_LIST;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_DATE;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_DATE_TIME;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_TIME;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_LABEL;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_LINE;
static CCPBVOrderFields_FieldTypeEnum *CCPBVOrderFields_FieldTypeEnum_GROUP;
IOSObjectArray *CCPBVOrderFields_FieldTypeEnum_values;

@implementation CCPBVOrderFields_FieldTypeEnum

+ (CCPBVOrderFields_FieldTypeEnum *)BOOLEAN {
  return CCPBVOrderFields_FieldTypeEnum_BOOLEAN;
}
+ (CCPBVOrderFields_FieldTypeEnum *)TEXTFIELD {
  return CCPBVOrderFields_FieldTypeEnum_TEXTFIELD;
}
+ (CCPBVOrderFields_FieldTypeEnum *)TEXTAREA {
  return CCPBVOrderFields_FieldTypeEnum_TEXTAREA;
}
+ (CCPBVOrderFields_FieldTypeEnum *)RICHTEXT {
  return CCPBVOrderFields_FieldTypeEnum_RICHTEXT;
}
+ (CCPBVOrderFields_FieldTypeEnum *)PASSWORD {
  return CCPBVOrderFields_FieldTypeEnum_PASSWORD;
}
+ (CCPBVOrderFields_FieldTypeEnum *)INTEGER {
  return CCPBVOrderFields_FieldTypeEnum_INTEGER;
}
+ (CCPBVOrderFields_FieldTypeEnum *)DECIMAL {
  return CCPBVOrderFields_FieldTypeEnum_DECIMAL;
}
+ (CCPBVOrderFields_FieldTypeEnum *)LIST {
  return CCPBVOrderFields_FieldTypeEnum_LIST;
}
+ (CCPBVOrderFields_FieldTypeEnum *)DATE {
  return CCPBVOrderFields_FieldTypeEnum_DATE;
}
+ (CCPBVOrderFields_FieldTypeEnum *)DATE_TIME {
  return CCPBVOrderFields_FieldTypeEnum_DATE_TIME;
}
+ (CCPBVOrderFields_FieldTypeEnum *)TIME {
  return CCPBVOrderFields_FieldTypeEnum_TIME;
}
+ (CCPBVOrderFields_FieldTypeEnum *)LABEL {
  return CCPBVOrderFields_FieldTypeEnum_LABEL;
}
+ (CCPBVOrderFields_FieldTypeEnum *)LINE {
  return CCPBVOrderFields_FieldTypeEnum_LINE;
}
+ (CCPBVOrderFields_FieldTypeEnum *)GROUP {
  return CCPBVOrderFields_FieldTypeEnum_GROUP;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [CCPBVOrderFields_FieldTypeEnum class]) {
    CCPBVOrderFields_FieldTypeEnum_BOOLEAN = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"BOOLEAN" withInt:0];
    CCPBVOrderFields_FieldTypeEnum_TEXTFIELD = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"TEXTFIELD" withInt:1];
    CCPBVOrderFields_FieldTypeEnum_TEXTAREA = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"TEXTAREA" withInt:2];
    CCPBVOrderFields_FieldTypeEnum_RICHTEXT = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"RICHTEXT" withInt:3];
    CCPBVOrderFields_FieldTypeEnum_PASSWORD = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"PASSWORD" withInt:4];
    CCPBVOrderFields_FieldTypeEnum_INTEGER = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"INTEGER" withInt:5];
    CCPBVOrderFields_FieldTypeEnum_DECIMAL = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"DECIMAL" withInt:6];
    CCPBVOrderFields_FieldTypeEnum_LIST = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"LIST" withInt:7];
    CCPBVOrderFields_FieldTypeEnum_DATE = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"DATE" withInt:8];
    CCPBVOrderFields_FieldTypeEnum_DATE_TIME = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"DATE_TIME" withInt:9];
    CCPBVOrderFields_FieldTypeEnum_TIME = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"TIME" withInt:10];
    CCPBVOrderFields_FieldTypeEnum_LABEL = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"LABEL" withInt:11];
    CCPBVOrderFields_FieldTypeEnum_LINE = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"LINE" withInt:12];
    CCPBVOrderFields_FieldTypeEnum_GROUP = [[CCPBVOrderFields_FieldTypeEnum alloc] initWithNSString:@"GROUP" withInt:13];
    CCPBVOrderFields_FieldTypeEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ CCPBVOrderFields_FieldTypeEnum_BOOLEAN, CCPBVOrderFields_FieldTypeEnum_TEXTFIELD, CCPBVOrderFields_FieldTypeEnum_TEXTAREA, CCPBVOrderFields_FieldTypeEnum_RICHTEXT, CCPBVOrderFields_FieldTypeEnum_PASSWORD, CCPBVOrderFields_FieldTypeEnum_INTEGER, CCPBVOrderFields_FieldTypeEnum_DECIMAL, CCPBVOrderFields_FieldTypeEnum_LIST, CCPBVOrderFields_FieldTypeEnum_DATE, CCPBVOrderFields_FieldTypeEnum_DATE_TIME, CCPBVOrderFields_FieldTypeEnum_TIME, CCPBVOrderFields_FieldTypeEnum_LABEL, CCPBVOrderFields_FieldTypeEnum_LINE, CCPBVOrderFields_FieldTypeEnum_GROUP, nil } count:14 type:[IOSClass classWithClass:[CCPBVOrderFields_FieldTypeEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:CCPBVOrderFields_FieldTypeEnum_values];
}

+ (CCPBVOrderFields_FieldTypeEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [CCPBVOrderFields_FieldTypeEnum_values count]; i++) {
    CCPBVOrderFields_FieldTypeEnum *e = CCPBVOrderFields_FieldTypeEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LCCPBVOrderFields_FieldTypeEnum"};
  static J2ObjcClassInfo _CCPBVOrderFields_FieldTypeEnum = { "FieldType", "com.sparseware.bellavista.oe", "OrderFields", 0x4019, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_CCPBVOrderFields_FieldTypeEnum;
}

@end

static CCPBVOrderFields_GroupTypeEnum *CCPBVOrderFields_GroupTypeEnum_INLINE;
static CCPBVOrderFields_GroupTypeEnum *CCPBVOrderFields_GroupTypeEnum_DROP_DOWN;
static CCPBVOrderFields_GroupTypeEnum *CCPBVOrderFields_GroupTypeEnum_OVERLAY;
static CCPBVOrderFields_GroupTypeEnum *CCPBVOrderFields_GroupTypeEnum_DIALOG;
IOSObjectArray *CCPBVOrderFields_GroupTypeEnum_values;

@implementation CCPBVOrderFields_GroupTypeEnum

+ (CCPBVOrderFields_GroupTypeEnum *)INLINE {
  return CCPBVOrderFields_GroupTypeEnum_INLINE;
}
+ (CCPBVOrderFields_GroupTypeEnum *)DROP_DOWN {
  return CCPBVOrderFields_GroupTypeEnum_DROP_DOWN;
}
+ (CCPBVOrderFields_GroupTypeEnum *)OVERLAY {
  return CCPBVOrderFields_GroupTypeEnum_OVERLAY;
}
+ (CCPBVOrderFields_GroupTypeEnum *)DIALOG {
  return CCPBVOrderFields_GroupTypeEnum_DIALOG;
}

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [CCPBVOrderFields_GroupTypeEnum class]) {
    CCPBVOrderFields_GroupTypeEnum_INLINE = [[CCPBVOrderFields_GroupTypeEnum alloc] initWithNSString:@"INLINE" withInt:0];
    CCPBVOrderFields_GroupTypeEnum_DROP_DOWN = [[CCPBVOrderFields_GroupTypeEnum alloc] initWithNSString:@"DROP_DOWN" withInt:1];
    CCPBVOrderFields_GroupTypeEnum_OVERLAY = [[CCPBVOrderFields_GroupTypeEnum alloc] initWithNSString:@"OVERLAY" withInt:2];
    CCPBVOrderFields_GroupTypeEnum_DIALOG = [[CCPBVOrderFields_GroupTypeEnum alloc] initWithNSString:@"DIALOG" withInt:3];
    CCPBVOrderFields_GroupTypeEnum_values = [[IOSObjectArray alloc] initWithObjects:(id[]){ CCPBVOrderFields_GroupTypeEnum_INLINE, CCPBVOrderFields_GroupTypeEnum_DROP_DOWN, CCPBVOrderFields_GroupTypeEnum_OVERLAY, CCPBVOrderFields_GroupTypeEnum_DIALOG, nil } count:4 type:[IOSClass classWithClass:[CCPBVOrderFields_GroupTypeEnum class]]];
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithArray:CCPBVOrderFields_GroupTypeEnum_values];
}

+ (CCPBVOrderFields_GroupTypeEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < [CCPBVOrderFields_GroupTypeEnum_values count]; i++) {
    CCPBVOrderFields_GroupTypeEnum *e = CCPBVOrderFields_GroupTypeEnum_values->buffer_[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static const char *superclass_type_args[] = {"LCCPBVOrderFields_GroupTypeEnum"};
  static J2ObjcClassInfo _CCPBVOrderFields_GroupTypeEnum = { "GroupType", "com.sparseware.bellavista.oe", "OrderFields", 0x4019, 0, NULL, 0, NULL, 1, superclass_type_args};
  return &_CCPBVOrderFields_GroupTypeEnum;
}

@end
@implementation CCPBVOrderFields_$1

- (id)compute {
  @try {
    [this$0_ loadEx];
    return this$0_->fields_;
  }
  @catch (JavaLangException *e) {
    return e;
  }
}

- (void)finishWithId:(id)result {
  if ([result isKindOfClass:[JavaLangException class]]) {
    if (val$cb_ != nil) {
      [val$cb_ finishedWithBoolean:YES withId:result];
    }
  }
  else if (val$cb_ != nil) {
    [val$cb_ finishedWithBoolean:NO withId:this$0_->fields_];
  }
}

- (id)initWithCCPBVOrderFields:(CCPBVOrderFields *)outer$
     withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$0 {
  this$0_ = outer$;
  val$cb_ = capture$0;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "LCCPBVOrderFields" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVOrderFields_$1 = { "$1", "com.sparseware.bellavista.oe", "OrderFields", 0x8000, 1, methods, 2, fields, 0, NULL};
  return &_CCPBVOrderFields_$1;
}

@end
@implementation CCPBVOrderFields_$2

- (int)compareWithId:(RAREUTJSONObject *)o1
              withId:(RAREUTJSONObject *)o2 {
  double d1 = [((RAREUTJSONObject *) nil_chk(o1)) optDoubleWithNSString:[CCPBVOrderFields SEQUENCE_NUMBER] withDouble:999999999];
  double d2 = [((RAREUTJSONObject *) nil_chk(o2)) optDoubleWithNSString:[CCPBVOrderFields SEQUENCE_NUMBER] withDouble:999999999];
  return [JavaLangDouble compareWithDouble:d1 withDouble:d2];
}

- (BOOL)isEqual:(id)param0 {
  return [super isEqual:param0];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcClassInfo _CCPBVOrderFields_$2 = { "$2", "com.sparseware.bellavista.oe", "OrderFields", 0x8000, 0, NULL, 0, NULL, 0, NULL};
  return &_CCPBVOrderFields_$2;
}

@end
@implementation CCPBVOrderFields_$3

- (id)compute {
  CCPBVOrderFields *fields = [[CCPBVOrderFields alloc] initWithNSString:val$itemID_ withNSString:[NSString stringWithFormat:@"/hub/main/util/ordering/discontinue_fields/%@", val$itemID_]];
  @try {
    [fields load__WithRAREiFunctionCallback:nil];
  }
  @catch (JavaLangException *e) {
    return e;
  }
  id<JavaUtilList> list = [fields getFields];
  if ((list != nil) && ![list isEmpty]) {
    return fields;
  }
  return fields;
}

- (void)finishWithId:(id)result {
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:NO withId:result];
}

- (id)initWithNSString:(NSString *)capture$0
withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$1 {
  val$itemID_ = capture$0;
  val$cb_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "val$itemID_", NULL, 0x1012, "LNSString" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVOrderFields_$3 = { "$3", "com.sparseware.bellavista.oe", "OrderFields", 0x8000, 1, methods, 2, fields, 0, NULL};
  return &_CCPBVOrderFields_$3;
}

@end
@implementation CCPBVOrderFields_$4

- (id)compute {
  @try {
    return [CCPBVUtils getContentAsJSONWithNSString:[NSString stringWithFormat:@"/hub/main/util/ordering/sentences/%@", val$itemID_] withJavaUtilMap:nil withBoolean:NO];
  }
  @catch (JavaLangException *e) {
    return e;
  }
}

- (void)finishWithId:(id)result {
  [((id<RAREiFunctionCallback>) nil_chk(val$cb_)) finishedWithBoolean:[result isKindOfClass:[JavaLangThrowable class]] withId:result];
}

- (id)initWithNSString:(NSString *)capture$0
withRAREiFunctionCallback:(id<RAREiFunctionCallback>)capture$1 {
  val$itemID_ = capture$0;
  val$cb_ = capture$1;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "compute", NULL, "LNSObject", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "val$itemID_", NULL, 0x1012, "LNSString" },
    { "val$cb_", NULL, 0x1012, "LRAREiFunctionCallback" },
  };
  static J2ObjcClassInfo _CCPBVOrderFields_$4 = { "$4", "com.sparseware.bellavista.oe", "OrderFields", 0x8000, 1, methods, 2, fields, 0, NULL};
  return &_CCPBVOrderFields_$4;
}

@end