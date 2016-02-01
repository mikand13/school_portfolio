//
//  StarRatingControl.swift
//  ImdbReader
//
//  This implementation is based on this example: https://medium.com/swift-programming/float-rating-view-in-swift-e740b6b9404d#.i7o756mtb
//  It shows and updates continously a floating rating from min to max. (Set in storyboard.)
//
//  Created by Anders Mikkelsen on 09/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import UIKit

public protocol StarRatingControlDelegate {
    func starRatingControlDidUpdate(starView: StarRatingControl, didUpdate rating: Float)
    func starRatingControlIsUpdating(starView: StarRatingControl, isUpdating rating: Float)
}

@IBDesignable
public class StarRatingControl: UIView {
    weak var delegate: ImdbEntityDetailViewController?

    private var emptyImageViews: [UIImageView] = []
    private var fullImageViews: [UIImageView] = []

    var imageContentMode: UIViewContentMode = UIViewContentMode.ScaleAspectFit

    @IBInspectable public var minImageSize: CGSize = CGSize(width: 5.0, height: 5.0)
    
    @IBInspectable public var emptyImage: UIImage? {
        didSet {
            for imageView in self.emptyImageViews {
                imageView.image = emptyImage
            }
            self.refresh()
        }
    }
    
    @IBInspectable public var fullImage: UIImage? {
        didSet {
            for imageView in self.fullImageViews {
                imageView.image = fullImage
            }
            self.refresh()
        }
    }
    
    @IBInspectable public var minRating: Int  = 0 {
        didSet {
            if self.rating < Float(minRating) {
                self.rating = Float(minRating)
                self.refresh()
            }
        }
    }
    
    @IBInspectable public var maxRating: Int = 5 {
        didSet {
            let needsRefresh = maxRating != oldValue
            
            if needsRefresh {
                self.removeImageViews()
                self.initImageViews()
                
                self.setNeedsLayout()
                self.refresh()
            }
        }
    }
    
    public var rating: Float = 0 {
        didSet {
            if rating != oldValue {
                self.refresh()
            }
        }
    }
    
    // MARK: Initializations
    
    required override public init(frame: CGRect) {
        super.init(frame: frame)
        
        self.initImageViews()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        self.initImageViews()
    }
    
    // MARK: Refresh images
    
    private func refresh() {
        for i in 0..<self.fullImageViews.count {
            let imageView = self.fullImageViews[i]
            
            if self.rating >= Float(i + 1) {
                imageView.layer.mask = nil
                imageView.hidden = false
            } else if self.rating>Float(i) && self.rating<Float(i + 1) {
                let maskLayer = CALayer()
                maskLayer.frame = CGRectMake(0, 0, CGFloat(self.rating - Float(i)) * imageView.frame.size.width, imageView.frame.size.height)
                maskLayer.backgroundColor = UIColor.blackColor().CGColor

                imageView.layer.mask = maskLayer
                imageView.hidden = false
            } else {
                imageView.layer.mask = nil;
                imageView.hidden = true
            }
        }
    }
    
    // MARK: Layout
    
    private func sizeForImage(image: UIImage, inSize size:CGSize) -> CGSize {
        let imageRatio = image.size.width / image.size.height
        let viewRatio = size.width / size.height
        
        if imageRatio < viewRatio {
            let scale = size.height / image.size.height
            let width = scale * image.size.width
            
            return CGSizeMake(width, size.height)
        } else {
            let scale = size.width / image.size.width
            let height = scale * image.size.height
            
            return CGSizeMake(size.width, height)
        }
    }
    
    override public func layoutSubviews() {
        super.layoutSubviews()
        
        if let emptyImage = self.emptyImage {
            let desiredImageWidth = self.frame.size.width / CGFloat(self.emptyImageViews.count)
            let maxImageWidth = max(self.minImageSize.width, desiredImageWidth)
            let maxImageHeight = max(self.minImageSize.height, self.frame.size.height)

            let imageViewSize = self.sizeForImage(emptyImage, inSize: CGSizeMake(maxImageWidth, maxImageHeight))
            let xPlacement = (self.frame.size.width - (imageViewSize.width * CGFloat(self.emptyImageViews.count)))
            let imageXOffset =  xPlacement / CGFloat((self.emptyImageViews.count - 1))
            
            for i in 0..<self.maxRating {
                let x = i == 0 ? 0:CGFloat(i) * (imageXOffset + imageViewSize.width)
                let y = (frame.size.height - imageViewSize.height) / 2
                let imageFrame = CGRectMake(x, y, imageViewSize.width, imageViewSize.height)
                
                var imageView = self.emptyImageViews[i]
                imageView.frame = imageFrame
                
                imageView = self.fullImageViews[i]
                imageView.frame = imageFrame
            }
            
            self.refresh()
        }
    }
    
    private func removeImageViews() {
        for i in 0..<self.emptyImageViews.count {
            var imageView = self.emptyImageViews[i]

            imageView.removeFromSuperview()
            imageView = self.fullImageViews[i]
            imageView.removeFromSuperview()
        }
        
        self.emptyImageViews.removeAll(keepCapacity: false)
        self.fullImageViews.removeAll(keepCapacity: false)
    }
    
    private func initImageViews() {
        if self.emptyImageViews.count != 0 {
            return
        }
        
        for _ in 0..<self.maxRating {
            let emptyImageView = UIImageView()
            emptyImageView.contentMode = self.imageContentMode
            emptyImageView.image = self.emptyImage

            self.emptyImageViews.append(emptyImageView)
            self.addSubview(emptyImageView)
            
            let fullImageView = UIImageView()
            fullImageView.contentMode = self.imageContentMode
            fullImageView.image = self.fullImage
            
            self.fullImageViews.append(fullImageView)
            self.addSubview(fullImageView)
        }
    }
    
    // MARK: Touch
    
    private func handleTouchAtLocation(touchLocation: CGPoint) {
        var newRating: Float = 0
        for i in (self.maxRating-1).stride(through: 0, by: -1) {
            let imageView = self.emptyImageViews[i]
            
            if touchLocation.x > imageView.frame.origin.x {
                let newLocation = imageView.convertPoint(touchLocation, fromView:self)
                
                if imageView.pointInside(newLocation, withEvent: nil) {
                    let decimalNumber = Float(newLocation.x / imageView.frame.size.width)
                    newRating = Float(i) + decimalNumber
                } else {
                    newRating = Float(i) + 1.0
                }
                
                break
            }
        }
        
        self.rating = newRating < Float(self.minRating) ? Float(self.minRating) : newRating
        
        if let delegate = self.delegate {
            delegate.starRatingControlIsUpdating(self, isUpdating: self.rating)
        }
    }
    
    override public func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        if let touch = touches.first {
            let touchLocation = touch.locationInView(self)
            self.handleTouchAtLocation(touchLocation)
        }
    }
    
    override public func touchesMoved(touches: Set<UITouch>, withEvent event: UIEvent?) {
        if let touch = touches.first  {
            let touchLocation = touch.locationInView(self)
            self.handleTouchAtLocation(touchLocation)
        }
    }
    
    override public func touchesEnded(touches: Set<UITouch>, withEvent event: UIEvent?) {
        if let delegate = self.delegate {
            delegate.starRatingControlDidUpdate(self, didUpdate: self.rating)
        }
    }
}
