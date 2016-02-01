using UnityEngine;
using System.Collections;

/**
 * This class generates the Alien Laser, subclass of Laser.
 * 
 * @author Anders Mikkelsen
 * */

public class AlienLaserShot : Laser {
    
	void Update () {
        transform.Translate(Vector3.down * base.MoveSpeed * Time.deltaTime);

        if (transform.position.y < -3.8f) {
            DestroyObject(gameObject);
        }
	}

    void OnTriggerEnter2D(Collider2D collider) {
        DestroyObject(gameObject);
    }
}
