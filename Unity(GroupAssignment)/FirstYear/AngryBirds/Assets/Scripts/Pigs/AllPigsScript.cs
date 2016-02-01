using UnityEngine;
using System.Collections;

/**
 * This class defines behaviour for all pigs.
 * 
 * @author Group 9
 * 
 * */

public class AllPigsScript : MonoBehaviour {

	public float waitTime = 0.35f;

	private bool isDead = false;

	public AudioClip die;

	private AudioSource _Audio;

	private Animator _Anim;


	public Transform headCheck;
	public LayerMask whatIsDangerous;
	public bool isHitInHead;
	public float headRadius = 0.5f;

	// Use this for initialization
	void Start () {
		_Anim = GetComponent<Animator>();
		_Audio = GetComponent<AudioSource>();
	}
	
	// When an object collides with this, destroy this
	void OnCollisionEnter2D(Collision2D i){
		if(i.gameObject.tag == "Bird" && !isDead){
			isDead = true;
			_Audio.PlayOneShot(die, 1);
			StartCoroutine(WaitAndDie(waitTime));
		}
		// Billig. Veldig billig.
		if ((transform.rotation.z > 0.3 || transform.rotation.z < -0.3) && !isDead)
		{
			isDead = true;
			_Audio.PlayOneShot(die, 1);
			StartCoroutine(WaitAndDie(waitTime));
		}

	}

	void FixedUpdate()
	{
		isHitInHead = Physics2D.OverlapCircle (headCheck.position, headRadius, whatIsDangerous);
		if (isHitInHead) {
			if(!isDead){
				isDead = true;
				StartCoroutine(WaitAndDie(waitTime));
			}

		}
	}

    public void OnDie() {
        GameManager.Instance.OnGameObjectDie(gameObject);
    }

	IEnumerator WaitAndDie(float waitTime) {
		_Anim.SetBool ("isDead", isDead);
		yield return new WaitForSeconds(waitTime);
        OnDie();
	}
}
