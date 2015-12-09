//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/fhir/FHIRUtils.java
//
//  Created by decoteaud on 11/18/15.
//

#ifndef _ComSparsewareBellavistaExternalFhirFHIRUtils_H_
#define _ComSparsewareBellavistaExternalFhirFHIRUtils_H_

@class CCPBVActionPath;
@class CCPBVContentWriter;
@class CCPBVHttpHeaders;
@class ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode;
@class JavaIoWriter;
@class JavaUtilHashMap;
@class RAREUTCharArray;
@class RAREUTJSONArray;
@class RAREUTJSONObject;
@class RAREUTJSONWriter;
@protocol JavaUtilSet;

#import "JreEmulation.h"

@interface ComSparsewareBellavistaExternalFhirFHIRUtils : NSObject {
}

+ (JavaUtilHashMap *)interpretationColors;
- (id)init;
+ (void)writeNameWithId:(id)writer
    withRAREUTJSONArray:(RAREUTJSONArray *)a;
+ (void)writeCodableConceptWithRAREUTJSONWriter:(RAREUTJSONWriter *)jw
                            withRAREUTJSONArray:(RAREUTJSONArray *)a
                            withJavaUtilHashMap:(JavaUtilHashMap *)map;
+ (id)createWriterWithCCPBVActionPath:(CCPBVActionPath *)path
               withCCPBVContentWriter:(CCPBVContentWriter *)w
                 withCCPBVHttpHeaders:(CCPBVHttpHeaders *)headers
                          withBoolean:(BOOL)row;
+ (BOOL)isJSONFormatRequestedWithCCPBVActionPath:(CCPBVActionPath *)path;
+ (BOOL)isHTMLFormatRequestedWithCCPBVActionPath:(CCPBVActionPath *)path;
+ (NSString *)getBestMedicalCodeWithRAREUTJSONObject:(RAREUTJSONObject *)o;
+ (NSString *)getBestMedicalTextWithRAREUTJSONObject:(RAREUTJSONObject *)o;
+ (NSString *)getBestMedicalCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a;
+ (NSString *)getBestMedicalTextWithRAREUTJSONArray:(RAREUTJSONArray *)a;
+ (NSString *)getHL7FHIRCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a;
+ (NSString *)getInterpretationColorWithNSString:(NSString *)code;
+ (NSString *)getRangeWithRAREUTJSONArray:(RAREUTJSONArray *)ranges
                              withBoolean:(BOOL)includeUnits
                      withRAREUTCharArray:(RAREUTCharArray *)ca;
+ (NSString *)cleanAndEncodeStringWithNSString:(NSString *)value;
+ (void)escapeContolSequencesIfNecessaryWithJavaIoWriter:(JavaIoWriter *)w
                                            withNSString:(NSString *)value;
+ (void)writeQuotedStringIfNecessaryWithJavaIoWriter:(JavaIoWriter *)w
                                        withNSString:(NSString *)value
                                 withRAREUTCharArray:(RAREUTCharArray *)ca;
+ (NSString *)getRateWithRAREUTJSONObject:(RAREUTJSONObject *)rate
                      withRAREUTCharArray:(RAREUTCharArray *)ca;
+ (NSString *)getRateOrRatioWithRAREUTJSONObject:(RAREUTJSONObject *)rate
                                    withNSString:(NSString *)firstKey
                                    withNSString:(NSString *)secondKey
                                    withNSString:(NSString *)sep
                                     withBoolean:(BOOL)includeUnits
                             withRAREUTCharArray:(RAREUTCharArray *)ca;
+ (NSString *)getRangeWithRAREUTJSONObject:(RAREUTJSONObject *)range
                               withBoolean:(BOOL)includeUnits
                       withRAREUTCharArray:(RAREUTCharArray *)ca;
+ (ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)getMedicalCodeWithRAREUTJSONObject:(RAREUTJSONObject *)o;
+ (ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)getMedicalCodeWithRAREUTJSONArray:(RAREUTJSONArray *)a;
+ (NSString *)getCodableConceptWithRAREUTJSONArray:(RAREUTJSONArray *)a
                                      withNSString:(NSString *)field
                               withJavaUtilHashMap:(JavaUtilHashMap *)map;
+ (NSString *)getLocationParentNameWithNSString:(NSString *)id_
                                        withInt:(int)backCount;
@end

@interface ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode : NSObject {
 @public
  NSString *hl7Code_;
  NSString *snomedCode_;
  NSString *loincCode_;
  NSString *hl7Display_;
  NSString *loincDisplay_;
  NSString *snomedDisplay_;
  NSString *text_;
}

- (id)initWithNSString:(NSString *)loincCode
          withNSString:(NSString *)snomedCode;
- (id)initWithNSString:(NSString *)loincCode
          withNSString:(NSString *)snomedCode
          withNSString:(NSString *)loincDisplay
          withNSString:(NSString *)snomedDisplay;
- (BOOL)isOneOfWithJavaUtilSet:(id<JavaUtilSet>)codes;
- (id)initWithNSString:(NSString *)hl7Code
          withNSString:(NSString *)snomedCode
          withNSString:(NSString *)loincCode
          withNSString:(NSString *)hl7Display
          withNSString:(NSString *)loincDisplay
          withNSString:(NSString *)snomedDisplay;
- (NSString *)getBestCode;
- (void)resolveHL7DisplayWithJavaUtilHashMap:(JavaUtilHashMap *)codeMap;
- (NSString *)getBestText;
- (void)copyAllFieldsTo:(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode *)other;
@end

J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, hl7Code_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, snomedCode_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, loincCode_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, hl7Display_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, loincDisplay_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, snomedDisplay_, NSString *)
J2OBJC_FIELD_SETTER(ComSparsewareBellavistaExternalFhirFHIRUtils_MedicalCode, text_, NSString *)

#endif // _ComSparsewareBellavistaExternalFhirFHIRUtils_H_