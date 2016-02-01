using UnityEngine;
using System.Collections;

public class PacManMove : MonoBehaviour {
    private GameManager gameManager;
    private GameManager.Direction myDirection;
    public delegate void PacManPosition (Vector3 transform);
    static public event PacManPosition pacManListener;

	public LayerMask levelLayer;
	
    public MeshFilter level;
    public MeshFilter pacManMesh;
	
    public AudioClip eatPill;
    public AudioClip superModeClip;
    public AudioClip blueMode;
	
    public FollowCam followCam;
    public Rigidbody bullet;
    
    private float LEVEL_SIZE;
    private float DESTROY_BULLET_TIME;
	public bool superMode;

    public float power;
	private float moveSpeed;
    private Vector3 startPos;
	private Vector3 curPos;

    public bool SuperMode {
        get { return this.superMode; }
        set { this.superMode = value; }
    }

    public float MoveSpeed {
        get { return this.moveSpeed; }
        set { this.moveSpeed = value; }
    }

	void Awake() {
        gameManager = (GameManager)(GameObject.Find("GameManager").GetComponent<GameManager>());

		LEVEL_SIZE = level.renderer.bounds.max.x * level.renderer.bounds.max.z;
        DESTROY_BULLET_TIME = 1.0f;
		gameManager.PacMan = this;
	}

	void Start() {
        moveSpeed = 10.0f;
		startPos = transform.position;
		curPos = transform.position;

        gameManager.allPills = GameObject.FindGameObjectsWithTag("pill");
        gameManager.remainingPills = (uint)(gameManager.allPills.Length + 4);
	}

	void FixedUpdate () {
        pacManListener(transform.position);

		if (superMode) {
            if (Input.GetButton("Fire1")) {
                Rigidbody instance = Instantiate(bullet, transform.position, transform.rotation) as Rigidbody;
                Vector3 fwd = transform.TransformDirection(Vector3.back);
                instance.AddForce(fwd * power);

                Destroy(instance.gameObject, DESTROY_BULLET_TIME);
            }

            if (followCam.InPosition) {
                float tiltAroundY = followCam.TiltAroundY;
                float moveForward = Input.GetAxis("Vertical");

                if (myDirection == GameManager.Direction.North) {
                    if (tiltAroundY < 280 && tiltAroundY > 260) {
                        myDirection = GameManager.Direction.West;           
                    } else if (tiltAroundY > 80 && tiltAroundY < 170) {
                        myDirection = GameManager.Direction.East;
                    }
                    moveNorth(moveForward);
                } else if (myDirection == GameManager.Direction.East) {
                    if (tiltAroundY > 170) {
                        myDirection = GameManager.Direction.South;
                    } else if (tiltAroundY < 10) {
                        myDirection = GameManager.Direction.North;
                    }
                    moveEast(moveForward);
                } else if (myDirection == GameManager.Direction.South) {
                    if (tiltAroundY > 260) {
                        myDirection = GameManager.Direction.West;
                    } else if (tiltAroundY < 100) {
                        myDirection = GameManager.Direction.East;
                    }
                    moveSouth(moveForward);
                } else if (myDirection == GameManager.Direction.West) {
                    if (tiltAroundY > 350) {
                        myDirection = GameManager.Direction.North;
                    } else if (tiltAroundY < 190) {
                        myDirection = GameManager.Direction.South;
                    }
                    moveWest(moveForward);
                }
            }
		} else {
			if (Input.GetAxis ("Right") > 0) {
                myDirection = GameManager.Direction.East;
                moveEast(Input.GetAxis("Right"));
			} else if (Input.GetAxis ("Left") > 0) {
                myDirection = GameManager.Direction.West;
                moveWest(Input.GetAxis("Left"));
			} else if (Input.GetAxis ("Up") > 0) {
                myDirection = GameManager.Direction.North;
                moveNorth(Input.GetAxis("Up"));
			} else if (Input.GetAxis ("Down") > 0) {
                myDirection = GameManager.Direction.South;
                moveSouth(Input.GetAxis("Down"));
			}
		}

        if (curPos.x >= level.renderer.bounds.max.x) {
            curPos.x = level.renderer.bounds.min.x;
        } else if (curPos.x <= level.renderer.bounds.min.x) {
            curPos.x = level.renderer.bounds.max.x;
        }

        transform.position = curPos;
        setRotation();
	}

