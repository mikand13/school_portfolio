using UnityEngine;
using System.Collections;

/**
 * This class defines special behaviour of Stone.
 * 
 * @author Group 9
 * 
 * 
 * */

public class StoneMaterial : MainMaterial {
    public float velocityThreshold;

    override public void SpecialBehaviour(Collision2D collider) {
        if (collider.relativeVelocity.x > velocityThreshold || collider.relativeVelocity.y > velocityThreshold) {
            spawnDebris("ParticleSystem/StoneSquirt");
            GameManager.Instance.OnGameObjectDie(gameObject);
        }
    }
}
