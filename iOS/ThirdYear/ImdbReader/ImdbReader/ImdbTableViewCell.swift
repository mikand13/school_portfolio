//
//  SearchResultTableViewCell.swift
//  ImdbReader
//
//  This class defines the custom cell view for showing entities on the masterview and the searchresults.
//
//  Created by Anders Mikkelsen on 10/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage

class ImdbTableViewCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var yearLabel: UILabel!
    @IBOutlet weak var posterImageView: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

    func setCellValues(entity: ImdbEntity, image: UIImage?) {
        titleLabel.text = entity.title
        yearLabel.text = entity.year
        
        guard let img = image else {
            print("nil image")
            return
        }
        
        posterImageView.image = img
    }
    
    func setCellValues(entity: [String : String]) {
        accessoryType = UITableViewCellAccessoryType.None
        titleLabel.text = entity["Title"]
        yearLabel.text = entity["Year"]
        
        if entity["Poster"]! != "N/A" {
            Alamofire.request(.GET, entity["Poster"]!)
                .responseImage { response in
                    guard let image = response.result.value else {
                        print("Could not fetch image for...")
                        return
                    }
                    
                    self.posterImageView.image = image
            }
        } else {
            posterImageView.image = UIImage(named: "imdb-logo")
        }
    }
}