    private void setRotation() {
        Quaternion rotation = transform.rotation;

        if (myDirection == GameManager.Direction.North) {
            rotation = Quaternion.AngleAxis(180, Vector3.up);
        } else if (myDirection == GameManager.Direction.South) {
            rotation = Quaternion.AngleAxis(0, Vector3.up);
        } else if (myDirection == GameManager.Direction.East) {
            rotation = Quaternion.AngleAxis(270, Vector3.up);
        } else if (myDirection == GameManager.Direction.West) {
            rotation = Quaternion.AngleAxis(90, Vector3.up);
        }

        transform.rotation = rotation;
    }

    private void moveEast(float moveForward) {
        if (genHit(levelLayer)) {
            curPos.x += moveForward * moveSpeed * Time.deltaTime;
        }
    }

    private void moveWest(float moveForward) {
        if (genHit(levelLayer)) {
            curPos.x -= moveForward * moveSpeed * Time.deltaTime;
        }
    }

    private void moveNorth(float moveForward) {
        if (genHit(levelLayer)) {
            curPos.z += moveForward * moveSpeed * Time.deltaTime;
        }
    }

    private void moveSouth(float moveForward) {
        if (genHit(levelLayer)) {
            curPos.z -= moveForward * moveSpeed * Time.deltaTime;
        }
    }

