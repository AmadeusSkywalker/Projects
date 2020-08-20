//
//  DisplayProfessorsViewController.swift
//  RottenProfessors
//
//  Created by Jingjing Jia on 4/18/18.
//  Copyright © 2018 IOS Decal. All rights reserved.
//

import UIKit


class DisplayProfessorsViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UISearchControllerDelegate, UISearchBarDelegate, UISearchResultsUpdating {
    
    
    
    var cachedImages: [String:UIImage] = [:]
    var searchActive : Bool = false
    var filtered:[String] = []
    let searchController = UISearchController(searchResultsController: nil)
    
    @IBAction func unwindToSelect(segue:UIStoryboardSegue) { }
 
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if searchActive {
            return filtered.count
        }
        return FacultyList.facultyDict.count
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "DisplayCell", for: indexPath) as! DisplayProfessorsCollectionViewCell
        var name = ""
        
        if searchActive {
            name = filtered.sorted()[indexPath.row]
            
        } else {
            name = Array(FacultyList.facultyDict.keys.sorted())[indexPath.row]

        }
        cell.profName.text = name
        
        
        
        if let image = cachedImages[name] {
            cell.profPic.image = image
        } else {
            
            cell.profPic.image = nil
            
            let stringurl = "https://www2.eecs.berkeley.edu/Faculty/Photos/Homepages/" + FacultyList.facultyDict[name]! + ".jpg"
            
            let url = URL(string: stringurl)!
            let session = URLSession(configuration: .default)
            let downloadPicTask = session.dataTask(with: url) { (data, response, error) in
                if let e = error {
                    print("Error downloading picture: \(e)")
                } else {
                    if let _ = response as? HTTPURLResponse {
                        if let imageData = data {
                            let image = UIImage(data: imageData)
                            DispatchQueue.main.async {
                                self.cachedImages[name] = image
                                cell.profPic.image = image // UIImage(data: imageData)
                            }
                        } else {
                            print("Couldn't get image: Image is nil")
                        }
                    } else {
                        print("Couldn't get response code")
                    }
                }
            }
            downloadPicTask.resume()
        }
        
        return cell
    }
    
    @IBOutlet var ProfessorCollection: UICollectionView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        ProfessorCollection.delegate = self
        ProfessorCollection.dataSource = self
        self.searchController.searchResultsUpdater = self
        self.searchController.delegate = self
        self.searchController.searchBar.delegate = self
        
        self.searchController.hidesNavigationBarDuringPresentation = false
        self.searchController.dimsBackgroundDuringPresentation = true
        self.searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "Search for a professor ..."
        searchController.searchBar.sizeToFit()
        
        searchController.searchBar.becomeFirstResponder()
        
        self.navigationItem.titleView = searchController.searchBar
    }
    
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false
        
    }
    
    func updateSearchResults(for searchController: UISearchController)
    {
        let searchString = searchController.searchBar.text
        
        filtered = FacultyList.facultyDict.keys.filter({ (item) -> Bool in
            let countryText: NSString = item as NSString
            
            return (countryText.range(of: searchString!, options: NSString.CompareOptions.caseInsensitive).location) != NSNotFound
        })
        
        ProfessorCollection.reloadData()
        
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchActive = true
        ProfessorCollection.reloadData()
    }
    
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false
        ProfessorCollection.reloadData()
    }
    
    func searchBarBookmarkButtonClicked(_ searchBar: UISearchBar) {
        if !searchActive {
            searchActive = true
            ProfessorCollection.reloadData()
        }
        
        searchController.searchBar.resignFirstResponder()
    }
    
    
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = self.collectionView(ProfessorCollection, cellForItemAt: indexPath) as! DisplayProfessorsCollectionViewCell
        
        chosenProfName = cell.profName.text
        chosenProfPic = cell.profPic.image
        
        self.performSegue(withIdentifier: "selecttodisplay", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let identifier = segue.identifier {
            if identifier == "selecttodisplay" {
                if let dest = segue.destination as? DisplayRatingsViewController {
                    dest.name = chosenProfName
                    dest.picture = chosenProfPic
                }
            }
        }
    }
    
    var chosenProfPic: UIImage?
    var chosenProfName: String?

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
