//
//  DisplayRatingsViewController.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/18/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit
import FirebaseDatabase

class DisplayRatingsViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        configureratings()
        profPic.image = picture
        profName.text = name
        

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func configureratings() {
        let dbRef = Database.database().reference()
        dbRef.child("ratings").observeSingleEvent(of: .value) { (snapshot) in
            if snapshot.hasChild(self.profName.text!.replacingOccurrences(of: ".", with: ",")) {
                let overalldic = snapshot.value as! [String:AnyObject]
           
                let current = overalldic[self.profName.text!.replacingOccurrences(of: ".", with: ",")] as! [String:AnyObject]
      
                let kindnesscount = current["kindness"] as! Int
                let passioncount = current["passion"] as! Int
                let wisdomcount = current["wisdom"] as! Int
                let overallcount = current["overall"] as! Int
                let totalcount = current["total"] as! Int
                
                if current.keys.contains("reviews") {
                    let r = current["reviews"] as! [String:[String:AnyObject]]
                    for (_,v) in r {
                        self.reviewdicts.append(v)
                        
                    }
                    self.reviewcounts = r.count
                }
                
                let overallperc = Int(100*overallcount/totalcount)
                self.overall.text = String(overallperc) + "%"
                
                if overallperc >= 50 {
                    self.emoji.image = #imageLiteral(resourceName: "happy")
                } else {
                    self.emoji.image = #imageLiteral(resourceName: "sad")
                }
                
                let mercyperc = Int(100*kindnesscount/totalcount)
                
                self.mercyPercent.text = String(mercyperc) + "%"
                if mercyperc >= 50 {
                    self.mercyEmoji.image = #imageLiteral(resourceName: "happy")
                } else {
                    self.mercyEmoji.image = #imageLiteral(resourceName: "sad")
                }
                
                let passionperc = Int(100*passioncount/totalcount)
                self.passionPercent.text = String(passionperc) + "%"
                if passionperc >= 50 {
                    self.passionEmoji.image = #imageLiteral(resourceName: "happy")
                } else {
                    self.passionEmoji.image = #imageLiteral(resourceName: "sad")
                }
                
                let wisdomperc = Int(100*wisdomcount/totalcount)
                print(wisdomcount)
                
                self.wisdomPercent.text = String(wisdomperc) + "%"
                if wisdomperc >= 50 {
                    self.wisdomEmoji.image = #imageLiteral(resourceName: "happy")
                } else {
                    self.wisdomEmoji.image = #imageLiteral(resourceName: "sad")
                }
                
                self.reviewsCounted.text = "Reviews Counted:  " + String(totalcount) + " "
                
                
                
                
            } else {
                self.reviewsCounted.text = "Reviews Counted:  0 "
                self.overall.text = "N/A"
                self.passionPercent.text = "N/A"
                self.mercyPercent.text = "N/A"
                self.wisdomPercent.text = "N/A"
                
                self.emoji.image = #imageLiteral(resourceName: "happy")
                self.passionEmoji.image = #imageLiteral(resourceName: "happy")
                self.mercyEmoji.image = #imageLiteral(resourceName: "happy")
                self.wisdomEmoji.image = #imageLiteral(resourceName: "happy")
                
            }
        }
        
    }
    
    
    
    var picture: UIImage?
    var name: String?
    var reviewcounts = 0
    var reviewdicts = [[String:AnyObject]]()

    
    @IBOutlet var overall: UILabel!
    @IBOutlet var emoji: UIImageView!
    
    @IBOutlet var profPic: UIImageView!
    
    @IBOutlet var profName: UILabel!
    
    @IBOutlet var mercyEmoji: UIImageView!
    
    @IBOutlet var mercyPercent: UILabel!
    
    @IBOutlet var passionEmoji: UIImageView!
    @IBOutlet var passionPercent: UILabel!
    @IBOutlet var wisdomEmoji: UIImageView!
    
    @IBOutlet var wisdomPercent: UILabel!
    
    @IBOutlet weak var reviewsCounted: UILabel!
    
    
    
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let identifier = segue.identifier {
            if identifier == "profToReview" {
                if let dest = segue.destination as? WriteReviewViewController {
                    dest.professor = name
                }
            }
            if identifier == "profToView" {
                if let dest = segue.destination as? ViewReviewViewController {
                    dest.professor = name
                    dest.reviewcounts = reviewcounts
                    dest.reviewdicts = reviewdicts
                }
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
