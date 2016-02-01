using UnityEngine;
using System.Collections;

public class GhostRandomMove : MonoBehaviour {
    private GameManager gameManager;
	private GameManager.Direction myDirection;
    private GameManager.GAME_STATE currentState;

	public LayerMask levelLayer;
	public MeshFilter level;
	public AudioClip deathGhost;
	public AudioClip deathPacMan;

    private const int DELAY_VALUE = 20;
	private const float CHECK_DIST = 2.0f;

	private float LEVEL_SIZE;
	private float MOVE_SPEED;
	
	private float spawnTime;
	private float vulnTime;

    private int intersectionDelay;
	
	private Vector3 pos;
	private Vector3 startPos;
	private Vector3 originalPos;
    private Vector3 homeCornerPos;
    private Vector3 pacManPos;

	private bool intersected;
	private bool teleported;
	private bool vulnerable;
	private bool activated;

	void Awake () {
        gameManager = (GameManager) (GameObject.Find("GameManager").GetComponent<GameManager>());
		LEVEL_SIZE = level.renderer.bounds.max.x * level.renderer.bounds.max.z;

        homeCornerPos = transform.position;
		
        if (this.name == "Speedy") {
			gameManager.Speedy = this;
            homeCornerPos = new Vector3(-level.renderer.bounds.max.x, 0, level.renderer.bounds.max.z);
            //Debug.Log(name + homeCornerPos);
		} else if (this.name == "Bashful") {
			gameManager.Bashful = this;
            homeCornerPos = new Vector3(level.renderer.bounds.max.x, 0, -level.renderer.bounds.max.z);
            //Debug.Log(name + homeCornerPos);
		} else if (this.name == "Pokey") {
			gameManager.Pokey = this;
            homeCornerPos = new Vector3(-level.renderer.bounds.max.x, 0, -level.renderer.bounds.max.z);
            //Debug.Log(name + homeCornerPos);
		} else if (this.name == "Shadow") {
			gameManager.Shadow = this;
            homeCornerPos = new Vector3(level.renderer.bounds.max.x, 0, level.renderer.bounds.max.z);
            //Debug.Log(name + homeCornerPos);
		}

	}

	void Start () {
        GameManager.stateListener += stateListener;
        PacManMove.pacManListener += pacManPosition;
		
        if (this.gameObject.name == "Shadow") {
			startPos = transform.position;		
		} else {
			startPos = GameObject.Find ("Shadow").transform.position;
		}
		
		originalPos = transform.position;
		pos = transform.position;

		MOVE_SPEED = 10.0f;

		intersectionDelay = 0;

		intersected = false;
		teleported = false;
		vulnerable = false;
		activated = false;

		spawnTime = Time.realtimeSinceStartup;
		startDir ();
	}

    private void stateListener(GameManager.GAME_STATE current){
<<<<<<< Updated upstream
        // Debug.Log(current);
=======
        Debug.Log(current);
>>>>>>> Stashed changes
        if (currentState != current) {
            reverseDir();
        }
        currentState = current;
	}

    private void pacManPosition(Vector3 pos) {
        // Debug.Log(pos + " is pacmanpos");
        pacManPos = pos;
    }

    private void startDir() {
        int randDir = Random.Range(1, 3);

        if (randDir == 1) {
            myDirection = GameManager.Direction.East;
        } else if (randDir == 2) {
            myDirection = GameManager.Direction.West;
        }
    }

	void FixedUpdate () {
        if (this.gameObject.name == "Shadow") {
            if (gameManager.RemainingPills == 10 + 10 * gameManager.Level) {
                MOVE_SPEED = 13.0f;
            }
        }

		float change = MOVE_SPEED * Time.deltaTime;
		checkVuln ();	
		
		if (intersected == true || intersectionDelay > 0) {
			intersectionDelay++;
			if (intersectionDelay > DELAY_VALUE) {
				intersectionDelay = 0;
				intersected = false;
			}
        } else {
			checkIntersection();
        }

		if (!(activated)) {
			if (this.name == "Speedy") {
                activate(2);
			} else if (this.name == "Bashful") {
                activate(4);
			} else if (this.name == "Pokey") {
                activate(6);
			} else {
				activated = true;
				startDir();
			}
        } else {
            if (myDirection == GameManager.Direction.North) {
				pos.z = pos.z + change;
            } else if (myDirection == GameManager.Direction.South) {
				pos.z = pos.z - change;
            } else if (myDirection == GameManager.Direction.East) {
				pos.x = pos.x + change;
            } else if (myDirection == GameManager.Direction.West) {
				pos.x = pos.x - change;
			}		
			transform.position = pos;
		}
        setRotation();
	}

