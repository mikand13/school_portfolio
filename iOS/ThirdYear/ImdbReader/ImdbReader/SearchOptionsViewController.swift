//
//  SearchOptionsViewController.swift
//  ImdbReader
//
//  Utilizes SwiftSpinner from https://github.com/icanzilb/SwiftSpinner.git
//
//  Created by Anders Mikkelsen on 01/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit
import Alamofire
import SwiftSpinner

class SearchOptionsViewController: UIViewController, UITextFieldDelegate, UIPickerViewDataSource, UIPickerViewDelegate {
    @IBOutlet weak var searchTitleTextField: UITextField!
    @IBOutlet weak var searchYearTextField: UITextField!
    @IBOutlet weak var searchTypePicker: UIPickerView!
    
    var searchResult : [[String : String]]?
    var imdbTypes : [String] = [ "Movie", "Series", "Episode" ] // Hardcoded because of API limitation
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setGestureRecognizerForView("touchOutsideSearchAreas", sender: self)
        
        searchTitleTextField.delegate = self
        searchYearTextField.delegate = self
        searchTypePicker.delegate = self
    }
    
    // MARK: - Navigation

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        guard sender is UIBarButtonItem else {
            print("Aborted...")
            return
        }
        
        if let searchTableVC = segue.destinationViewController as? SearchTableViewController {
            searchTableVC.searchResult = self.searchResult
        }
    }
    
    // MARK: UIPickerViewDataSource
    
    func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return imdbTypes.count
    }
    
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return imdbTypes[row]
    }
    
    // MARK: UITextFieldDelegate
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        
        return true
    }
    
    func touchOutsideSearchAreas(){
        searchYearTextField.endEditing(true)
        searchTitleTextField.endEditing(true)
    }

    // MARK: Actions
    
    @IBAction func searchButtonAction(sender: UIBarButtonItem) {
        guard let params = checkFormValidity() else {
            print("Form invalid...")
            return
        }
        
        SwiftSpinner.show(NSLocalizedString("FetchFromIMDbNotice", comment: "Notice on loadingwheel for imdbfetch"))
        
        Alamofire.request(.GET, AppDelegate.imdbApiEndPoint, parameters: params).validate()
            .responseData { response in
                guard response.result.isSuccess else {
                    SwiftSpinner.hide()
                    self.doInputAlert(NSLocalizedString("NoConnection", comment: "Title on no connection alert"),
                        message: NSLocalizedString("NoConnectionDescription", comment: "Notice on alert for no internet on imdbfetch"))
                    print("JSONfetch failed")
                    return
                }
            }.responseJSON { response in
                guard let json = response.result.value else {
                    SwiftSpinner.hide()
                    self.doInputAlert(NSLocalizedString("ExternalErrorOMDB", comment: "Title on OMDB error alert"),
                        message: NSLocalizedString("ExternalErrorOMDBDescription", comment: "Notice on alert for external error in OMDB on imdbfetch"))
                    print("Could not get json from response")
                    return
                }
                
                guard let searchResult = json["Search"]! else {
                    SwiftSpinner.hide()
                    self.doInputAlert(NSLocalizedString("NoResults", comment: "Title on no hits alert"),
                        message: NSLocalizedString("NoResultsDescription", comment: "Notice on alert for no hits on imdbfetch"))
                    print("Could not fetch search results")
                    self.doInputAlert("No hits!", message: "Please enter different search criteria...")
                    return
                }
                
                self.searchResult = searchResult as? [[String : String]]
                SwiftSpinner.hide()
                self.performSegueWithIdentifier("returnToSearchList", sender: sender)
        }
    }
    
    private func checkFormValidity() -> [String : String]? {
        guard let title : String = searchTitleTextField.text! else {
            return nil
        }
        
        guard title.characters.count > 0 else {
            doInputAlert(NSLocalizedString("NoTitle", comment: "Title on no title alert for searchform"),
                message: NSLocalizedString("NoTitleDescription", comment: "Notice on no title alert for searchform"))
            return nil
        }
        
        if searchYearTextField.text?.characters.count > 0 {
            guard (Int(searchYearTextField.text!) != nil) else {
                doInputAlert(NSLocalizedString("WrongYearFormat", comment: "Title on wrong year format alert for searchform"),
                    message: NSLocalizedString("WrongYearFormatDescription", comment: "Notice on wrong year alert for searchform"))
                return nil
            }
        }
        
        return buildSearchParams()
    }
    
    private func buildSearchParams() -> [String : String]? {
        var searchParams = [String : String]()
        searchParams["s"] = searchTitleTextField.text!
        
        if let year = searchYearTextField.text {
            searchParams["y"] = year
        }
        
        if let type : String = imdbTypes[searchTypePicker.selectedRowInComponent(0)] {
            searchParams["t"] = type
        }
        
        searchParams["plot"] = "long"
        searchParams["r"] = "json"

        return searchParams
    }
}
