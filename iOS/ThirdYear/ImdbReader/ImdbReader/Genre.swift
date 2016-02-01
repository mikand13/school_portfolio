//
//  Genre.swift
//  ImdbReader
//
//  Created by Anders Mikkelsen on 02/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import Foundation
import CoreData

class Genre: Nameable {
    class func calculateFavouriteGenre(imdbEntities: [AnyObject]?) -> String {
        guard let entities : [ImdbEntity] = imdbEntities as? [ImdbEntity] else {
            return NSLocalizedString("NothingStored", comment: "When nothing is stored in user lib.")
        }
        
        guard entities.count > 0 else {
            return NSLocalizedString("NothingStored", comment: "When nothing is stored in user lib.")
        }
        
        var (movies, series, episodes, games, currentMax) = calculateCounters(entities)
        var genresToDisplay : [String] = []
        
        checkAndAddGenreIfCountIsEqualToMax(&movies, counterMax: currentMax, genresToDisplay: &genresToDisplay,
            genre: NSLocalizedString("MoviesName", comment: "ImdbEntities as Movies in plural form"))
        checkAndAddGenreIfCountIsEqualToMax(&series, counterMax: currentMax, genresToDisplay: &genresToDisplay,
            genre: NSLocalizedString("SeriesName", comment: "ImdbEntities as Series in plural form"))
        checkAndAddGenreIfCountIsEqualToMax(&episodes, counterMax: currentMax, genresToDisplay: &genresToDisplay,
            genre: NSLocalizedString("EpisodesName", comment: "ImdbEntities as Episodes in plural form"))
        checkAndAddGenreIfCountIsEqualToMax(&games, counterMax: currentMax, genresToDisplay: &genresToDisplay,
            genre: NSLocalizedString("GamesName", comment: "ImdbEntities as Games in plural form"))
        
        return genresToDisplay.joinWithSeparator(", ")
    }
    
    private class func calculateCounters(entities: [ImdbEntity]) -> (Int, Int, Int, Int, Int) {
        var movies = 0, series = 0, episodes = 0, games = 0, currentMax = 0
        
        for entity in entities {
            switch entity {
            case is Movie:
                increaseCounter(&movies, counterMax: &currentMax)
            case is Series:
                increaseCounter(&series, counterMax: &currentMax)
            case is Episode:
                increaseCounter(&episodes, counterMax: &currentMax)
            case is Game:
                increaseCounter(&games, counterMax: &currentMax)
            default:
                print("Type not recognized")
            }
        }
        
        return (movies, series, episodes, games, currentMax)
    }
    
    private class func increaseCounter(inout counter: Int, inout counterMax: Int) {
        if ++counter > counterMax {
            counterMax = counter
        }
    }
    
    private class func checkAndAddGenreIfCountIsEqualToMax(inout counter: Int, counterMax: Int, inout genresToDisplay: [String], genre: String) {
        guard counter > 0 else {
            return
        }
        
        if counter == counterMax {
            genresToDisplay.append(genre)
        }
    }
}
