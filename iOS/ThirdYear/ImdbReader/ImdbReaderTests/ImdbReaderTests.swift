//
//  ImdbReaderTests.swift
//  ImdbReaderTests
//
//  In-Memory CoreData from here: http://alanduncan.me/blog/2014/09/28/swift-core-data-tests/
//
//  Created by Anders Mikkelsen on 25/11/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import XCTest
import CoreData
@testable import ImdbReader

class ImdbReaderTests: TestCoreData {
    var testMovieImdbEntity: ImdbEntity?
    
    override func setUp() {
        continueAfterFailure = false
        testMovieImdbEntity = createTestMovieImdbEntity(managedObjectContext!)
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func testGenresCreation() {
        nameablesTester("Genre", nameables: "Action, Adventure, Sci-Fi")
        XCTAssertEqual(3, testMovieImdbEntity!.genres?.count)
    }
    
    func testDirectorsCreation() {
        nameablesTester("Director", nameables: "George Lucas")
        XCTAssertEqual(1, testMovieImdbEntity!.directors?.count)
    }
    
    func testWritersCreation() {
        nameablesTester("Writer", nameables: "George Lucas")
        XCTAssertEqual(1, testMovieImdbEntity!.writers?.count)
    }
    
    func testActorsCreation() {
        nameablesTester("Actor", nameables: "Mark Hamill, Harrison Ford, Carrie Fisher, Peter Cushing")
        XCTAssertEqual(4, testMovieImdbEntity!.actors?.count)
    }
    
    func testCountriesCreation() {
        nameablesTester("Country", nameables: "USA")
        XCTAssertEqual(1, testMovieImdbEntity!.countries?.count)
    }
    
    func testLanguagesCreation() {
        nameablesTester("Language", nameables: "English")
        XCTAssertEqual(1, testMovieImdbEntity!.languages?.count)
    }
    
    private func nameablesTester(type: String, nameables: String) {
        Nameable.getOrCreateNameablesFromJSONString(type, imdbEntity: testMovieImdbEntity!, nameables: nameables, managedContext: managedObjectContext!)
    }
    
    func testCalculateFavouriteGenre() {
        for _ in 0..<4 {
            ImdbEntity.persistImdbEntity(self.createTestMovieImdbEntityAsArray(), image: UIImage(named: "imdb-logo")!, managedContext: self.managedObjectContext!)
        }
        
        let fetchRequest = NSFetchRequest(entityName: "Movie")
        fetchRequest.predicate = NSPredicate(format: "title = %s", argumentArray: ["Star Wars: Episode IV - A New Hope"])

        let imdbEntities = try! managedObjectContext!.executeFetchRequest(fetchRequest) as! [ImdbEntity]
        let genreString = Genre.calculateFavouriteGenre(imdbEntities)
        
        XCTAssertEqual("Movies", genreString, "Genrestring is incorrect!")
    }
    
    // Tests the runtime of actual persisting of objects with the correct method.
    func testPersistImdbEntity() {
        self.measureBlock {
            ImdbEntity.persistImdbEntity(self.createTestMovieImdbEntityAsArray(), image: UIImage(named: "imdb-logo")!, managedContext: self.managedObjectContext!)
        }
        
        let fetchRequest = NSFetchRequest(entityName: "Movie")
        fetchRequest.predicate = NSPredicate(format: "title = %s", argumentArray: ["Star Wars: Episode IV - A New Hope"])
        let imdbEntities = try! managedObjectContext!.executeFetchRequest(fetchRequest) as! [ImdbEntity]
        
        XCTAssertEqual(1, imdbEntities.count, "ImdbEntity not stored!")
    }

    // Creates a "mocked" imdbentity for testing, without using the ImdbEntity class.
    private func createTestMovieImdbEntity(managedObjectContext: NSManagedObjectContext) -> ImdbEntity {
        let entity = NSEntityDescription.entityForName("Movie", inManagedObjectContext: managedObjectContext)
        let testMovieImdbEntity = Movie(entity: entity!, insertIntoManagedObjectContext: managedObjectContext)
        
        testMovieImdbEntity.title = "Star Wars: Episode IV - A New Hope"
        testMovieImdbEntity.imdbId = "tt0076759"
        testMovieImdbEntity.imdbRating = 8.7
        testMovieImdbEntity.userRating = 0.0
        testMovieImdbEntity.imdbVotes = 802804
        testMovieImdbEntity.metascore = 92
        testMovieImdbEntity.plot = "Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a wookiee and two droids to save the universe from the Empire's world-destroying battle-station, while also attempting to rescue Princess Leia from the evil Darth Vader."
        testMovieImdbEntity.posterUrl = "N/A"
        testMovieImdbEntity.rated = "PG"
        
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "dd-MM-yyyy"
        
        testMovieImdbEntity.released = dateFormatter.dateFromString("25 May 2015")
        testMovieImdbEntity.runtimeMinutes = 121
        testMovieImdbEntity.awards = "Won 6 Oscars. Another 38 wins & 27 nominations."
        testMovieImdbEntity.year = "1977"
        testMovieImdbEntity.review = "Du har ikke registrert en anmeldelse enda!"
        
        return testMovieImdbEntity
    }
    
    // Makes a bogus json array for creation
    private func createTestMovieImdbEntityAsArray() -> ([String : String]) {
        var testMovieImdbEntityAsArray = [String : String]()
        testMovieImdbEntityAsArray["Title"] = "Star Wars: Episode IV - A New Hope"
        testMovieImdbEntityAsArray["Year"] = "1977"
        testMovieImdbEntityAsArray["Rated"] = "PG"
        testMovieImdbEntityAsArray["Released"] = "25 May 2015"
        testMovieImdbEntityAsArray["Runtime"] = "121 min"
        testMovieImdbEntityAsArray["Genre"] = "Action, Adventure, Sci-Fi"
        testMovieImdbEntityAsArray["Director"] = "George Lucas"
        testMovieImdbEntityAsArray["Writer"] = "George Lucas"
        testMovieImdbEntityAsArray["Actors"] = "Mark Hamill, Harrison Ford, Carrie Fisher, Peter Cushing"
        testMovieImdbEntityAsArray["Plot"] = "Some Test Plot"
        testMovieImdbEntityAsArray["Language"] = "English"
        testMovieImdbEntityAsArray["Country"] = "USA"
        testMovieImdbEntityAsArray["Awards"] = "Some Test Awards"
        testMovieImdbEntityAsArray["Poster"] = "N/A"
        testMovieImdbEntityAsArray["Metascore"] = "92"
        testMovieImdbEntityAsArray["imdbRating"] = "8.7"
        testMovieImdbEntityAsArray["imdbVotes"] = "802,804"
        testMovieImdbEntityAsArray["imdbID"] = "tt0076759"
        testMovieImdbEntityAsArray["Type"] = ""
        testMovieImdbEntityAsArray["Response"] = ""
        
        return testMovieImdbEntityAsArray
    }
}
