//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/ListPager.java
//
//  Created by decoteaud on 2/17/16.
//

#ifndef _CCPBVListPager_H_
#define _CCPBVListPager_H_

@class JavaUtilArrayList;
@class RAREActionLink;

#import "JreEmulation.h"

@interface CCPBVListPager : NSObject {
 @public
  JavaUtilArrayList *stack_;
  RAREActionLink *next__;
}

- (id)init;
- (RAREActionLink *)next;
- (RAREActionLink *)previous;
+ (RAREActionLink *)createPagingLinkWithRAREActionLink:(RAREActionLink *)source
                                          withNSString:(NSString *)href;
- (void)setNextWithRAREActionLink:(RAREActionLink *)next;
- (void)clear;
- (void)addWithRAREActionLink:(RAREActionLink *)link;
- (BOOL)hasNext;
- (BOOL)hasPrevious;
- (JavaUtilArrayList *)getStack;
- (void)copyAllFieldsTo:(CCPBVListPager *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVListPager, stack_, JavaUtilArrayList *)
J2OBJC_FIELD_SETTER(CCPBVListPager, next__, RAREActionLink *)

typedef CCPBVListPager ComSparsewareBellavistaListPager;

#endif // _CCPBVListPager_H_
