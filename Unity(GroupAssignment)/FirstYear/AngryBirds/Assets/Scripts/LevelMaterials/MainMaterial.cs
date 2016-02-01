using UnityEngine;
using System.Collections;

/**
 * This class defines superclass for all materials on the scene.
 * 
 * @author Group 9
 * 
 * */

public class MainMaterial : MonoBehaviour {
    void OnCollisionEnter2D(Collision2D collider) {
        SpecialBehaviour(collider);
    }

    virtual public void SpecialBehaviour(Collision2D collider) {
    
    }

    public void spawnDebris(string particleSystemName) {
        Instantiate(Resources.Load(particleSystemName), transform.position, new Quaternion(0, 0, 0, 0));
    }
}
