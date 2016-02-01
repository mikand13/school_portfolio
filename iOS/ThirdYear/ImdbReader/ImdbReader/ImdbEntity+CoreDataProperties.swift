//
//  ImdbEntity+CoreDataProperties.swift
//  ImdbReader
//
//  Created by Anders Mikkelsen on 02/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

import Foundation
import CoreData

extension ImdbEntity {

    @NSManaged var awards: String?
    @NSManaged var review: String?
    @NSManaged var imdbId: String?
    @NSManaged var userRating: NSNumber?
    @NSManaged var imdbRating: NSNumber?
    @NSManaged var imdbVotes: NSNumber?
    @NSManaged var metascore: NSNumber?
    @NSManaged var plot: String?
    @NSManaged var posterUrl: String?
    @NSManaged var rated: String?
    @NSManaged var released: NSDate?
    @NSManaged var runtimeMinutes: NSNumber?
    @NSManaged var title: String?
    @NSManaged var year: String?
    @NSManaged var actors: NSSet?
    @NSManaged var countries: NSSet?
    @NSManaged var directors: NSSet?
    @NSManaged var genres: NSSet?
    @NSManaged var languages: NSSet?
    @NSManaged var writers: NSSet?

}
