//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: com/sparseware/bellavista/external/ActionLinkEx.java
//
//  Created by decoteaud on 3/14/16.
//

#ifndef _CCPBVActionLinkEx_H_
#define _CCPBVActionLinkEx_H_

@class JavaNetURL;
@class RARESPOTLink;
@class RARESPOTViewer;
@class SPOTPrintableString;
@protocol RAREUTiURLResolver;
@protocol RAREiURLConnection;

#import "JreEmulation.h"
#include "com/appnativa/rare/net/ActionLink.h"

@interface CCPBVActionLinkEx : RAREActionLink {
 @public
  int linkedIndex_;
  id linkedData_;
}

- (id)init;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
          withRAREiURLConnection:(id<RAREiURLConnection>)connection;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
                withRARESPOTLink:(RARESPOTLink *)link;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
         withSPOTPrintableString:(SPOTPrintableString *)url;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
                    withNSString:(NSString *)url;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
                    withNSString:(NSString *)url
                    withNSString:(NSString *)type;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
                  withJavaNetURL:(JavaNetURL *)url;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
                  withJavaNetURL:(JavaNetURL *)url
                    withNSString:(NSString *)type;
- (id)initWithRAREUTiURLResolver:(id<RAREUTiURLResolver>)context
              withRARESPOTViewer:(RARESPOTViewer *)cfg;
- (id)initWithNSString:(NSString *)url;
- (id)initWithNSString:(NSString *)data
          withNSString:(NSString *)type;
- (id)initWithJavaNetURL:(JavaNetURL *)url;
- (id)initWithJavaNetURL:(JavaNetURL *)url
            withNSString:(NSString *)type;
- (id)getLinkedData;
- (void)setLinkedDataWithId:(id)linkedData;
- (int)getLinkedIndex;
- (void)setLinkedIndexWithInt:(int)linkedIndex;
- (void)copyAllFieldsTo:(CCPBVActionLinkEx *)other;
@end

J2OBJC_FIELD_SETTER(CCPBVActionLinkEx, linkedData_, id)

typedef CCPBVActionLinkEx ComSparsewareBellavistaExternalActionLinkEx;

#endif // _CCPBVActionLinkEx_H_
