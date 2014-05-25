//
//  ViewController.h
//  Xcode
//
//  Created by Lorenzo Dalla Casa on 22/05/14.
//  Copyright (c) 2014 Lorenzo Dalla Casa. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController

@property (weak, nonatomic) IBOutlet UITextField *textField;

@property (weak, nonatomic) IBOutlet UITextView *textView;

- (IBAction)solve:(id)sender;

- (IBAction)dismissKeyboard:(id)sender;

@end
