//
//  ImdbEntity.swift
//  ImdbReader
//
//  This class defines the abstract entity which all imdbentities are based.
//
//  Created by Anders Mikkelsen on 02/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import Foundation
import CoreData
import Alamofire

@objc(ImdbEntity)
class ImdbEntity: NSManagedObject {
    // only used for init, will be purged after contextsave and gc
    var attributes : [String : String]!
    var managedContext : NSManagedObjectContext!
    
    convenience init?(type: String, attributes: [String : String], managedObjectContext: NSManagedObjectContext) {
        self.init(entity: NSEntityDescription.entityForName(type, inManagedObjectContext: managedObjectContext)!, insertIntoManagedObjectContext: managedObjectContext)
        
        self.attributes = attributes
        self.managedContext = managedObjectContext
        
        createCoreDataRelations()
        setAttributes()
    }
    
    override func prepareForDeletion() {
        guard let image = posterUrl else {
            print("No image to delete...")
            return
        }
        
        do {
            if image != "N/A" {
                let libraryDir = NSFileManager.defaultManager().URLsForDirectory(.LibraryDirectory, inDomains: .UserDomainMask)[0]
                let imagePath = libraryDir.URLByAppendingPathComponent(image)
                try NSFileManager.defaultManager().removeItemAtURL(imagePath)
            }
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
        }
    }
    
    private func createCoreDataRelations() {
        self.mutableSetValueForKey("directors").addObjectsFromArray(buildRelation("Director")!)
        self.mutableSetValueForKey("genres").addObjectsFromArray(buildRelation("Genre")!)
        self.mutableSetValueForKey("languages").addObjectsFromArray(buildRelation("Language")!)
        self.mutableSetValueForKey("writers").addObjectsFromArray(buildRelation("Writer")!)
        self.mutableSetValueForKey("countries").addObjectsFromArray(buildRelation("Country")!)

        self.mutableSetValueForKey("actors").addObjectsFromArray(
            Nameable.getOrCreateNameablesFromJSONString("Actor", imdbEntity: self, nameables: attributes["Actors"]!, managedContext: managedContext)!)
    }

    private func buildRelation(type: String) -> [AnyObject]? {
        return Nameable.getOrCreateNameablesFromJSONString(type, imdbEntity: self, nameables: attributes[type]!, managedContext: managedContext)
    }
    
    private func setAttributes() {
        title = attributes["Title"]
        imdbId = attributes["imdbID"]
        imdbRating = Double(attributes["imdbRating"]!)
        userRating = 0.0
        imdbVotes = parseImdbVotes(attributes["imdbVotes"]!)
        metascore = Int(attributes["Metascore"]!)
        plot = attributes["Plot"]
        posterUrl = attributes["Poster"]
        rated = attributes["Rated"]
        
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "dd-MM-yyyy"
        
        released = dateFormatter.dateFromString(attributes["Released"]!)
        runtimeMinutes = parseRuntime(attributes["Runtime"]!)
        awards = attributes["Awards"]
        year = attributes["Year"]
        review = NSLocalizedString("EmptyReview", comment: "Notice of empty reviewfield")
    }
    
    private func parseRuntime(value : String) -> Int {
        var newString = ""
        
        for c in value.characters {
            newString.append(c)
            
            if c == " " {
                break
            }
        }
        
        guard let number = NSNumberFormatter().numberFromString(newString) else {
            return 0
        }
        
        return Int((number.integerValue))
    }
    
    private func parseImdbVotes(value : String) -> Int {
        return value == "N/A" ? 0 : Int(value.stringByReplacingOccurrencesOfString(",", withString: ""))!
    }
    
    class func destroyImdbEntity(entity: ImdbEntity, managedContext: NSManagedObjectContext) -> Bool {
        do {
            managedContext.deleteObject(entity)
            try managedContext.save()
            
            return true
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
            
            return false
        }
    }
    
    class func destroyImdbEntityAsHash(entity: [String : String], managedContext: NSManagedObjectContext) -> Bool {
        guard let type = checkTypeGuard(entity) else {
            return false
        }
        
