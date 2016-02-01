using UnityEngine;
using System.Collections;

/**
 * This class generates the Player Laser, subclass of Laser.
 * 
 * @author Anders Mikkelsen
 * */

public class LaserShot : Laser {
    public delegate void OnHit(bool hit);
    public static event OnHit onHitListener;

	void Update () {
        transform.Translate(Vector3.up * base.MoveSpeed * Time.deltaTime);

        if (transform.position.y > 0.3f) {
            DestroyObject(gameObject);
        }
	}

    void OnDestroy() {
        onHitListener(true);
    }
}
