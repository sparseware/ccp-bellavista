//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/CardStackCollectionActionListener.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVCardStackCollectionActionListener_H_
#define _CCPBVCardStackCollectionActionListener_H_

@class RAREActionEvent;
@protocol JavaUtilCollection;
@protocol JavaUtilList;

#import "JreEmulation.h"
#include "com/appnativa/rare/ui/event/iActionListener.h"

@interface CCPBVCardStackCollectionActionListener : NSObject < RAREiActionListener > {
 @public
  NSString *title_;
  id<JavaUtilList> items_;
  int column_;
}

- (id)initWithNSString:(NSString *)title
withJavaUtilCollection:(id<JavaUtilCollection>)items
               withInt:(int)column;
- (void)actionPerformedWithRAREActionEvent:(RAREActionEvent *)e;
- (void)copyAllFieldsTo:(CCPBVCardStackCollectionActionListener *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVCardStackCollectionActionListener, title_, NSString *)
J2OBJC_FIELD_SETTER(CCPBVCardStackCollectionActionListener, items_, id<JavaUtilList>)

typedef CCPBVCardStackCollectionActionListener ComSparsewareBellavistaCardStackCollectionActionListener;

#endif // _CCPBVCardStackCollectionActionListener_H_