	private void checkVuln () {
		if (vulnerable == true) {
			if (Time.realtimeSinceStartup - vulnTime >= 5) {
				switchVulnerable();
			}
		}
	}

    private void activate(int time) {
        if ((int)(Time.realtimeSinceStartup - spawnTime) >= time) {
            pos = startPos;
            transform.position = pos;
            startDir();
            activated = true;
        }
    }

    private void setRotation() {
        Quaternion rotation = transform.rotation;

        if (myDirection == GameManager.Direction.North) {
            rotation = Quaternion.AngleAxis(90, Vector3.up);
        } else if (myDirection == GameManager.Direction.South) {
            rotation = Quaternion.AngleAxis(270, Vector3.up);
        } else if (myDirection == GameManager.Direction.East) {
            rotation = Quaternion.AngleAxis(180, Vector3.up);
        } else if (myDirection == GameManager.Direction.West) {
            rotation = Quaternion.AngleAxis(0, Vector3.up);
        }

        transform.rotation = rotation;
    }
	
	private void checkIntersection() {
		ArrayList posDir = new ArrayList();
        GameManager.Direction tempDir = myDirection;

		Vector3 temp;
		Vector3 leftOffset;
		Vector3 rightOffset;
		
		float OFFSET_VALUE = 0.95f;

<<<<<<< Updated upstream
        if (tempDir == GameManager.Direction.North) {
            revTempDir = GameManager.Direction.South;
        } else if (tempDir == GameManager.Direction.South) {
            revTempDir = GameManager.Direction.North;
        } else if (tempDir == GameManager.Direction.East) {
            revTempDir = GameManager.Direction.West;
        } else if (tempDir == GameManager.Direction.West) {
            revTempDir = GameManager.Direction.East;
        } else {
            revTempDir = GameManager.Direction.North;
        }

=======
>>>>>>> Stashed changes
		for (int i = 0; i < 4 ; i++) {
			temp = transform.position;
            leftOffset = transform.position;
            rightOffset = transform.position;

			if (i == 0) {
				temp.z = temp.z + LEVEL_SIZE;
				leftOffset.x -= OFFSET_VALUE;
				rightOffset.x += OFFSET_VALUE;

                checkDirectionPossible(posDir, leftOffset, rightOffset, temp, GameManager.Direction.North, GameManager.Direction.South);
			}else if (i == 1) {
				temp.z = temp.z - LEVEL_SIZE;
				leftOffset.x += OFFSET_VALUE;
				rightOffset.x -= OFFSET_VALUE;

                checkDirectionPossible(posDir, leftOffset, rightOffset, temp, GameManager.Direction.South, GameManager.Direction.North);
			}else if (i == 2) {
				temp.x = temp.x + LEVEL_SIZE;
				leftOffset.z -= OFFSET_VALUE;
				rightOffset.z += OFFSET_VALUE;

                checkDirectionPossible(posDir, leftOffset, rightOffset, temp, GameManager.Direction.East, GameManager.Direction.West);
			}else if (i == 3) {
				temp.x = temp.x - LEVEL_SIZE;
				leftOffset.z += OFFSET_VALUE;
				rightOffset.z -= OFFSET_VALUE;

                checkDirectionPossible(posDir, leftOffset, rightOffset, temp, GameManager.Direction.West, GameManager.Direction.East);
			}
		}

<<<<<<< Updated upstream
        int newDirection = Random.Range(0, posDir.Count);

        if (posDir.Count > 0) {
            if (checkDir(tempDir)) {
                setNewDir(posDir, newDirection);
            } else {
                int directionChangeChance = Random.Range(1, 11);

                if (directionChangeChance == 1) {
                    if (!(checkDir(revTempDir))) {
                        myDirection = revTempDir;
                        intersected = true;
                    }
                } else if (directionChangeChance >= 2 && directionChangeChance <= 5) {
                    if (!(checkDir(tempDir))) {
                        intersected = true;
                    } else {
                        setNewDir(posDir, newDirection);
                    }
                } else {
                    setNewDir(posDir, newDirection);
                }
            }
        } else {
            intersected = false;
        }
	}

