using UnityEngine;
using System.Collections;

/**
 * This class defines the bird shooter.
 * 
 * @author Group 9
 * 
 * 
 * */

public class Shooter : MonoBehaviour {
    public GameObject bird;
    public GameObject tetherPoint;
    public bool fire;

    private Vector3 lineStartPos;
    private Color brown;
    public AudioClip stretch;
    public AudioClip launch;
    private bool played = false;
    private AudioSource _Audio;

	// Use this for initialization
	void Start () {
        fire = false;
        gameObject.AddComponent<AudioSource>();
        _Audio = GetComponent<AudioSource>();

        lineStartPos = tetherPoint.transform.position;

        LineRenderer lineRenderer = gameObject.AddComponent<LineRenderer>();
        lineRenderer.material = new Material(Shader.Find("Particles/Alpha Blended"));

        brown = new Color(0.329F, 0.159F, 0.048F);
        lineRenderer.SetColors(brown, brown);
        lineRenderer.SetWidth(0.12f, 0.12f);
        lineRenderer.SetVertexCount(3);

        GameManager.onGameStateChangedListener += stateListener;
	}
	
	// Update is called once per frame
    void Update() {
        if (fire) {
            if (tetherPoint.transform.position == lineStartPos) {
                played = false;
            }
            
            if (tetherPoint.transform.position != lineStartPos && !played) {
                _Audio.PlayOneShot(stretch, 1);
                played = true;
            }

            LineRenderer lineRenderer = GetComponent<LineRenderer>();
            lineRenderer.SetPosition(0, lineStartPos + new Vector3(0.28f, 0, 0));
            lineRenderer.SetPosition(1, tetherPoint.transform.position);
            lineRenderer.SetPosition(2, lineStartPos + new Vector3(-0.16f, 0, 0));

            bird.rigidbody2D.velocity = Vector2.zero;
            bird.transform.position = tetherPoint.transform.position;
            Vector2 pos = tetherPoint.transform.position;

            if (Input.GetAxis("Horizontal") > 0 && pos.x < transform.position.x) {
                pos.x += Input.GetAxis("Horizontal") * Time.deltaTime;
            } else if (Input.GetAxis("Horizontal") < 0 && pos.x > (transform.position.x - 1.2f)) {
                pos.x += Input.GetAxis("Horizontal") * Time.deltaTime;
            }

            if (Input.GetAxis("Vertical") > 0 && pos.y < (transform.position.y + 0.3f)) {
                pos.y += Input.GetAxis("Vertical") * Time.deltaTime;
            } else if (Input.GetAxis("Vertical") < 0 && pos.y > (transform.position.y - 1.3f)) {
                pos.y += Input.GetAxis("Vertical") * Time.deltaTime;
            }

            tetherPoint.transform.position = pos;
            bird.transform.position = tetherPoint.transform.position;

            // This is where the bird is actually fired

            if (Input.GetKey(KeyCode.Space)) {
                fire = false;
                bird.rigidbody2D.AddForce((transform.position - tetherPoint.transform.position) * 1000.0f);
                tetherPoint.transform.position = transform.position;
                GameManager.Instance.ShotFired();

                _Audio.PlayOneShot(launch, 1);
                lineRenderer.SetPosition(1, tetherPoint.transform.position);
            }
        }
    }

    private void stateListener(GameState current){
        if (current == GameState.GAME_LEVEL_WAITING) {
            fire = true;
        } else if (current == GameState.GAME_LEVEL_COMPLETED) {
            fire = false;
            GameManager.onGameStateChangedListener -= stateListener;
        } else {
            fire = false;
        }
	}

    public GameObject Bird {
        get {return bird;}
        set {this.bird = value; }    
    }
}
