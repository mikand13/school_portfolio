using UnityEngine;
using System.Collections;

public class MoveMothership : Alien {
    public float moveSpeed;
    public Alien alien1;

    /**
     * The following to methods overrides the ones in Alien, so the mothership wont fire shots.
     * */ 
    void Start() {
        pointValue = alien1.pointValue * 10;
    }
	void Update () {
        if (transform.position.x < 5.2) {
            transform.Translate(Vector3.right * moveSpeed * Time.deltaTime);
        } else {
            DestroyObject(gameObject);
        }
	}

    public override void specialBehaviour(Collider2D collider) {
        Object o = Instantiate(Resources.Load("Mothership/ParticleExplosion"), collider.transform.position, transform.rotation);
        StartCoroutine(KillExplosion(o));
        base.specialBehaviour(collider);
    }

    IEnumerator KillExplosion(Object o) {
        yield return new WaitForSeconds(0.5f);
        DestroyObject(o);
    }
}
