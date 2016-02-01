//
//  MyImdbItemsTableViewController.swift
//  ImdbReader
//
//  This class defines the Masterviewcontroller which displays all entites stored.
//
//  Created by Anders Mikkelsen on 01/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit
import CoreData
import Alamofire
import AlamofireImage

class MyImdbItemsTableViewController: UITableViewController, UISearchBarDelegate, NSFetchedResultsControllerDelegate {
    var detailViewController: ImdbEntityDetailViewController? = nil
    var managedObjectContext: NSManagedObjectContext? = nil
    var filteredImdbEntities: [ImdbEntity]?
    
    @IBOutlet weak var genreLabel: UILabel!
    @IBOutlet weak var genreViewLabel: UILabel!
    @IBOutlet weak var searchBar: UISearchBar!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.navigationItem.leftBarButtonItem = self.editButtonItem()
        
        if let split = self.splitViewController {
            let controllers = split.viewControllers
            self.detailViewController = (controllers[controllers.count - 1] as! UINavigationController).topViewController as? ImdbEntityDetailViewController
        }
    }
    
    override func viewWillAppear(animated: Bool) {
        self.clearsSelectionOnViewWillAppear = self.splitViewController!.collapsed
        super.viewWillAppear(animated)
        
        setFavouriteGenre(self.fetchedResultsController.fetchedObjects)
    }
    
    override func viewDidAppear(animated: Bool) {
        if self.fetchedResultsController.fetchedObjects?.count == 0 {
            doInputAlert(NSLocalizedString("NothingStored", comment: "When nothing is stored in user lib."),
                message: NSLocalizedString("ClickSearchToStart", comment: "Notice to user when nothing is stored"))
        }
    }
    
    // MARK: FavouriteGenre
    
    private func setFavouriteGenre(entities: [AnyObject]?) {
        genreViewLabel.text = Genre.calculateFavouriteGenre(entities)
    }
    
    // MARK: Segues
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "showDetail" {
            if let indexPath = self.tableView.indexPathForSelectedRow {
                let object = self.fetchedResultsController.objectAtIndexPath(indexPath)
                let controller = (segue.destinationViewController as! UINavigationController).topViewController as! ImdbEntityDetailViewController
                
                controller.detailItem = object as? ImdbEntity
                controller.navigationItem.leftBarButtonItem = self.splitViewController?.displayModeButtonItem()
                controller.navigationItem.leftItemsSupplementBackButton = true
            }
        }
    }
    
    // MARK: Table View
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        guard let _ = filteredImdbEntities else {
            return self.fetchedResultsController.sections?.count ?? 0
        }
        
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let filteredEntities = filteredImdbEntities else {
            let sectionInfo = self.fetchedResultsController.sections![section]
            
            return sectionInfo.numberOfObjects
        }
        
        return filteredEntities.count
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 90
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("myItemsCell", forIndexPath: indexPath) as! ImdbTableViewCell
        self.configureCell(cell, atIndexPath: indexPath)
        
        return cell
    }
    
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        switch editingStyle {
        case .Delete:
            let context = self.fetchedResultsController.managedObjectContext
            context.deleteObject(self.fetchedResultsController.objectAtIndexPath(indexPath) as! NSManagedObject)
            
            do {
                try context.save()
            } catch let error as NSError {
                print("Unresolved error \(error), \(error.userInfo)")
            }
        default:
            print("Unsupported editing")
        }
    }
    
    private func configureCell(cell: ImdbTableViewCell, atIndexPath indexPath: NSIndexPath) {
        var imdbEntity: ImdbEntity
        
        if let filteredEntities = filteredImdbEntities {
            imdbEntity = filteredEntities[indexPath.row]
        } else {
            guard let object = self.fetchedResultsController.objectAtIndexPath(indexPath) as? ImdbEntity else {
                print("Not an imdbentity")
                return
            }
            
            imdbEntity = object
        }
        
        guard imdbEntity.posterUrl! != "N/A" else {
            cell.setCellValues(imdbEntity, image: UIImage(named: "imdb-logo")!)
            return
        }

        cell.setCellValues(imdbEntity, image: ImdbEntity.fetchImage(imdbEntity))
    }
    
    // MARK: SearchBar
    
    func searchBarTextDidBeginEditing(searchBar: UISearchBar) {
        self.tableView.reloadData()
    }
    
    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {
        doFilter()
    }
    
    func searchBarCancelButtonClicked(searchBar: UISearchBar) {
        searchBar.text = ""
        doFilter()
        searchBar.endEditing(true)
    }
    
    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        doFilter()
        searchBar.endEditing(true)
    }
    
    private func doFilter() {
        self.filteredImdbEntities = nil
        updateSearchResults()
        self.tableView.reloadData()
    }
    
    private func updateSearchResults() {
        guard let allEntities = self.fetchedResultsController.fetchedObjects as? [ImdbEntity] else {
            print("No searchable entities")
            return
        }
        
        guard let searchString = self.searchBar.text else {
            print("Searchstring not availiable")
            return
        }
        
        guard searchString.characters.count > 0 else {
            print("No searchstring, no filtering...")
            return
        }

        self.filteredImdbEntities = allEntities.filter { (entity) -> Bool in
            return entity.title!.lowercaseString.rangeOfString(searchString.lowercaseString) != nil
        }

        setFavouriteGenre(self.filteredImdbEntities)
    }
    
    // MARK: NSFetchedResultsController
    
    var fetchedResultsController: NSFetchedResultsController {
        guard _fetchedResultsController == nil else {
            return _fetchedResultsController!
        }
        
        let fetchRequest = NSFetchRequest()
        let imdbEntity = NSEntityDescription.entityForName("ImdbEntity", inManagedObjectContext: self.managedObjectContext!)
        
        fetchRequest.entity = imdbEntity
        fetchRequest.fetchBatchSize = 20
        fetchRequest.sortDescriptors = [NSSortDescriptor(key: "title", ascending: false)]
        
        let newFetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: self.managedObjectContext!, sectionNameKeyPath: nil, cacheName: "Master")
        newFetchedResultsController.delegate = self
        _fetchedResultsController = newFetchedResultsController
        
        do {
            try _fetchedResultsController!.performFetch()
        } catch let error as NSError {
            print("Unresolved error \(error), \(error.userInfo)")
            abort()
        }
        
        return _fetchedResultsController!
    }

    var _fetchedResultsController: NSFetchedResultsController? = nil
    
    func controllerWillChangeContent(controller: NSFetchedResultsController) {
        self.tableView.beginUpdates()
    }
    
    func controller(controller: NSFetchedResultsController, didChangeObject anObject: AnyObject, atIndexPath indexPath: NSIndexPath?, forChangeType type: NSFetchedResultsChangeType, newIndexPath: NSIndexPath?) {
        switch type {
        case .Insert:
            tableView.insertRowsAtIndexPaths([newIndexPath!], withRowAnimation: .Fade)
        case .Delete:
            tableView.deleteRowsAtIndexPaths([indexPath!], withRowAnimation: .Fade)
        case .Update:
            self.configureCell(tableView.cellForRowAtIndexPath(indexPath!) as! ImdbTableViewCell, atIndexPath: indexPath!)
        default:
            print("Unsupported")
        }
        
        setFavouriteGenre(self.fetchedResultsController.fetchedObjects)
    }
    
    func controllerDidChangeContent(controller: NSFetchedResultsController) {
        self.tableView.endUpdates()
    }
    
    // MARK: Actions
    
    @IBAction func unwindToMaster(unwindSegue: UIStoryboardSegue) {
        self.editing = false
    }
    
    // MARK: Touch
    
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.searchBar.endEditing(true)
    }
}
