//
//  main.m
//  XCodeApplication
//

#import <UIKit/UIKit.h>

#import "AppDelegate.h"
#import "RAREAPApplication.h"
#import "RareTOUCH_library.h"

int main(int argc, char * argv[])
{
  @autoreleasepool {
    [[RareTOUCH_library class] initializeLibrary];
    return UIApplicationMain(argc, argv, NSStringFromClass([RAREAPApplication class]), NSStringFromClass([AppDelegate class]));
  }
}