        guard let entityTitle = entity["Title"] else {
            print("Could not extract title")
            
            return false
        }
        
        let fetchRequest = NSFetchRequest(entityName: type)
        fetchRequest.predicate = NSPredicate(format: "title = %s", argumentArray: [entityTitle])
        
        do {
            for nsmo in try managedContext.executeFetchRequest(fetchRequest) as! [NSManagedObject] {
                managedContext.deleteObject(nsmo)
            }
            
            try managedContext.save()
            
            return true
        } catch let error as NSError {
            managedContext.rollback()
            print("Could not fetch \(error), \(error.userInfo)")
            
            return false
        }
    }
    
    class func persistImdbEntity(entity: [String : String], image: UIImage, managedContext: NSManagedObjectContext) {
        guard let imdbId = entity["imdbID"] else {
            print("Could not extract ImbdId")
            return
        }
        
        guard let type = checkTypeGuard(entity) else {
            print("Could not establish type")
            return
        }

        Alamofire.request(.GET, AppDelegate.imdbApiEndPoint, parameters: ["i" : imdbId]).validate()
            .responseData { response in
                guard response.result.isSuccess else {
                    print("JSONfetch failed")
                    return
                }
            }.responseJSON { response in
                guard let entityAsArray = response.result.value! as? [String : String] else {
                    print("Could not get json from response")
                    return
                }
                
                do {
                    try persistEntityFromEntityArray(type, image: image, entityAsArray: entityAsArray, managedContext: managedContext)
                    try managedContext.save()
                } catch let error as NSError  {
                    managedContext.rollback()
                    print("Could not save \(error), \(error.userInfo)")
                }
            }
    }
    
    private class func persistEntityFromEntityArray(type: String, image: UIImage, entityAsArray: [String : String], managedContext: NSManagedObjectContext) throws {
        let imdbEntity = try fetchCorrectEntity(type, attributes: entityAsArray, managedContext: managedContext)
        
        if imdbEntity.posterUrl! != "N/A" {
            imdbEntity.posterUrl = String(imdbEntity.posterUrl!.hashValue)
            
            guard let imageData = UIImagePNGRepresentation(image) else {
                return
            }
            
            let libraryDir = NSFileManager.defaultManager().URLsForDirectory(.LibraryDirectory, inDomains: .UserDomainMask)[0]
            let imagePath = libraryDir.URLByAppendingPathComponent(imdbEntity.posterUrl!)
            imageData.writeToFile(imagePath.path!, atomically: true)
        }
    }
    
    internal class func checkTypeGuard(entity: [String : String]) -> String? {
        guard let type = entity["Type"]?.capitalizedString else {
            print("Could not extract type")
            
            return nil
        }
        
        return type
    }
    
    private class func fetchCorrectEntity(entityType: String, attributes: [String : String], managedContext: NSManagedObjectContext) throws -> ImdbEntity {
        switch entityType {
        case "Movie":
            return Movie(attributes: attributes, managedObjectContext: managedContext)!
        case "Series":
            return Series(attributes: attributes, managedObjectContext: managedContext)!
        case "Episode":
            return Episode(attributes: attributes, managedObjectContext: managedContext)!
        case "Game":
            return Game(attributes: attributes, managedObjectContext: managedContext)!
        default:
            throw NSError(domain: "NoImdbEntity", code: 123, userInfo: nil)
        }
    }
    
    class func update(entity: ImdbEntity, managedContext: NSManagedObjectContext) -> Bool {
        do {
            try managedContext.save()
            
            return true
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
            
            return false
        }
    }
    
    class func fetchImage(entity: ImdbEntity) -> UIImage? {
        let libraryDir = NSFileManager.defaultManager().URLsForDirectory(.LibraryDirectory, inDomains: .UserDomainMask)[0]
        let imagePath = libraryDir.URLByAppendingPathComponent(entity.posterUrl!)
        
        return UIImage(contentsOfFile: imagePath.path!)
    }
}
