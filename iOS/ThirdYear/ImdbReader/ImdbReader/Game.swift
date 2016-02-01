//
//  Game.swift
//  ImdbReader
//
//  Created by Anders Mikkelsen on 07/12/15.
//  Copyright © 2015 Anders Mikkelsen. All rights reserved.
//

import Foundation
import CoreData


class Game: ImdbEntity {
    convenience init?(attributes: [String : String], managedObjectContext: NSManagedObjectContext) {
        guard let type = ImdbEntity.checkTypeGuard(attributes) else {
            return nil
        }
        
        self.init(type: type, attributes: attributes, managedObjectContext: managedObjectContext)
        
        runtimeMinutes = 0
    }
}
