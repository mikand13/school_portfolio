//
//  ImdbReaderUITests.swift
//  ImdbReaderUITests
//
//  Created by Anders Mikkelsen on 25/11/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import XCTest
@testable import ImdbReader

class ImdbReaderUITests: XCTestCase {
    var app: XCUIApplication?
    
    override func setUp() {
        super.setUp()
        
        continueAfterFailure = false
        XCUIApplication().launch()
        app = XCUIApplication()
        
        if app!.tables.cells.count == 0 {
            XCTAssertNotNil(XCUIApplication().alerts["Nothing is stored!"].collectionViews.buttons["OK"], "No elements alert should be displayed")
            XCUIApplication().alerts["Nothing is stored!"].collectionViews.buttons["OK"].tap()
        } else {
            deleteAllStarWars(app!)
        }
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func testSearchButtonShouldDisplaySearchTableViewControllerAndShouldBeReturnable() {
        let app = XCUIApplication()
        app.navigationBars["My IMDb"].buttons["Search IMDb"].tap()
        XCTAssertNotNil(XCUIApplication().navigationBars["Search Results"], "SearchVC should be available")
        app.navigationBars["Search Results"].buttons["Stop"].tap()
        XCTAssertNotNil(app.alerts["Nothing is stored!"].collectionViews.buttons["OK"], "No elements alert should be displayed")
        app.alerts["Nothing is stored!"].collectionViews.buttons["OK"].tap()
        XCTAssertNotNil(app.navigationBars["My IMDb"], "We should be back at my imdb.")
    }
    
    func testSearchForStarWarsShouldReturnResults() {
        goToSearchOptions(app!)
        XCTAssertNotNil(app!.navigationBars["Search Options"], "SearchOptionsVC should be available")
        
        searchForStarWars(app!)
        XCTAssertNotNil(app!.navigationBars["Search Results"], "SearchVC should be available")
        XCTAssertNotEqual(0, app!.tables.cells.count)
    }
    
    func testSearchStoreAndDelete() {
        goToSearchOptions(app!)
        searchForStarWars(app!)
        addStarWarsAndExitSearch(app!)
        XCTAssertEqual(1, app!.tables.cells.count)
        deleteSingularStarWars(app!)
        XCTAssertEqual(0, app!.tables.cells.count)
    }
    
    func testDisplayAndEditImdbEntity() {
        goToSearchOptions(app!)
        searchForStarWars(app!)
        addStarWarsAndExitSearch(app!)
        app!.tables.elementBoundByIndex(0).cells.elementBoundByIndex(0).tap()
        XCTAssertNotNil(app!.textViews["reviewTextArea"])
        let oldValue = app!.textViews["reviewTextArea"].value as? String
        let newValue = editReviewAreaOnDetail(app!)
        returnFromDetailToMaster(app!)
        app!.tables.elementBoundByIndex(0).cells.elementBoundByIndex(0).tap()
        XCTAssertNotEqual(newValue!, oldValue!)
        returnFromDetailToMaster(app!)
        
        deleteSingularStarWars(app!)
    }
    
    func testMultipleAddAndDelete() {
        goToSearchOptions(app!)
        searchForStarWars(app!)
        
        for i in 0..<3 {
            app!.tables.elementBoundByIndex(0).cells.elementBoundByIndex(UInt(i)).tap()
        }
        
        app!.navigationBars["Search Results"].buttons["Stop"].tap()
        
        XCTAssertEqual(3, app!.tables.cells.count)
        deleteAllStarWars(app!)
        XCTAssertEqual(0, app!.tables.cells.count)
    }
    
    private func goToSearchOptions(app: XCUIApplication) {
        app.navigationBars["My IMDb"].buttons["Search IMDb"].tap()
        app.navigationBars["Search Results"].buttons["Search"].tap()
    }
    
    private func searchForStarWars(app: XCUIApplication) {
        let tittelTextField = app.textFields["Title"]
        tittelTextField.tap()
        tittelTextField.typeText("Star Wars: Episode IV")
        app.typeText("\r")
        app.navigationBars["Search Options"].buttons["Do Search"].tap()
    }
    
    private func addStarWarsAndExitSearch(app: XCUIApplication) {
        app.tables.elementBoundByIndex(0).cells.elementBoundByIndex(0).tap()
        app.navigationBars["Search Results"].buttons["Stop"].tap()
    }
    
    private func deleteSingularStarWars(app: XCUIApplication) {
        app.navigationBars["My IMDb"].buttons["Edit"].tap()
        
        let tablesQuery = app.tables
        tablesQuery.buttons["Delete Title:, Released:, Star Wars: Episode IV - A New Hope, 1977"].tap()
        tablesQuery.buttons["Delete"].tap()
    }
    
    private func deleteAllStarWars(app: XCUIApplication) {
        app.navigationBars["My IMDb"].buttons["Edit"].tap()

        while (app.tables.elementBoundByIndex(0).cells.count > 0) {
            app.tables.elementBoundByIndex(0).cells.elementBoundByIndex(0).buttons.elementBoundByIndex(0).tap()
            app.tables.buttons["Delete"].tap()
        }
    }
    
    private func editReviewAreaOnDetail(app: XCUIApplication) -> String? {
        let reviewtextareaTextView = app.textViews["reviewTextArea"]
        reviewtextareaTextView.tap()
        reviewtextareaTextView.typeText("sometest\r")
        app.navigationBars.matchingIdentifier("Done").buttons["Done"].tap()
        
        return reviewtextareaTextView.value as? String
    }
    
    private func returnFromDetailToMaster(app: XCUIApplication) {
        app.navigationBars.matchingIdentifier("Done").buttons["My IMDb"].tap()
    }
}
