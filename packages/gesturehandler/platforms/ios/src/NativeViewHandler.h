//
//  NativeViewHandler.h
//  GestureHandler
//
//  Created by Krzysztof Magiera on 12/10/2017.
//  Copyright © 2017 Software Mansion. All rights reserved.
//

#import "GestureHandler.h"

@interface DummyGestureRecognizer : UIGestureRecognizer
@end

@interface NativeViewGestureHandler : GestureHandler
- (void)setShouldActivateOnStart:(Boolean)value;
- (void)setDisallowInterruption:(Boolean)value;
@end