	private bool genHit(LayerMask levelLayer) {
		RaycastHit hitLeft;
		RaycastHit hitRight;

        float OFFSET_DISTANCE = 0.7f;
		float CHECK_DIST = 1.0f;
		float CHANGE_POS = 0.05f;
		
		bool air = false;
		bool air2 = false;
        		
		Vector3 right = transform.position;
		Vector3 rightFar = genTemp (); 
		Vector3 left = transform.position;
		Vector3 leftFar = genTemp ();

        if (myDirection == GameManager.Direction.North) {
			right.x += OFFSET_DISTANCE;
			rightFar.x += OFFSET_DISTANCE;
			left.x -= OFFSET_DISTANCE;
			leftFar.x -= OFFSET_DISTANCE;
			air = Physics.Raycast (left, leftFar, out hitLeft, LEVEL_SIZE, levelLayer);
			air2 = Physics.Raycast (right,rightFar, out hitRight, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(left, leftFar, Color.white);
            Debug.DrawRay(right, rightFar, Color.white);
        } else if (myDirection == GameManager.Direction.South) {
			right.x -= OFFSET_DISTANCE;
			rightFar.x -= OFFSET_DISTANCE;
			left.x += OFFSET_DISTANCE;
			leftFar.x += OFFSET_DISTANCE;
			air = Physics.Raycast (left, leftFar, out hitLeft, LEVEL_SIZE, levelLayer);
			air2 = Physics.Raycast (right,rightFar, out hitRight, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(left, leftFar, Color.white);
            Debug.DrawRay(right, rightFar, Color.white);
        } else if (myDirection == GameManager.Direction.East) {
			right.z -= OFFSET_DISTANCE;
			rightFar.z -= OFFSET_DISTANCE;
			left.z += OFFSET_DISTANCE;
			leftFar.z += OFFSET_DISTANCE;
			air = Physics.Raycast (left, leftFar, out hitLeft, LEVEL_SIZE, levelLayer);
			air2 = Physics.Raycast (right,rightFar, out hitRight, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(left, leftFar, Color.white);
            Debug.DrawRay(right, rightFar, Color.white);
        } else if (myDirection == GameManager.Direction.West) {
			right.z += OFFSET_DISTANCE;
			rightFar.z += OFFSET_DISTANCE;
			left.z -= OFFSET_DISTANCE;
			leftFar.z -= OFFSET_DISTANCE;
			air = Physics.Raycast (left, leftFar, out hitLeft, LEVEL_SIZE, levelLayer);
			air2 = Physics.Raycast (right,rightFar, out hitRight, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(left, leftFar, Color.white);
            Debug.DrawRay(right, rightFar, Color.white);
		} else {
			hitRight = new RaycastHit();	
			hitLeft = new RaycastHit();
		}

		if ((hitLeft.distance < CHECK_DIST && hitRight.distance > CHECK_DIST)
		    || (hitRight.distance < CHECK_DIST && hitLeft.distance > CHECK_DIST)
		    || (hitLeft.distance < CHECK_DIST && air2 == false)
		    || (hitRight.distance < CHECK_DIST && air == false)) {
                if (myDirection == GameManager.Direction.North) {
				    if (hitLeft.distance < CHECK_DIST) {
                        moveXRight(hitLeft.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, left, leftFar);
				    } else if (hitRight.distance < CHECK_DIST) {
                        moveXLeft(hitRight.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, right, rightFar);
				    }
                } else if (myDirection == GameManager.Direction.South) {
				    if (hitLeft.distance < CHECK_DIST) {
                        moveXLeft(hitLeft.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, left, leftFar);
				    } else if (hitRight.distance < CHECK_DIST) {
                        moveXRight(hitRight.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, right, rightFar);
				    }
                } else if (myDirection == GameManager.Direction.East) {
				    if (hitLeft.distance < CHECK_DIST && air == true) {
                        moveZLeft(hitLeft.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, left, leftFar, air);
				    } else if (hitRight.distance < CHECK_DIST) {
                        moveZRight(hitRight.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, right, rightFar, air2);
				    }
                } else if (myDirection == GameManager.Direction.West) {
				    if (hitLeft.distance < CHECK_DIST && air == true) {
                        moveZRight(hitLeft.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, left, leftFar, air);
				    } else if (hitRight.distance < CHECK_DIST) {
                        moveZLeft(hitRight.distance, CHECK_DIST, CHANGE_POS, OFFSET_DISTANCE, right, rightFar, air2);
				    }
			    } 
			return true;
		} else if (hitLeft.distance > CHECK_DIST && hitRight.distance > CHECK_DIST) {
			return true;
		} else if (air == false && air2 == false) {
			return true;
		} else {
			return false;		
		}
	}

    private void moveXRight(float distance, float CHECK_DIST, float CHANGE_POS, float OFFSET_DISTANCE, Vector3 ray, Vector3 rayFar) {
        while (distance < CHECK_DIST) {
            curPos.x += CHANGE_POS;
            transform.position = curPos;
            ray.x = (curPos.x += OFFSET_DISTANCE);

            RaycastHit leftHit;
            Physics.Raycast(ray, rayFar, out leftHit, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(ray, rayFar, Color.white, 1.2f);

            distance = leftHit.distance;
        }
    }

    private void moveXLeft(float distance, float CHECK_DIST, float CHANGE_POS, float OFFSET_DISTANCE, Vector3 ray, Vector3 rayFar) {
        while (distance < CHECK_DIST) {
            curPos.x -= CHANGE_POS;
            transform.position = curPos;
            ray.x = (curPos.x -= OFFSET_DISTANCE);

            RaycastHit leftHit;
            Physics.Raycast(ray, rayFar, out leftHit, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(ray, rayFar, Color.white, 1.2f);

            distance = leftHit.distance;
        }
    }

    private void moveZRight(float distance, float CHECK_DIST, float CHANGE_POS, float OFFSET_DISTANCE, Vector3 ray, Vector3 rayFar, bool air) {
        while (distance < CHECK_DIST && air == true) {
            curPos.z += CHANGE_POS;
            transform.position = curPos;
            ray.z = (curPos.z += OFFSET_DISTANCE);

            RaycastHit rightHit;
            air = Physics.Raycast(ray, rayFar, out rightHit, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(ray, rayFar, Color.white, 1.2f);

            distance = rightHit.distance;
        }
    }

    private void moveZLeft(float distance, float CHECK_DIST, float CHANGE_POS, float OFFSET_DISTANCE, Vector3 ray, Vector3 rayFar, bool air) {
        while (distance < CHECK_DIST && air == true) {
            curPos.z -= CHANGE_POS;
            transform.position = curPos;
            ray.z = (curPos.z -= OFFSET_DISTANCE);

            RaycastHit rightHit;
            air = Physics.Raycast(ray, rayFar, out rightHit, LEVEL_SIZE, levelLayer);
            Debug.DrawRay(ray, rayFar, Color.white, 1.2f);

            distance = rightHit.distance;
        }
    }
	
	private Vector3 genTemp() {
		Vector3 temp = transform.position;

        if (myDirection == GameManager.Direction.North) {
			temp.z = temp.z + LEVEL_SIZE;
        } else if (myDirection == GameManager.Direction.South) {
			temp.z = temp.z - LEVEL_SIZE;
        } else if (myDirection == GameManager.Direction.East) {
            temp.x = temp.x + LEVEL_SIZE;
        } else if (myDirection == GameManager.Direction.West) {
            temp.x = temp.x - LEVEL_SIZE;
        }
		
		return temp;
	}

	private void OnTriggerEnter (Collider collision) {
		if (collision.tag == "pill" || collision.tag == "powerUp") {
			gameManager.RemovePill(collision);
			collision.gameObject.SetActive(false);
			Camera.main.GetComponent<AudioSource>().PlayOneShot(eatPill);
		    
            if (collision.tag == "powerUp") {
			    Camera.main.GetComponent<AudioSource>().PlayOneShot(blueMode);		
		    }
		}  
	}

	public void resetPacMan() {
		transform.position = startPos;
		curPos = transform.position;
	}
    
    public void superModeMusic() {
        AudioSource player = gameObject.GetComponent<AudioSource>();
        player.PlayDelayed(5.0f);
    }
}