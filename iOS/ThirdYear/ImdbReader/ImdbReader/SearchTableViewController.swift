//
//  SearchTableViewController.swift
//  ImdbReader
//
//  This class defines the tableviewcontroller for all searchresults.
//
//  Created by Anders Mikkelsen on 01/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit
import CoreData
import Alamofire
import AlamofireImage

class SearchTableViewController: UITableViewController {
    @IBOutlet var searchTableView: UITableView!
    
    var managedContext : NSManagedObjectContext?
    var searchResult : [[ String : String ]]?

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        managedContext = appDelegate.managedObjectContext
    }
    
    override func viewDidAppear(animated: Bool) {
        let userDefaults = NSUserDefaults.standardUserDefaults()
        
        guard userDefaults.boolForKey("firstSearch") else {
            userDefaults.setBool(true, forKey: "firstSearch")
            doInputAlert(NSLocalizedString("DoASeach", comment: "Title for the do a search alert"),
                message: NSLocalizedString("DoASearchInstructions", comment: "Instructions for first search"))
            return
        }
    }

    // MARK: TableViewDataSource

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.searchResult?.count ?? 0
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 90
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("searchCell", forIndexPath: indexPath) as! ImdbTableViewCell
        
        if let entity = isImdbEntityAlreadyPersisted(indexPath.row) {
            cell.setCellValues(entity, image: ImdbEntity.fetchImage(entity))
            cell.accessoryType = UITableViewCellAccessoryType.Checkmark
        } else {
            cell.setCellValues(self.searchResult![indexPath.row])
        }
        
        return cell
    }
    
    private func isImdbEntityAlreadyPersisted(entityIndex: Int) -> ImdbEntity? {
        let entity = self.searchResult![entityIndex]
        let entityTitle = entity["Title"]
        
        let fetchRequest = NSFetchRequest(entityName: (entity["Type"]?.capitalizedString)!)
        fetchRequest.predicate = NSPredicate(format: "title = %s", argumentArray: [entityTitle!])
        
        do {
            let results = try managedContext!.executeFetchRequest(fetchRequest) as! [ImdbEntity]
            
            if results.count > 0 {
                return results.first
            }
            
            return nil
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
        }
        
        return nil
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        guard let cell = tableView.cellForRowAtIndexPath(indexPath) as? ImdbTableViewCell else {
            print("Cell not found!")
            return
        }
        
        let entity = self.searchResult![indexPath.row]
        
        switch cell.accessoryType {
        case .Checkmark:
            if ImdbEntity.destroyImdbEntityAsHash(entity, managedContext: managedContext!) {
                cell.accessoryType = UITableViewCellAccessoryType.None
            }
        case .None:
            guard let cellImage = cell.posterImageView.image else {
                print("Could not fetch image from cell")
                return
            }
            
            ImdbEntity.persistImdbEntity(entity, image: cellImage, managedContext: managedContext!)
            cell.accessoryType = UITableViewCellAccessoryType.Checkmark
        default:
            print("Unknown accessorytype on cell...")
        }
    }

    // MARK: Actions
    
    @IBAction func unwindToSearch(unwindSegue: UIStoryboardSegue) {
        tableView.reloadData()
    }
}
