using UnityEngine;
using System.Collections;

/**
 * This class defines the Aliens. It generates movement of each individual alien and makes sure an alien lets MoveAlien know theyve encountered an edge.
 * It also defines what an alien should do upon getting hit by a trigger(laser).
 * 
 * @author Anders Mikkelsen
 * 
 * */

public class Alien : MonoBehaviour {
    public delegate void OnPoints(int points);
    public static event OnPoints onPointsListener;

    public delegate void AtEdge();
    public static event AtEdge atEdgeListener;

    public int pointValue;
    private bool elligible = false;
    private bool tested = false;

    private Vector3 pos;
    private float xMaxDistance;
    private float xMinDistance;


    void Start() {
        xMaxDistance = GameManager.Instance.XMaxDistance;
        xMinDistance = GameManager.Instance.XMinDistance;
        GameManager.onStateChangedListener += onStateChangedListener;
    }

    void onStateChangedListener(GameState gameState) {
        if (gameState == GameState.GAME_OVER) {
            GameManager.onStateChangedListener -= onStateChangedListener;
        }
    }

    void Update() {
        pos = transform.position;

        if (elligible) {
            if ((pos.x >= xMaxDistance || pos.x <= xMinDistance)) {
                Switch();
            }        
        }
            
        if (!tested && !elligible) {
            StartCoroutine(checkElligible());
        } else if (elligible) {
            if (Random.Range(1, 700) == 699) { // This random number serves as a way to generate periodic shots, but not in a predictable way.
                Instantiate(Resources.Load("Laser/AlienLaser"), new Vector3(transform.position.x, transform.position.y - 0.3f, transform.position.z), new Quaternion(0, 0, 0, 0));
            }
        }
    }

    private void Switch() {
        atEdgeListener();
    }
    IEnumerator checkElligible() {
        tested = true;
        yield return new WaitForSeconds(Random.Range(0, 3)); // This is a random timer to generate few raycasts, and generate them at different times. This to preserve resources, as it doesnt have to happen immediatly.
        RaycastHit2D hit = Physics2D.Raycast(new Vector2(transform.position.x, transform.position.y - 0.2f), Vector3.down);
        if (hit.collider == null || !(hit.collider.tag.Equals("Alien"))) {
            elligible = true;
        }
        tested = false;
    }

    void OnTriggerEnter2D(Collider2D collider) {        
        specialBehaviour(collider);  
        StartCoroutine(KillAlien());
        SendPoints(pointValue);
    }
    public void SendPoints(int points) {
        onPointsListener(points); // sends points to the Score Counter
    }

    public IEnumerator KillAlien() {
        GameObject.FindGameObjectWithTag("Level").GetComponent<AudioSource>().Play();
        Destroy(gameObject.GetComponent<Collider2D>());
        gameObject.GetComponent<Animator>().SetBool("dead", true);             
        yield return new WaitForSeconds(0.5f);
        DestroyObject(gameObject);
    }

    virtual public void specialBehaviour(Collider2D collider) {
        DestroyObject(collider.gameObject);
    }

    void OnDestroy() {
        if (GameObject.FindGameObjectsWithTag("Alien").Length == 0) { // Decided to go with this variant for checking for aliens. Couldve had a static value in the class, but i prefer this as its less prone to fuckup.
            GameManager.Instance.endGame(true);
        }
    }
}
