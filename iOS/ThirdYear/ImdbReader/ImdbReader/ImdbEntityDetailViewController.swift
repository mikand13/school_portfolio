//
//  DetailViewController.swift
//  ImdbReader
//
//  This class defines the Viewcontroller that builds and shows the detailview for the master view controller.
//
//  Created by Anders Mikkelsen on 25/11/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit
import CoreData

class ImdbEntityDetailViewController: UIViewController, UITextViewDelegate, StarRatingControlDelegate {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var genreLabel: UILabel!
    @IBOutlet weak var directorLabel: UILabel!
    @IBOutlet weak var actorLabel: UILabel!
    @IBOutlet weak var runtimeLabel: UILabel!
    @IBOutlet weak var typeLabel: UILabel!
    @IBOutlet weak var reviewTextArea: UITextView!
    @IBOutlet weak var reviewDoneButton: UIButton!
    @IBOutlet weak var myRatingLabel: UILabel!
    @IBOutlet weak var starRatingView: StarRatingControl!
    
    @IBOutlet weak var deleteButton: UIBarButtonItem!

    var managedContext : NSManagedObjectContext?
    var keyboardHeight: CGFloat! = 0
    var currentRating: Float! = 0
    var editingReview: Bool = false

    var detailItem: ImdbEntity? {
        didSet {
            self.configureView()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.configureView()
        
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        managedContext = appDelegate.managedObjectContext
        
        setGestureRecognizerForView("touchOutsideReviewArea", sender: self)
        
        reviewTextArea.delegate = self
        starRatingView.delegate = self
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillShow:"), name: UIKeyboardWillShowNotification, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillHide:"), name: UIKeyboardWillHideNotification, object: nil)
        
        self.starRatingView?.rating = currentRating
    }
    
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        
        NSNotificationCenter.defaultCenter().removeObserver(self)
        
        if let detail = self.detailItem {
            ImdbEntity.update(detail, managedContext: managedContext!)
        }
    }
    
    // MARK: View Configuration
    
    private func configureView() {
        reviewDoneButton.hidden = true
        
        guard let detail = self.detailItem else {
            print("detalItem is unspecified...")
            clearView()
            return
        }
        
        if let label = titleLabel { label.text = detail.title }
        if let label = reviewTextArea { label.text = detail.review }
        if let label = ratingLabel { label.text = String(format: "%.1f", Float(detail.imdbRating!)) }
        if let label = genreLabel { label.text = makeNameableLabel(detail.genres) }
        if let label = directorLabel { label.text = makeNameableLabel(detail.directors) }
        if let label = actorLabel { label.text = makeNameableLabel(detail.actors) }
        if let label = typeLabel { label.text = String(detail.dynamicType) }
        if let label = runtimeLabel { label.text = makeRunTimeLabel(detail) }
        if let label = myRatingLabel { label.baselineAdjustment = .AlignCenters }
            
        currentRating = Float(detail.userRating!)
    }
    
    private func makeNameableLabel(nameableNSSet: NSSet?) -> String {
        guard let nameables = nameableNSSet!.allObjects as? [Nameable] else {
            return "N/A"
        }
        
        return nameables.map({ $0.name! }).joinWithSeparator(", ")
    }
    
    private func makeRunTimeLabel(entity: ImdbEntity) -> String {
        let runtime = Int(entity.runtimeMinutes!)
        
        return runtime > 0 ? minutesToHoursMinutesToString(runtime) : "N/A"
    }
    
    private func minutesToHoursMinutesToString (minutes: Int) -> String {
        let (h, m) = minutesToHoursMinutes(minutes)
        
        return String(format: NSLocalizedString("HoursAndMinutes", comment: "Formatted string for hours and minutes"), h, m)
    }
    
    private func minutesToHoursMinutes (minutes: Int) -> (Double, Double) {
        let (hr,  minf) = modf (Double(minutes) / 60)
        let (min, _) = modf (60 * minf)
        
        return (hr, min)
    }
    
    private func clearView() {
        self.view.hidden = true
    }
    
    // MARK: Navigation
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        guard sender is UIBarButtonItem else {
            print("Aborted...")
            return
        }
        
        switch sender as! UIBarButtonItem {
        case deleteButton:
            ImdbEntity.destroyImdbEntity(self.detailItem!, managedContext: self.managedContext!)
        default:
            print("No segue prep needed...")
        }
    }
    
    // MARK: Keyboardactions
    
    func keyboardWillShow(notification: NSNotification) {
        guard let userInfo = notification.userInfo else {
            print("Could not fetch userinfo for keyboard notification")
            return
        }
        
        guard let keyboardSize = (userInfo[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.CGRectValue() else {
            print("Could not get keyboardsize")
            return
        }
        
        keyboardHeight = keyboardSize.height
        startEditingReview()
    }
    
    func keyboardWillHide(notification: NSNotification) {
        if editingReview {
            stopEditingReview()
        }
    }
    
    func startEditingReview() {
        self.animateTextField(true)
        reviewDoneButton.enabled = true
        reviewDoneButton.hidden = false
        editingReview = true
    }
    
    func stopEditingReview() {
        editingReview = false
        self.animateTextField(false)
        reviewTextArea.endEditing(true)
        reviewDoneButton.enabled = false
        reviewDoneButton.hidden = true
    }
    
    private func animateTextField(up: Bool) {
        let movement = (up ? -keyboardHeight : keyboardHeight)
        
        UIView.animateWithDuration(0.3, animations: {
            self.view.frame = CGRectOffset(self.view.frame, 0, movement)
        })
    }
    
    func touchOutsideReviewArea() {
        if editingReview {
            stopEditingReview()
        }
    }
    
    // MARK: UITextViewDelegate
    
    func textViewDidEndEditing(textView: UITextView) {
        self.detailItem?.review = reviewTextArea.text!
    }
    
    // MARK: StarRatingControlDelegate
    
    func starRatingControlDidUpdate(starView: StarRatingControl, didUpdate rating: Float) {
        starRatingControlIsUpdating(starView, isUpdating: rating)
    }
    
    func starRatingControlIsUpdating(starView: StarRatingControl, isUpdating rating: Float) {
        self.starRatingView.rating = rating
        self.detailItem?.userRating = rating
    }
    
    // MARK: Actions
    
    @IBAction func reviewDoneAction(sender: AnyObject) {
        if editingReview {
            stopEditingReview()
        }
    }
}


