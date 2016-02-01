//
//  Nameable.swift
//  ImdbReader
//
//  This class initializes all nameables based on input type.
//
//  Created by Anders Mikkelsen on 10/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import Foundation
import CoreData

class Nameable: NSManagedObject {
    class func getOrCreateNameablesFromJSONString(type: String, imdbEntity: ImdbEntity, nameables: String, managedContext: NSManagedObjectContext) -> [AnyObject]? {
        let nameablesAsIndividualStrings = nameables.componentsSeparatedByString(",")
        var nameables = [AnyObject]()
        
        for s in nameablesAsIndividualStrings {
            let name = s.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())
            
            if let coreDataNameAble = checkIfNameableExists(type, name: name, managedContext: managedContext) {
                coreDataNameAble.mutableSetValueForKey("imdbEntities").addObjectsFromArray([imdbEntity])
                
                nameables.append(coreDataNameAble)
            } else {
                guard let newNameable = createNameable(type, imdbEntity: imdbEntity, name: name, managedContext: managedContext) else {
                    print("Unable to create nameable")
                    return nil
                }
                
                nameables.append(newNameable)
            }
        }
        
        return nameables
    }
    
    // Checks if input nameable already exists as persisted object with a fetchrequest.
    private class func checkIfNameableExists(type: String, name: String, managedContext: NSManagedObjectContext) -> Nameable? {
        let fetchRequest = NSFetchRequest(entityName: type)
        fetchRequest.predicate = NSPredicate(format: "name = %s", argumentArray: [name])
        
        do {
            let results = try managedContext.executeFetchRequest(fetchRequest) as! [NSManagedObject]
            
            return results.first as? Nameable
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
            
            return nil
        }
    }
    
    // Creates a fresh nameable of the inferred type.
    private class func createNameable(type: String, imdbEntity: ImdbEntity, name: String, managedContext: NSManagedObjectContext) -> Nameable? {
        let newNameableEntity = NSEntityDescription.entityForName(type, inManagedObjectContext: managedContext)
        
        guard let newNamable = inferAndConstructType(type, newNameableEntity: newNameableEntity!, managedContext: managedContext) else {
            print("Could not infer type...")
            return nil
        }
        
        newNamable.name = name
        newNamable.mutableSetValueForKey("imdbEntities").addObjectsFromArray([imdbEntity])
        
        return newNamable
    }
    
    // Does actual infering and construction of object.
    private class func inferAndConstructType(type: String, newNameableEntity: NSEntityDescription, managedContext: NSManagedObjectContext) -> Nameable? {
        switch type {
        case "Genre":
            return Genre(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        case "Country":
            let country = Country(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
            country.code = "N/A"
            
            return country
        case "Director":
            return Director(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        case "Language":
            return Director(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        case "Actor":
            return Actor(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        case "Writer":
            return Writer(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        case "Language":
            return Language(entity: newNameableEntity, insertIntoManagedObjectContext: managedContext)
        default:
            return nil
        }
    }
}
