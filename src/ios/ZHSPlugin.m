/********* ZHSPlugin.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>


@interface ZHSPlugin : CDVPlugin {
  // Member variables go here.
}

- (void)printPdf:(CDVInvokedUrlCommand*)command;

@end

@implementation ZHSPlugin

- (void)printPdf:(CDVInvokedUrlCommand*)command
{
    //NSString* str = @"http://app.chixm.com/test/123.pdf";
    NSString* str = [command.arguments objectAtIndex:0];
    str = [str stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    NSURL *URL = [NSURL URLWithString:(NSString*)str];
    UIPrintInteractionController *pic = [UIPrintInteractionController sharedPrintController];
    if  (pic && [UIPrintInteractionController canPrintURL:URL] ) {
        UIPrintInfo *printInfo = [UIPrintInfo printInfo];
        printInfo.outputType = UIPrintInfoOutputGeneral;
        NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
        printInfo.jobName = [infoDictionary objectForKey:@"CFBundleDisplayName"];
        printInfo.duplex = UIPrintInfoDuplexLongEdge;
        pic.printInfo = printInfo;
        //pic.showsPageRange = YES;
        pic.printingItem = URL;
        void (^completionHandler)(UIPrintInteractionController *, BOOL, NSError *) =
        ^(UIPrintInteractionController *pic, BOOL completed, NSError *error) {
            
            if (!completed && error)
                NSLog(@"FAILED! due to error in domain %@ with error code %ld",
                      error.domain, (long)error.code);
        };
        [pic presentAnimated:YES completionHandler:completionHandler];
    }
}

@end
