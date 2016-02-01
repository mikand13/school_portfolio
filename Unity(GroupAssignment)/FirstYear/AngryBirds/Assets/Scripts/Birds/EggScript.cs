using UnityEngine;
using System.Collections;

/**
 * This class defines egg behaviour.
 * 
 * @author Group 9
 * 
 * */

public class EggScript : MonoBehaviour {
	public float waitTime = 5f;
	public bool hasHit;
	private Animator _anim;

	
	void Start () {
		_anim = GetComponent<Animator>();
	}

	void OnCollisionEnter2D(Collision2D coll) {
        if (coll.gameObject.tag != "Bird") {
            if (!hasHit) {
                StartCoroutine(destroySelf(waitTime));
                hasHit = true;
            }
        }	
	}

	IEnumerator destroySelf(float waitTime) {
		yield return new WaitForSeconds(waitTime);
		_anim.SetBool ("isDead",hasHit);
		yield return new WaitForSeconds(waitTime);
		Destroy (gameObject);
	}
}
