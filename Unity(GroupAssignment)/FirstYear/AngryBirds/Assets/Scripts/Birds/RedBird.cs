using UnityEngine;
using System.Collections;

/**
 * This class defines small red bird behaviour.
 * 
 * @author Group 9
 * 
 * */

public class RedBird : AllBirdsScript {
    void OnCollisionEnter2D(Collision2D collider) {
        makeSquirt(collider, "ParticleSystem/RedBirdFeatherSquirt");
    }
}