    public void reverseDir() {
        /**
         * 
         * Denne metoden skulle snu retningen når State i spillet endret seg. Fjernet da vi ikke fikk bevegelsesmønsteret helt i orden.

=======
		int newDirection = Random.Range (0, posDir.Count);

		if (posDir.Count > 0) {
			if (checkDir (tempDir)) {
				setNewDir(posDir, newDirection);
			} else {
				int directionChangeChance = Random.Range(1, 11);
                   
                if (directionChangeChance >= 1 && directionChangeChance <= 5) {
					if (!(checkDir (tempDir))) {
						intersected = true;
					} else {
						setNewDir(posDir, newDirection);
					}
				} else {
					setNewDir(posDir, newDirection);
				}
			}
		} else {
			intersected = false;	
		}
	}

    public void reverseDir() {
>>>>>>> Stashed changes
        if (myDirection == GameManager.Direction.North) {
            myDirection = GameManager.Direction.South;
        } else if (myDirection == GameManager.Direction.South) {
            myDirection = GameManager.Direction.North;
        } else if (myDirection == GameManager.Direction.East) {
            myDirection = GameManager.Direction.West;
        } else if (myDirection == GameManager.Direction.West) {
            myDirection = GameManager.Direction.East;
        } else {
            myDirection = GameManager.Direction.North;
        }

        if (intersected == true) {
            intersectionDelay = 0;
            intersected = false;
        }
<<<<<<< Updated upstream
         **/
=======
>>>>>>> Stashed changes
    }

    private void checkDirectionPossible(ArrayList posDir, Vector3 leftOffset, Vector3 rightOffset, Vector3 temp, GameManager.Direction currentDir, GameManager.Direction oppositeDir) {
        Debug.DrawRay(leftOffset, temp * 1.0f, Color.white);
        if (genHit(leftOffset, temp, levelLayer) && (myDirection != currentDir && myDirection != oppositeDir)) {
            Debug.DrawRay(rightOffset, temp * 1.0f, Color.white);
            if (genHit(rightOffset, temp, levelLayer) && (myDirection != currentDir && myDirection != oppositeDir)) {
                posDir.Add(currentDir);
            }
        }
    }

	private bool genHit(Vector3 offset, Vector3 temp, LayerMask levelLayer) {
		RaycastHit hit;
		Physics.Raycast (offset, temp, out hit, LEVEL_SIZE, levelLayer);
		
		if (hit.distance > 3.0f) {
			return true;		
		} else {
			return false;
		}
	}

	private void setNewDir (ArrayList posDir, int newDirection) {
        if (!(checkDir((GameManager.Direction)(posDir[newDirection])))) {
            myDirection = (GameManager.Direction)(posDir[newDirection]);
			intersected = true;
		}
	}

    private bool checkDir(GameManager.Direction dir) {
		Vector3 temp = genTemp(dir);
		RaycastHit hit;

		if (Physics.Raycast (transform.position, temp, out hit, CHECK_DIST)) {
			if (hit.collider.tag == "Level") {
				return true;		
			} else {
				return false;	
			}
		} else {
			return false;		
		}
	}

    private Vector3 genTemp(GameManager.Direction dir) {
		Vector3 temp = transform.position;
		
		if (dir == GameManager.Direction.North) {
			temp.z = temp.z + LEVEL_SIZE;
		} else if (dir == GameManager.Direction.South) {
			temp.z = temp.z - LEVEL_SIZE;
		} else if (dir == GameManager.Direction.East) {
			temp.x = temp.x + LEVEL_SIZE;
		} else if (dir == GameManager.Direction.West) {
			temp.x = temp.x - LEVEL_SIZE;
		}
		
		return temp;
	}

	public void switchVulnerable () {
		if (!(vulnerable)) {
			vulnerable = true;
			vulnTime = Time.realtimeSinceStartup;
		} else {
			vulnerable = false;	
		}
	}

	private void reset() {
		resetPos ();
		startDir();

		if (vulnerable == true) {
			switchVulnerable();
		}

		spawnTime = Time.realtimeSinceStartup;
		activated = false;

		gameObject.SetActive(true);
	}

	public void playerDied() {
		resetPos ();
		spawnTime = Time.realtimeSinceStartup;
		activated = false;
	}

