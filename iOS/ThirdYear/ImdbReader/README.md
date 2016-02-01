# PG5600-Exam

## Dependencies
* Alamofire (https://github.com/Alamofire/Alamofire)
* AlamofireImage (https://github.com/Alamofire/AlamofireImage)
* SwiftSpinner (https://github.com/icanzilb/SwiftSpinner)
* Xcode 7.2
* Swift 2.0
* Development target >~ 9.1 (This is what i have tested against, 9.1 and 9.2 only sim and physical device)

## Inspirations (Referanser annotated in class docs where used)
* Dynamic setting of ratingstars with floatvalue (https://medium.com/swift-programming/float-rating-view-in-swift-e740b6b9404d#.i7o756mtb)
* In-Memory CoreData for unittests (http://alanduncan.me/blog/2014/09/28/swift-core-data-tests/)

## External Resources (Kilder)
* IMDb Logo (http://www.imdb.com/pressroom/brand_guidelines/)
* OMDB API (http://http://www.omdbapi.com/)
* iOS Developer Library (https://developer.apple.com/library/ios/navigation/)

## App Info
* The app has been tested on devices from 4S and 6S (Physical) to various devices on the simulator.
* The app supports landscape mode.
* The app is localized for English (Development Language) and Norwegian Bokmål (Norway).
* The launchscreen is a simple copy of the initial table of entities, with an imdb logo and a loading message. (This is not localizing for some reason although the storyboard is in place.
* The app works in ipad mode as long as you are in landscape but the detail views get messed up in portrait, i guess one needs to make a special ipad interpretation?
* The app is set up to handle seasons and episodes but the api for getting them is pretty crap, as you cant predetermine season counts and stuff like that so it is not yet implemented.
* All information from the omdb api is stored but not all is shown in the detailview. I extracted what I thought was the most important but I think i show good forethought in storing all information.
* Images are fetched once and then stored on hashvalue for later retreival. That way we wont get the same pictures over and over again. This includes when search for the same items, it will fetch the stored image instead of fetching from the api.
* The Favourite Genre label works on any amount of equally sized genres.
* I decided to not use SwiftyJson for parsing the json, because there was a bigger learning opportunity in doing it myself.
* You can do a search of all your entities, which will update automatically as you type your searchstring!

## Install
* Run ```pod install```
* Open ImdbReader.xcworkspace
* Run ```cmd + U``` to run tests and check that everything is as it should be. (Turn off Hardware Keyboard, messes up keyboard notifications for uitests on detailview.)

### Comments
* The app is in my opinion adhereing to the principles of MVC as laid down in the apple guides. Display logic exists in controllers, views only handle setting of own variables or direct display issues. All heavy logic has been moved down into the model layer. Only exception is the recalculation of star ratings but it didnt make sense to push it up into the controller or make a new model for it, as it isnt a coredata level item.
* I couldve have done the genrefiltering with a predicated NSExpression but i suspect the algorithm i made is simple and effecient enough to be as good or faster than a coredata fetch. So im sticking to it!

### Tests
* Current testcoverage is 63% app wide.
* The unittests test the most important logic which is the storing of imdbentities in the database. I have not implemented any negative tests as the only way that would happen is if the omdb api changes and if that happens the tests will fail and both the tests and the logic must be tweaked anyway.
* The UItests test principal uinavigation for searching for a movie, storing it, checking it out and giving it a review and deleting it again. 

### Testing conclusion

I chose to focus mainly on UI tests as they are very effective in exposing flaws in development and the user experience always comes first. This revealed some issues with accessibility which i was able to work out. I then focused on testing the CoreData functionality. Specifically the storing of the data from OMDB. I also test some of the other algorithms i made for the app but most of the controller logic is pretty straight forward so i didnt focus on that, most of it is private as well. The tests include one game entity which was intended because the OMDB API is pretty imprecise and caused quite som greif with its "N/A" values.

### Further testing

For further testing i wouldve first made several more uitests to test every possible button and interaction combination to ensure the user experience is as smooth as possible. Then i would start implementing more exhaustive tests for all coredata related logic. It is relativly robust now but could be alot stronger.
