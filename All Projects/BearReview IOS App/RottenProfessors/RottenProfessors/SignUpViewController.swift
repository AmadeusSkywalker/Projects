//
//  SignUpViewController.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/18/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit
import FirebaseAuth

class SignUpViewController: UIViewController, UITextFieldDelegate {
    
    var userEmail = ""
    var userName = ""
    var userPassword = ""
    var userVeriPassword = ""
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.usernameTextField.delegate = self
        self.emailTextField.delegate = self
        self.passwordTextField.delegate = self
        self.passwordVeriTextField.delegate = self

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    @IBAction func signupPressed(_ sender: Any) {
        guard let email = emailTextField.text else { return }
        guard let password = passwordTextField.text else { return }
        guard let name = usernameTextField.text else { return }
        guard let verifiedPassword = passwordVeriTextField.text else { return }
        if email == "" || password == "" || name == "" || verifiedPassword == "" {
            let alertController = UIAlertController(title: "Form Error.", message: "Please fill in form completely.", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alertController.addAction(defaultAction)
            present(alertController, animated: true, completion: nil)
            
        }else {
            Auth.auth().createUser(withEmail: email, password: password) { (user, error) in
                if error == nil {
                    let changeReq = user!.createProfileChangeRequest()
                    changeReq.displayName = name
                    changeReq.commitChanges(completion:
                        { (err) in
                    })
                    let alertController = UIAlertController(title: "Congratulations!", message: "You have successfully signed up", preferredStyle: .alert)
                    alertController.addAction(UIAlertAction(title: "Okay", style: .default, handler:
                        {
                            [unowned self] (action) -> Void in
                            self.performSegue(withIdentifier: "signupToSelect", sender: self)
                    }))
                    self.present(alertController, animated: true, completion: nil)
                } else if password != verifiedPassword {
                    let alertController = UIAlertController(title: "Verification Error.", message: "The two passwords do not match.", preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.passwordVeriTextField.textColor = UIColor.red
                    self.present(alertController, animated: true, completion: nil)
                } else if email.range(of: "berkeley.edu") == nil {
                    let alertController = UIAlertController(title: "Email Address Error.", message: "Please use a berkeley Email address.", preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.present(alertController, animated: true, completion: nil)
                }
                
                
                else {
                    let alertController = UIAlertController(title: "Sign Up Error", message: error?.localizedDescription, preferredStyle: .alert)
                    let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                    alertController.addAction(defaultAction)
                    self.present(alertController, animated: true, completion: nil)
                }
            }
        }
        
        
        
    }
    
    @IBOutlet weak var passwordTextField: UITextField!
    
    @IBOutlet weak var passwordVeriTextField: UITextField!
    
    @IBOutlet weak var usernameTextField: UITextField!
    
    @IBOutlet weak var emailTextField: UITextField!
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField == self.emailTextField {
            if textField.text != nil {
                self.userEmail = textField.text!
            }
        } else if textField == self.passwordTextField {
            if textField.text != nil {
                self.userPassword = textField.text!
            }
        } else if textField == self.usernameTextField {
            if textField.text != nil {
                self.userName = textField.text!
            }
        } else if textField == self.passwordVeriTextField {
            if textField.text != nil {
                self.userVeriPassword = textField.text!
            }
        }
    }
    
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
