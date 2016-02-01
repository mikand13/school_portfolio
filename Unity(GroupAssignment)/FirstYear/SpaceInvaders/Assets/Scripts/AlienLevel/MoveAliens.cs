using UnityEngine;
using System.Collections;

/**
 * This class is the alien controller. I decided the easiest and most effective / least resource intensive would be to have all the aliens be moved by one
 * GameObject. As they are supposed to move in a group anyway this is better than 55 transforms updating every frame.
 * 
 * This class listens to the aliens and switches direction when an alien informs it's at one of the edges.
 * 
 * I have also implemented the Mothership spawning here instead of in the GameManager so as to have all the Alien movement and generation in one script.
 * 
 * @author Anders Mikkelsen
 * */

public class MoveAliens : MonoBehaviour {
    public float motherShipSpawnTime;
    private bool gameRunning;
    private bool spawnMothership;
    private bool spawnIt;

    //Made these public so they are changeable in the editor.
    public float xMovePerFrame;
    public float yMovePerFrame;

    private bool switchX;
    private bool switched;

    void Start() {
        switchX = false;
        switched = false;
        gameRunning = false;
        spawnMothership = true;
        spawnIt = false;

        Alien.atEdgeListener += atEdgeListener;
        GameManager.onStateChangedListener += stateChangedListener;
    }

    // Update is called once per frame
    void Update() {
        if (gameRunning == true) {
            if (switchX) {
                transform.Translate(Vector3.left * xMovePerFrame * Time.deltaTime);
            } else {
                transform.Translate(Vector3.right * xMovePerFrame * Time.deltaTime);
            }

            // Mothership spawner, with IEnumerator delay method.

            if (spawnIt) {
                spawnIt = false;
                spawnMothership = true;
                Instantiate(Resources.Load("Mothership/Mothership"), new Vector3(-2.0f, 0.27f, 0.0f), new Quaternion(0, 0, 0, 0));
            }

            if (spawnMothership) {
                StartCoroutine(MothershipTimer());
            }
        }
    }

    private IEnumerator MothershipTimer() {
        spawnMothership = false;
        yield return new WaitForSeconds(motherShipSpawnTime);
        if (!GameManager.Instance.GameOver) {
            spawnIt = true;        
        }
    }

    public void atEdgeListener() {
        // This ensures you dont switch twice
        if (!switched) {
            transform.Translate(Vector3.down * yMovePerFrame);
            switchX = !switchX;
            switched = true;
            StartCoroutine(switchDone());
        }
    }

    IEnumerator switchDone() {
        yield return new WaitForSeconds(1);
        switched = false;
    }

    public void stateChangedListener(GameState gameState) {
        if (gameState == GameState.LEVEL_RUNNING) {
            gameRunning = true;
        } else if (gameState == GameState.GAME_OVER) {
            Alien.atEdgeListener -= atEdgeListener;
            GameManager.onStateChangedListener -= stateChangedListener;
        }
    }

    // These get methods allow the aliens to inherit the level size and the speed so as to anticipate an edge.

    public float XMovePerFrame {
        get { return xMovePerFrame; }
    }

    public float YMovePerFrame {
        get { return yMovePerFrame; }
    }
}
