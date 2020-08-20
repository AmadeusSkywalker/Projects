//
//  ViewReviewViewController.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/30/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit

class ViewReviewViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if reviewcounts == 0 {
            return 1
        } else {
            return reviewcounts
        }
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = reviewTableView.dequeueReusableCell(withIdentifier: "indivreview", for: indexPath) as! ReviewTableViewCell
        if reviewcounts == 0 {
            cell.thumb.image = #imageLiteral(resourceName: "up")
            cell.dateposted.text = "N/A"
            cell.content.text = "No Reviews Yet."
        } else {
            let sorteddicts = Array(reviewdicts).sorted(by: {$0["date"] as! String > $1["date"] as! String})
            let selected = sorteddicts[indexPath.row]
            if selected["overall"] as! Int == 1 {
                cell.thumb.image = #imageLiteral(resourceName: "up")
            } else {
                cell.thumb.image = #imageLiteral(resourceName: "down")
            }
            cell.dateposted.text = selected["date"] as? String
            cell.content.text = selected["content"] as? String
        }
        cell.content.layer.borderWidth = 2.5
        cell.content.layer.borderColor = UIColor.gray.cgColor
        return cell
    }
    
    
    

    override func viewDidLoad() {
        super.viewDidLoad()
        reviewTableView.delegate = self
        reviewTableView.dataSource = self
        self.title = "Reviews for Prof " + self.professor!

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    var professor: String?
    var reviewdicts = [[String:AnyObject]]()
    var reviewcounts = 0
    
    @IBOutlet weak var reviewTableView: UITableView!
    
    
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
