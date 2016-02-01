using UnityEngine;
using System.Collections;
[RequireComponent (typeof (Rigidbody2D))]
[RequireComponent (typeof (Animator))]

/**
 * This class defines superclass for all birds.
 * 
 * @author Group 9
 * 
 * */

public class AllBirdsScript : MonoBehaviour {

    public bool isFired;
    public bool goTime;
	public AudioClip onWinSound;
	private AudioSource _ASource;
	private bool _hasPlayed;
	private float vSpeed;
    protected bool flying;
	[HideInInspector]
	public Animator anim;
    public bool isDead = false;

    void Awake() {
        anim = GetComponent<Animator>();
		_ASource = GetComponent<AudioSource> ();
        GameManager.onGameStateChangedListener += stateListener;
    }

    void Update () {
        anim.SetFloat ("vSpeed", rigidbody2D.velocity.y);
        if (flying == true) {
            if (rigidbody2D.velocity.x <= 0.1 && rigidbody2D.velocity.y <= 0.1 && transform.position.x >= GameObject.Find("Shooter").GetComponent<Shooter>().tetherPoint.transform.position.x) {
                Invoke("waitCoRoutine", 4.0f);                
            }
            SpecialBehaviour();
        }

		if (anim.GetBool ("isDead")) {
			if(!_hasPlayed){
				_ASource.PlayOneShot(onWinSound,1);
				_hasPlayed = true;
			}
		}
    }

    virtual public void SpecialBehaviour() {
    
    }

    public void waitCoRoutine() {
        GameManager.onGameStateChangedListener -= stateListener;
        GameManager.Instance.BirdStopped(gameObject);
    }

    public void stateListener(GameState current) {
        if (current == GameState.GAME_LEVEL_ACTIVE && goTime == true) {
            flying = true;
            StartCoroutine(SetFlying());
        }
    }

    public IEnumerator SetFlying() {
        yield return new WaitForSeconds(0.5f);
        isFired = true;
    }

    public bool Flying {
        get { return flying; }
    }

    public bool GoTime {
        set { this.goTime = value; }
    }

    public Animator Anim {
        get { return anim; }
    }

    public IEnumerator KillFeathers(Object go) {
        yield return new WaitForSeconds(0.9f);
        DestroyObject(go);
    }

    // anim method for the feathersquirt

    public void makeSquirt(Collision2D collider, string location) {
        if (collider.collider.tag.Equals("Pig") || collider.collider.tag.Equals("Material")) {
            Object go = Instantiate(Resources.Load(location), transform.position, new Quaternion(0, 0, 0, 0));
            StartCoroutine(KillFeathers(go));
        }
    }
}
