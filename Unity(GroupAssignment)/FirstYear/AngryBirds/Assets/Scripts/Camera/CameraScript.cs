using UnityEngine;
using System.Collections;
[RequireComponent (typeof (Rigidbody2D))]

/**
 * This class defines the Main Camera. It acts according to information sent from the Game Manager and gives movement permission to the player based on this.
 * 
 * @author Group 9
 * 
 * */

public class CameraScript : MonoBehaviour {

	public float cameraSpeed = 1f;
	public float minX;
	public float maxX;

	//Midlertidig. Skal ikke være bunnet direkte til kula.
	public GameObject bullet;

	private bool _isLocked;
    private bool isDone;
    private bool panForward;
    private bool panBack;

	// Use this for initialization
	void Start () {
        GameManager.onGameStateChangedListener += stateListener;
        GameManager.Instance.CameraReady();
        if (Application.loadedLevelName == "EndScene") {
            Instantiate(Resources.Load("LevelFinish/Game_Over"), new Vector3(minX, -3f, 0f), new Quaternion(0, 0, 0, 0));
        }
	}
	
	// Update is called once per frame
	void Update ()
	{
        Vector3 pos = transform.position;
        if (panForward || panBack) {
            if (panForward && panBack) {
                Invoke("panCamera", 3);
            } else {
                panCamera();                
            }
        } else if (_isLocked == false) {
            if (transform.position.x > minX) {
		        if (Input.mousePosition.x < Screen.width / 10) {
                    pos.x -= cameraSpeed * Time.deltaTime;
			        _isLocked = false;
		        }

            }

            if (transform.position.x < maxX) {
                if (Input.mousePosition.x > (Screen.width / 10) * 9) {
                    pos.x += cameraSpeed * Time.deltaTime;
			        _isLocked = false;
		        }
            }

            transform.position = pos;

		    if (Input.GetButtonDown ("Jump")) {
			    _isLocked = true;
		    }

            // This if() changes the fieldofview and pos og the cam to compensate for high flying birds.

        } else if (_isLocked) {
            if (transform.position.x < maxX) {
                if (!isDone) {
                    Vector3 pos2 = transform.position;
                    pos2.x = bullet.transform.position.x;
                    float fieldOfViewChange;
                    if (bullet.transform.position.y > transform.position.y) {
                        fieldOfViewChange = bullet.transform.position.y * 10;
                    } else {
                        fieldOfViewChange = 0;
                    }

                    if (fieldOfViewChange < 30 && fieldOfViewChange > 0) {
                        camera.fieldOfView = 50 + fieldOfViewChange;
                        float temp = camera.fieldOfView;
                        pos2.y = (80f - temp) * -0.1f;
                        transform.position = pos2;
                    } else {
                        transform.position = pos2;
                    }
                }
            }
        }
	}

    // This method pans the camera at the start of a level and tells the Game Manager it has been done.

    private void panCamera() {
        //Debug.Log("PANMODE!");
        if (panForward || panBack) {
             if (panBack) {
                panForward = false;
                // Debug.Log("PANNING BACKWARD!");
                Vector3 pos = transform.position;
                pos.x -= 10 * Time.deltaTime;
                transform.position = pos;

                if (transform.position.x <= minX) {
                    panForward = false;
                    panBack = false;
                    // Debug.Log("LEVEL READY!");
                    GameManager.Instance.PanDone();
                }
            } else if (panForward) {
                //Debug.Log("PANNING FORWARD!");
                Vector3 pos = transform.position;
                pos.x += 10 * Time.deltaTime;
                transform.position = pos;

                if (transform.position.x >= maxX) {
                    panBack = true;
                }
            }
        }
    }

    // This method accepts an event from the Game Manager and sets the Camera variables.

    private void stateListener(GameState current) {
        if (current == GameState.GAME_LEVEL_INIT) {
            if (!(Application.loadedLevelName == "EndScene")) {
                panForward = true;
            } else {
                GameManager.onGameStateChangedListener -= stateListener;
                camera.fieldOfView = 50f;
                transform.position = new Vector3(minX, -3f, -10f);
                _isLocked = true;
                isDone = true;
            }
        } else if (current == GameState.GAME_LEVEL_WAITING) {
            camera.fieldOfView = 50f;
            transform.position = new Vector3(minX, -3f, -10f);
            _isLocked = false;
        } else if (current == GameState.GAME_LEVEL_ACTIVE) {
            bullet = GameObject.Find("Shooter").GetComponent<Shooter>().Bird;
        } else if (current == GameState.GAME_LEVEL_COMPLETED) {
            camera.fieldOfView = 50f;
            transform.position = new Vector3(minX, -3f, -10f);
            _isLocked = true;
            isDone = true;
            GameManager.onGameStateChangedListener -= stateListener;
        }
    }
}
