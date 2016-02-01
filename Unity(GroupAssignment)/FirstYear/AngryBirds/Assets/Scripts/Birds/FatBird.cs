using UnityEngine;
using System.Collections;
[RequireComponent (typeof (Rigidbody2D))]

/**
 * This class defines Fat Bird behaviour.
 * 
 * @author Group 9
 * 
 * */

public class FatBird : AllBirdsScript {
	private Rigidbody2D _rBody2D;
	private float _maxMass;

	// Use this for initialization
	void Start () {
		_rBody2D = GetComponent<Rigidbody2D>();
		_maxMass = _rBody2D.mass * 2;
	}
	
	// Update is called once per frame
    override public void SpecialBehaviour() {
		if (isFired && _rBody2D.mass != _maxMass)
		{
			_rBody2D.mass = _rBody2D.mass * 2;
        }      
	}

    void OnCollisionEnter2D(Collision2D collider) {
        makeSquirt(collider, "ParticleSystem/LargeRedBirdFeatherSquirt");
    }
}
