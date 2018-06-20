//
//  ReviewTableViewCell.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/30/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit

class ReviewTableViewCell: UITableViewCell {

    @IBOutlet weak var thumb: UIImageView!
    
    @IBOutlet weak var dateposted: UILabel!
    
    @IBOutlet weak var content: UITextView!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
