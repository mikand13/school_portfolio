using UnityEngine;
using System.Collections;

/**
 * This class defines special behaviour of wood.
 * 
 * @author Group 9
 * 
 * 
 * */

public class WoodMaterial : MainMaterial {
    public float velocityThreshold;

    override public void SpecialBehaviour(Collision2D collider) {
        if (collider.relativeVelocity.x > velocityThreshold || collider.relativeVelocity.y > velocityThreshold) {
            spawnDebris("ParticleSystem/WoodSquirt");
            GameManager.Instance.OnGameObjectDie(gameObject);
        }
    }
}
