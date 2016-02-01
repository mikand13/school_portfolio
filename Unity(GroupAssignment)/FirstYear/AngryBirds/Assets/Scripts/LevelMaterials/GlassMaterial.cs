using UnityEngine;
using System.Collections;

/**
 * This class defines special behaviour of glass.
 * 
 * @author Group 9
 * 
 * 
 * */

public class GlassMaterial : MainMaterial {
    public float velocityThreshold;

    override public void SpecialBehaviour(Collision2D collider) {
        if (collider.relativeVelocity.x > velocityThreshold || collider.relativeVelocity.y > velocityThreshold) {
            spawnDebris("ParticleSystem/GlassSquirt");
            GameManager.Instance.OnGameObjectDie(gameObject);
        }
    }
}
