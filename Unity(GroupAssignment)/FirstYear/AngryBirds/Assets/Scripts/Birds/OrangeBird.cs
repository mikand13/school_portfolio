using UnityEngine;
using System.Collections;
[RequireComponent (typeof (CircleCollider2D))]
[RequireComponent (typeof (Animator))]
[RequireComponent (typeof (Rigidbody2D))]

/**
 * This class defines Orange Bird behaviour.
 * 
 * @author Group 9
 * 
 * */

public class OrangeBird : AllBirdsScript {
	public bool isBig = false;

	private CircleCollider2D _cc2D;
	private Rigidbody2D _rBody2D;
	
	void Start ()
	{
		_cc2D = GetComponent<CircleCollider2D> ();
		_rBody2D = GetComponent<Rigidbody2D> ();

	}

    override public void SpecialBehaviour() {
        if (isFired && !isBig && Input.GetButtonDown("Jump")) {
            isBig = true;
            anim.SetBool("isBig", isBig);
            _cc2D.center = new Vector2(0.1f, 0f);
            _rBody2D.gravityScale = _rBody2D.gravityScale / 2;
        }

        if (_cc2D.radius < 1.50f && isBig) {
            _cc2D.radius += 0.1f;
        }
    }

    void OnCollisionEnter2D(Collision2D collider) {
        if (isBig) {
            makeSquirt(collider, "ParticleSystem/LargeOrangeBirdFeatherSquirt");
        } else {
            makeSquirt(collider, "ParticleSystem/TinyOrangeBirdFeatherSquirt"); 
        }
    }
}
