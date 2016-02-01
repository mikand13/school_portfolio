using UnityEngine;
using System.Collections;

/**
 * This class defines black bird behaviour.
 * 
 * @author Group 9
 * 
 * */

public class BlackBirdScript : AllBirdsScript {

	public GameObject egg;
	public int numberOfEggs = 3;

	private bool layingEgg;
	public float waitTime = 1f;

    override public void SpecialBehaviour() {
		if (isFired && numberOfEggs != 0 && Input.GetButtonDown("Jump")) {
			StartCoroutine(LayEgg(waitTime));
		    anim.SetBool ("layEgg",layingEgg);
		}
	}

    void OnCollisionEnter2D(Collision2D collider) {
        makeSquirt(collider, "ParticleSystem/BlackBirdFeatherSquirt");
    }

	IEnumerator LayEgg(float waitTime) {
		layingEgg = true;
		Instantiate(egg, new Vector3(transform.position.x,transform.position.y-0.6f,0f), new Quaternion(0f,0f,0f,0f) );
		numberOfEggs--;
		yield return new WaitForSeconds(waitTime);
		layingEgg = false;
	}
}
