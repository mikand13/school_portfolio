using UnityEngine;
using System.Collections;

/**
 * This class is the player contoller. It controls movement to the right and left, and fires LaserShot objects.
 * It also listens for hit from the laser, so that when the laser hits/exits screen you can fire again, as per the original game.
 * 
 * @author Anders Mikkelsen
 * */

public class PlayerController : MonoBehaviour {
    public float moveSpeed;
    private bool fire;
    private bool move;

    private Vector3 startPos;

    private float xMaxDistance;
    private float xMinDistance;

    void Start() {
        fire = true;
        GameManager.onStateChangedListener += onStateChangedListener;

        xMaxDistance = GameManager.Instance.XMaxDistance;
        xMinDistance = GameManager.Instance.XMinDistance;

        startPos = transform.position;

        move = true;
    }

	void Update () {
        if (move) {
            if (Input.GetAxis("Horizontal") > 0 && transform.position.x < xMaxDistance - 0.1f) {
                transform.Translate(Vector3.right * moveSpeed * Time.deltaTime);
            } else if (Input.GetAxis("Horizontal") < 0 && transform.position.x > xMinDistance + 0.1f) {
                transform.Translate(Vector3.left * moveSpeed * Time.deltaTime);
            }
        }

        if (Input.GetButtonUp("Laser")) {
            if (fire) {                
                Instantiate(Resources.Load("Laser/Laser"), new Vector3(transform.position.x, transform.position.y + 0.2f, transform.position.z), new Quaternion(0, 0, 0, 0));
                gameObject.GetComponent<AudioSource>().Play();
                LaserShot.onHitListener += OnHitListener;
                fire = false;
            }
        }
	}

    public void onStateChangedListener(GameState gameState) {
        if (gameState == GameState.GAME_OVER) {
            GameManager.onStateChangedListener -= onStateChangedListener;
            fire = false;
            if (GameManager.Instance.Win) {
                move = false;
                transform.position = startPos;            
            }
        }
    }

    public void OnHitListener(bool hit) {
        fire = hit;
        LaserShot.onHitListener -= OnHitListener;
    }

    void OnTriggerEnter2D(Collider2D collider) {
        DestroyObject(collider.gameObject);
        GameManager.Instance.endGame(false);
        DestroyObject(gameObject);
    }

    void OnCollisionEnter2D(Collision2D collider) { // this ensures that the player dies if the aliens come too far down.
        if (collider.gameObject.tag.Equals("Alien")) {
            DestroyObject(collider.gameObject);
            GameManager.Instance.endGame(false);
            DestroyObject(gameObject);
        }
    }
}