	private void resetPos() {
		pos = originalPos;
		transform.position = pos;
	}

	private void OnTriggerEnter (Collider collision) {
		if (collision.tag == "Player" && vulnerable == true) {
			gameObject.SetActive (false);
			AudioSource.PlayClipAtPoint (deathGhost, Camera.main.transform.position);
			
			gameManager.GhostCollision (true);
			Invoke ("reset", 5.0f);
		} else if (collision.tag == "Player" && vulnerable == false) {
			AudioSource.PlayClipAtPoint (deathPacMan, Camera.main.transform.position);
			gameManager.GhostCollision (false);
		} else if (collision.tag == "Bullet") {
			reset ();
		}
	}

	private void OnTriggerExit (Collider collision) {
		if (collision.tag == "RespawnWest" || collision.tag == "RespawnEast") {
			if (teleported == false) {
				if (collision.tag == "RespawnWest") {
					pos.x -= 65.0f;		
					teleported = true;
				} else if (collision.tag == "RespawnEast") {
					pos.x += 65.0f;		
					teleported = true;
				}	
			} else {
				teleported = false;		
			}
		}
	}
}

/**
 * if (posDir.Count > 0) {
            if (currentState == GameManager.GAME_STATE.SCATTER) {
                createAngle(homeCornerPos, tempDir, posDir);
            } else if (currentState == GameManager.GAME_STATE.CHASE) {
                createAngle(pacManPos, tempDir, posDir);
            }

            // Debug.Log(name + " Pac man is in: " + pacManPos + " I am in: " + ownPos);
        }
 * 
 * 
    private void createAngle(Vector3 testPos, GameManager.Direction tempDir, ArrayList posDir) {
        var angle = Vector3.Angle(new Vector3(transform.position.x, transform.position.y, 500.0f), testPos);
        float refAngle;
        if (transform.position.z > 0) {
            if (AngleDir(new Vector3(transform.position.x, transform.position.y, 500.0f), testPos) == 1)
            {
                refAngle = 360 - angle;
            } else {
                refAngle = angle;
            }
        } else {
            if (AngleDir(new Vector3(transform.position.x, transform.position.y, 500.0f), testPos) == -1)
            {
                refAngle = 360 - angle;
            } else {
                refAngle = angle;
            }
        }

        Debug.Log(transform.position + " " + pacManPos + " " + name + " " + refAngle);

        if (!(checkDir(tempDir))) {
            posDir.Add(tempDir);
        }

        checkAngle(posDir, refAngle);
    }

    private void checkAngle(ArrayList posDir, float refAngle) {
        foreach (GameManager.Direction testDir in posDir) {
            if (testDir == GameManager.Direction.North && intersected == false) {
                // Debug.Log(name + "It is northward");
                if ((refAngle >= 315 && refAngle < 360) || (refAngle > 0 && refAngle <= 45)) {
                    setNewDir(testDir);
                }
            }

            if (testDir == GameManager.Direction.South && intersected == false) {
                // Debug.Log(name + "It is southward");
                if (refAngle <= 225 || refAngle > 135) {
                    setNewDir(testDir);
                }
            }

            if (testDir == GameManager.Direction.West && intersected == false) {
                // Debug.Log(name + "It is westward");
                if (refAngle < 315 || refAngle > 225) {
                    setNewDir(testDir);
                }
            }

            if (testDir == GameManager.Direction.East && intersected == false) {
                // Debug.Log(name + "It is eastward");
                if (refAngle <= 135 || refAngle > 45) {
                    setNewDir(testDir);
                }
            }

            if (intersected == false && posDir.Count > 0) {
                int newDirection = Random.Range(0, posDir.Count);

                myDirection = (GameManager.Direction) (posDir[newDirection]);
            }
        }
    }

    private void setNewDir(GameManager.Direction testDir) {
        if (!(checkDir(testDir))) {
            myDirection = testDir;
            intersected = true;
        }
    }

    private float AngleDir(Vector3 fwd, Vector3 targetDir) {
        Vector3 pos = Vector3.Cross(fwd, targetDir);
        float direction = Vector3.Dot(pos, Vector3.up);

        if (direction > 0.0f) {
            return -1.0f;
        } else if (direction < 0.0) {
            return 1.0f;
        } else {
            return 0.0f;
        }
    }
 * 
 **/


