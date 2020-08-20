//
//  WriteReviewViewController.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/28/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit
import FirebaseDatabase
import Foundation
import FirebaseStorage

class WriteReviewViewController: UIViewController, UITextViewDelegate {
    var kindnessstatus: Int?
    var passionstatus: Int?
    var wisdomstatus: Int?
    var overallstatus: Int?
    var opreview = false
    var professor: String?
    
    
    @IBOutlet weak var kindnessup: UIButton!
    @IBOutlet weak var kindnessdown: UIButton!
    @IBOutlet weak var passionup: UIButton!
    @IBOutlet weak var passiondown: UIButton!
    @IBOutlet weak var wisdomup: UIButton!
    @IBOutlet weak var wisdomdown: UIButton!
    @IBOutlet weak var overallup: UIButton!
    @IBOutlet weak var overalldown: UIButton!
    
    @IBOutlet weak var reviewTextView: UITextView!
    
    @IBAction func kindnessuppressed(_ sender: Any) {
        if kindnessstatus != nil && kindnessstatus == 0 {
            kindnessdown.layer.borderWidth = 0
        }
        kindnessup.layer.borderWidth = 2
        kindnessup.layer.borderColor = UIColor.gray.cgColor
        kindnessstatus = 1
    }
    
    @IBAction func kindnessdonwpressed(_ sender: Any) {
        if kindnessstatus != nil && kindnessstatus == 1 {
            kindnessup.layer.borderWidth = 0
        }
        kindnessdown.layer.borderWidth = 2
        kindnessdown.layer.borderColor = UIColor.gray.cgColor
        kindnessstatus = 0
    }
    
    @IBAction func passionuppressed(_ sender: Any) {
        if passionstatus != nil && passionstatus == 0 {
            passiondown.layer.borderWidth = 0
        }
        passionup.layer.borderWidth = 2
        passionup.layer.borderColor = UIColor.gray.cgColor
        passionstatus = 1
    }
    
    @IBAction func passiondownpressed(_ sender: Any) {
        if passionstatus != nil && passionstatus == 1 {
            passionup.layer.borderWidth = 0
        }
        passiondown.layer.borderWidth = 2
        passiondown.layer.borderColor = UIColor.gray.cgColor
        passionstatus = 0
    }
    
    
    @IBAction func wisdomuppressed(_ sender: Any) {
        if wisdomstatus != nil && wisdomstatus == 0 {
            wisdomdown.layer.borderWidth = 0
        }
        wisdomup.layer.borderWidth = 2
        wisdomup.layer.borderColor = UIColor.gray.cgColor
        wisdomstatus = 1
    }
    
    @IBAction func wisdomdownpressed(_ sender: Any) {
        if wisdomstatus != nil && wisdomstatus == 1 {
            wisdomup.layer.borderWidth = 0
        }
        wisdomdown.layer.borderWidth = 2
        wisdomdown.layer.borderColor = UIColor.gray.cgColor
        wisdomstatus = 0
    }
    
    
    @IBAction func overalluppressed(_ sender: Any) {
        if overallstatus != nil && overallstatus == 0 {
            overalldown.layer.borderWidth = 0
        }
        overallup.layer.borderWidth = 2
        overallup.layer.borderColor = UIColor.gray.cgColor
        overallstatus = 1
    }
    
    @IBAction func overalldownpressed(_ sender: Any) {
        if overallstatus != nil && overallstatus == 1 {
            overallup.layer.borderWidth = 0
        }
        overalldown.layer.borderWidth = 2
        overalldown.layer.borderColor = UIColor.gray.cgColor
        overallstatus = 0
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if textView == self.reviewTextView {
            self.opreview = true
        }
    }
    
    @IBAction func submitPressed(_ sender: Any) {
        if kindnessstatus == nil || wisdomstatus == nil || overallstatus == nil || passionstatus == nil {
            let alertController = UIAlertController(title: "Rating not completed.", message: "Please rate all fields.", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alertController.addAction(defaultAction)
            self.present(alertController, animated: true, completion: nil)
        } else {
            var postdict = [String:Any]()
            
            if self.reviewTextView.text != "" {
                let formatter = DateFormatter()
                formatter.dateFormat = "MM/dd/yyyy"
                let now = Date()
                let dateString = formatter.string(from:now)
                
                postdict = ["overall": overallstatus!, "date": dateString, "content": self.reviewTextView.text!] as [String : Any]
            }
            
            let dbRef = Database.database().reference()
            dbRef.child("ratings").observeSingleEvent(of: .value) { (snapshot) in
                if snapshot.hasChild(self.professor!.replacingOccurrences(of: ".", with: ",")) {
                    let overalldict = snapshot.value as! [String:AnyObject]
                    let current = overalldict[self.professor!.replacingOccurrences(of: ".", with: ",")]!
                    print(current)
                    let kindness = current["kindness"] as! Int + self.kindnessstatus!
                    let passion = current["passion"] as! Int + self.passionstatus!
                    let wisdom = current["wisdom"] as! Int + self.wisdomstatus!
                    let overall = current["overall"] as! Int + self.overallstatus!
                    let total = current["total"] as! Int + 1
                    dbRef.child("ratings").child(self.professor!.replacingOccurrences(of: ".", with: ",")).updateChildValues(["overall": overall, "passion": passion, "wisdom": wisdom, "kindness": kindness, "total": total])
                    if self.opreview {
                        dbRef.child("ratings").child(self.professor!.replacingOccurrences(of: ".", with: ",")).child("reviews").childByAutoId().updateChildValues(postdict)
                    }
                } else {
                    dbRef.child("ratings").child(self.professor!.replacingOccurrences(of: ".", with: ",")).setValue(["overall": self.overallstatus!, "passion": self.passionstatus!, "wisdom": self.wisdomstatus!, "kindness": self.kindnessstatus!, "total": 1])
                    if self.opreview {
                    dbRef.child("ratings").child(self.professor!.replacingOccurrences(of: ".", with: ",")).child("reviews").childByAutoId().setValue(postdict)
                    }
                }
            }
            
            
            
            
            let alertController = UIAlertController(title: "Congratulations!", message: "You have successfully rated a professor.", preferredStyle: .alert)
            alertController.addAction(UIAlertAction(title: "Okay", style: .default, handler:
                {
                    [unowned self] (action) -> Void in
                    self.performSegue(withIdentifier: "rateToSelect", sender: self)
            }))
            if presentedViewController == nil {
                self.present(alertController, animated: true, completion: nil)
            } else{
                self.dismiss(animated: false) { () -> Void in
                    self.present(alertController, animated: true, completion: nil)
                }
            }
            
            
            
          
        }
        
        
  
        
        
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Rate Prof " + self.professor!
        self.reviewTextView.delegate = self
        reviewTextView.layer.borderWidth = 2
        reviewTextView.layer.borderColor = UIColor.gray.cgColor
        definesPresentationContext = true
        

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
